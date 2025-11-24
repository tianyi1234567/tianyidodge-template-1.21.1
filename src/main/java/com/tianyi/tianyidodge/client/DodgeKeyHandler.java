package com.tianyi.tianyidodge.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.tianyi.tianyidodge.TianyiDodge;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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
    private static final int DODGE_COOLDOWN_TICKS = 20; // 1秒冷却
    private static final int DODGE_DISTANCE = 5; // 冲刺距离
    private static final int INVULNERABILITY_TICKS = 10; // 无敌帧持续时间(0.5秒)

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

        // 设置冲刺速度
        Vec3 dodgeVector = new Vec3(x * DODGE_DISTANCE, player.getDeltaMovement().y, z * DODGE_DISTANCE);
        player.setDeltaMovement(dodgeVector);

        // 设置冷却和无敌时间
        dodgeCooldown = DODGE_COOLDOWN_TICKS;
        invulnerabilityTicks = INVULNERABILITY_TICKS;

        TianyiDodge.LOGGER.info("闪避! 无敌时间: {} ticks", invulnerabilityTicks);
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Pre event) {
        // 只在客户端处理
        if (event.getEntity().level().isClientSide()) {
            Player player = (Player) event.getEntity();
            
            // 减少冷却时间
            if (dodgeCooldown > 0) {
                dodgeCooldown--;
            }
            
            // 处理无敌帧
            if (invulnerabilityTicks > 0) {
                invulnerabilityTicks--;
                player.invulnerableTime = invulnerabilityTicks;
            }
        }
    }
}
