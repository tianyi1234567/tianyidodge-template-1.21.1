package com.tianyi.tianyidodge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.tianyi.tianyidodge.TianyiDodge;
import com.tianyi.tianyidodge.Config;
import com.tianyi.tianyidodge.client.clientPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.lwjgl.glfw.GLFW;

//闪避的实现类
@EventBusSubscriber(modid = TianyiDodge.MODID, value = Dist.CLIENT)
public class DodgeKeyHandler {
    private static final String KEY_CATEGORY = "key.categories.tianyi_dodge";
    private static final String DODGE_KEY = "key.tianyi_dodge.dodge";

    public static final KeyMapping DODGE_MAPPING = new KeyMapping(
            DODGE_KEY,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_CATEGORY
    );

    private static int dodgeCooldown = 0;
    private static int invulnerabilityTicks = 0;
    private static int dodgeTicks = 0;
    private static Vec3 dodgeDirection = null;
    private static double dodgeDistance = 0;


    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(DODGE_MAPPING);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS && DODGE_MAPPING.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && dodgeCooldown <= 0 && !player.isSpectator()) {
                performDodge(player);
            }
        }
    }
    //平滑闪避效果的方法（这个实现方式比原版的加速好）
    private static void performDodge(Player player) {
        //获取玩家当前按下的移动键
        Minecraft mc = Minecraft.getInstance();
        boolean forward = mc.options.keyUp.isDown();
        boolean backward = mc.options.keyDown.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();

        //根据按下的键确定闪避方向
        double x = 0;
        double z = 0;
        String direction = "forward";//如果什么都不按，就放这个

        //获取玩家视角角度，用于计算正确的左右方向
        float yRot = (float) Math.toRadians(player.getYRot());

        //设置优先级（这里用AI写了，太麻烦）
        if (forward) {
            //前进方向：根据玩家视角计算
            x = -Math.sin(yRot);
            z = Math.cos(yRot);
            direction = "forward";
        } else if (backward) {
            //后退方向：与前进方向相反
            x = Math.sin(yRot);
            z = -Math.cos(yRot);
            direction = "backward";
        }

        if (left) {
            // 左移方向：垂直于视角方向
            if (!forward && !backward) {
                // 只有左右键时，使用纯粹的左右方向
                x = Math.cos(yRot);
                z = Math.sin(yRot);
                direction = "left";
            } else {
                // 前后键也按下时，添加左右分量
                x += Math.cos(yRot) * 0.707; // 0.707 ≈ 1/√2，用于斜向移动的归一化
                z += Math.sin(yRot) * 0.707;
            }
        } else if (right) {
            // 右移方向：与左移方向相反
            if (!forward && !backward) {
                // 只有左右键时，使用纯粹的左右方向
                x = -Math.cos(yRot);
                z = -Math.sin(yRot);
                direction = "right";
            } else {
                // 前后键也按下时，添加左右分量
                x += -Math.cos(yRot) * 0.707;
                z += -Math.sin(yRot) * 0.707;
            }
        }

        // 如果没有按下任何移动键，使用玩家朝向
        if (x == 0 && z == 0) {
            x = -Math.sin(yRot);
            z = Math.cos(yRot);
            direction = "forward"; // 默认向前动画
        }

        // 归一化移动向量（如果同时按下多个键的计算，有了这个是真的丝滑）
        double length = Math.sqrt(x * x + z * z);
        if (length > 0) {
            x /= length;
            z /= length;
        }

        //计算闪避距离（默认配置值+1，因为平移计算方法有点小问题，不过不管，能跑就行）
        double distance = Config.DODGE_DISTANCE.get() + 1.0;

        double initialSpeed = distance / 4.0; //在4个tick内完成闪避（虽然短，但是不用太担心动画的问题）

        //设置新的速度向量，保持Y轴不变（防止闪避期间落地上减速，但是也会造成空中闪避落不下来，后面再改吧）
        Vec3 currentMovement = player.getDeltaMovement();
        Vec3 dodgeVelocity = new Vec3(x * initialSpeed, currentMovement.y, z * initialSpeed);
        player.setDeltaMovement(dodgeVelocity);

        dodgeDirection = new Vec3(x, 0, z);
        dodgeDistance = distance;
        dodgeTicks = 4; // 闪避持续时间（tick数）

        //冷却
        dodgeCooldown = Config.DODGE_COOLDOWN.get();
        invulnerabilityTicks = Config.INVULNERABILITY_TICKS.get(); //无敌帧时间

        //闪避的粒子效果
        if (Config.DODGE_PARTICLE_EFFECT.get()) {
            Vec3 playerPos = player.position();
            for (int i = 0; i < 25; i++) {//数量
                double offsetX = (player.getRandom().nextDouble() - 0.5) * 1.0;
                double offsetY = player.getRandom().nextDouble() * 1.35; //粒子起始的位置
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 1.0;

                player.level().addParticle(
                    ParticleTypes.PORTAL,
                    playerPos.x + offsetX,
                    playerPos.y + offsetY,
                    playerPos.z + offsetZ,
                    0,    //水平移动
                    -0.2, //下落的速度
                    0     //和x轴一样
                );
            }
        }

        //传送声音
        if (Config.DODGE_SOUND_EFFECT.get()) {
            Vec3 playerPos = player.position();
            //用客户端播放服务端不知道为什么没有声音
            Minecraft.getInstance().player.playSound(
                SoundEvents.ENDERMAN_TELEPORT, //传送声音设置，后面自己注册一个新音效，暂时用末影人的
                0.45f, //音量
                1.0f  //音调
            );
        }

        //播放对应方向的闪避动画
        clientPlayer.playDodgeAnimation((net.minecraft.client.player.AbstractClientPlayer) player, direction);
        //测试闪避生没生效的
//        TianyiDodge.LOGGER.info("我向{}方向闪避啦！: {} ticks", direction, invulnerabilityTicks);
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Pre event) {//闪避方式，让AI写的，这玩意真的麻烦
        //只在客户端处理
        if (event.getEntity().level().isClientSide() && event.getEntity()
                instanceof Player player) {

            //减少冷却时间
            if (dodgeCooldown > 0) {
                dodgeCooldown--;
            }

            //处理闪避移动
            if (dodgeTicks > 0 && dodgeDirection != null) {
                // 使用恒定的速度，确保总移动距离等于配置值
                // 计算每帧需要移动的距离：总距离 / 剩余tick数
                double distancePerTick = dodgeDistance / 4.0;

                // 使用速度而不是直接设置位置，以确保碰撞检测正常工作

                // 计算新的速度向量，只设置水平方向的速度
                Vec3 newMovement = new Vec3(
                    dodgeDirection.x * distancePerTick,
                        dodgeDirection.y * distancePerTick,
//                    0, // Y轴速度设为0，防止跳跃或下落影响闪避（这里我改了，因为没有必要设置，也没效果）
                    dodgeDirection.z * distancePerTick
                );

                // 简单检查碰撞 - 只在下一帧位置有方块阻挡时才调整
                Vec3 currentPos = player.position();
                Vec3 nextPos = currentPos.add(newMovement);

                // 只检查目标位置是否有实体方块，忽略空气和其他非实体方块
                if (!player.level().getBlockState(net.minecraft.core.BlockPos.containing(nextPos.x, nextPos.y, nextPos.z)).isAir()) {
                    // 如果目标位置有方块，只移动一小段距离
                    newMovement = new Vec3(
                        dodgeDirection.x * 0.2,
                        0,
                        dodgeDirection.z * 0.2
                    );
                }

                // 设置速度而不是直接设置位置，让游戏引擎处理碰撞检测（自己写的会鬼畜）
                player.setDeltaMovement(newMovement);

                //禁用原版动画系统（不然会鬼畜！）
                if (player instanceof net.minecraft.client.player.AbstractClientPlayer) {
                    net.minecraft.client.player.AbstractClientPlayer clientPlayer = (net.minecraft.client.player.AbstractClientPlayer) player;
                    //保存原始数据，如果还没保存的话
                    if (!clientPlayer.getPersistentData().contains("tianyi_dodge_original_noGravity")) {
                        clientPlayer.getPersistentData().putBoolean("tianyi_dodge_original_noGravity", clientPlayer.isNoGravity());
                        clientPlayer.getPersistentData().putBoolean("tianyi_dodge_original_onGround", clientPlayer.onGround());
                    }

                    //在闪避期间禁用重力和地面检测（重力给了设置，但是没效果，之后再修吧）
                    if (Config.DODGE_DISABLE_GRAVITY.get()) {
                        clientPlayer.setNoGravity(true);
                    }
                    clientPlayer.setOnGround(true);

                    //重置玩家姿势，防止原版动画系统触发，但是也会导致归位且有点问题
                    clientPlayer.setPose(net.minecraft.world.entity.Pose.STANDING);
                }

                // 减少剩余闪避时间
                dodgeTicks--;

                // 闪避结束时重置方向
                if (dodgeTicks == 0) {
                    // 在重置方向前保存闪避方向，用于计算粒子位置
                    Vec3 savedDodgeDirection = dodgeDirection;

                    dodgeDirection = null;
                    // 恢复正常移动速度
                    player.setDeltaMovement(0, player.getDeltaMovement().y, 0);

                    // 恢复原版的动画
                    if (player instanceof net.minecraft.client.player.AbstractClientPlayer) {
                        net.minecraft.client.player.AbstractClientPlayer clientPlayer = (net.minecraft.client.player.AbstractClientPlayer) player;
                        // 恢复原始设置（之前禁用了重力的话，这个必不可少）
                        if (clientPlayer.getPersistentData().contains("tianyi_dodge_original_noGravity")) {
                            if (Config.DODGE_DISABLE_GRAVITY.get()) {
                                clientPlayer.setNoGravity(clientPlayer.getPersistentData().getBoolean("tianyi_dodge_original_noGravity"));
                            }
                            clientPlayer.setOnGround(clientPlayer.getPersistentData().getBoolean("tianyi_dodge_original_onGround"));

                            // 清除保存的数据
                            clientPlayer.getPersistentData().remove("tianyi_dodge_original_noGravity");
                            clientPlayer.getPersistentData().remove("tianyi_dodge_original_onGround");
                        }
                    }
                }
            }

            //处理无敌帧
            if (invulnerabilityTicks > 0) {
                invulnerabilityTicks--;
                player.invulnerableTime = invulnerabilityTicks;

                //当无敌帧结束时，停止动画（实际没啥用）
                if (invulnerabilityTicks == 0) {
                    clientPlayer.stopDodgeAnimation((net.minecraft.client.player.AbstractClientPlayer) player);
                }
            }
        }
    }
}
