package com.tianyi.tianyidodge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.tianyi.tianyidodge.TianyiDodge;
import com.tianyi.tianyidodge.Config;
import com.tianyi.tianyidodge.client.clientPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
    //AI写的临时移动方法，之后要换掉
    private static void performDodge(Player player) {
        // 获取玩家的移动方向
        Vec3 movement = player.getDeltaMovement();
        double x = movement.x;
        double z = movement.z;

        // 如果玩家没有移动，使用朝向
        if (Math.abs(x) < 0.1 && Math.abs(z) < 0.1) {
            float yRot = (float) Math.toRadians(player.getYRot());
            x = -Math.sin(yRot);
            z = Math.cos(yRot);
        } else {
            // 归一化移动向量
            double length = Math.sqrt(x * x + z * z);
            x /= length;
            z /= length;
        }

        //冲刺的速度
        double distance = Config.DODGE_DISTANCE.get();
        Vec3 dodgeVector = new Vec3(x * distance, player.getDeltaMovement().y, z * distance);
        player.setDeltaMovement(dodgeVector);

        //冷却
        dodgeCooldown = Config.DODGE_COOLDOWN.get();
        invulnerabilityTicks = 30; //无敌帧时间

        //播放闪避动画的调用
        clientPlayer.playDodgeAnimation((net.minecraft.client.player.AbstractClientPlayer) player);
        //测试闪避生没生效的
        TianyiDodge.LOGGER.info("我闪避啦！: {} ticks", invulnerabilityTicks);
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Pre event) {
        //只在客户端处理
        if (event.getEntity().level().isClientSide() && event.getEntity()
                instanceof Player player) {

            //减少冷却时间
            if (dodgeCooldown > 0) {
                dodgeCooldown--;
            }

            //处理无敌帧
            if (invulnerabilityTicks > 0) {
                invulnerabilityTicks--;
                player.invulnerableTime = invulnerabilityTicks;

                // 当无敌帧结束时，停止动画
                if (invulnerabilityTicks == 0) {
                    clientPlayer.stopDodgeAnimation((net.minecraft.client.player.AbstractClientPlayer) player);
                }
            }
        }
    }
}
