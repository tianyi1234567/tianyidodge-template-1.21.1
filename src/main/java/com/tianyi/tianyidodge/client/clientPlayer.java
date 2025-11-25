package com.tianyi.tianyidodge.client;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import static com.tianyi.tianyidodge.TianyiDodge.MODID;

public class clientPlayer {
    
    private static final int DODGE_ANIM_PRIORITY = 42;

    
    // 播放闪避动画
    public static void playDodgeAnimation(AbstractClientPlayer player) {
        try {
            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(player);
            ModifierLayer<IAnimation> playerAnimation = new ModifierLayer<>();
            playerAnimation.setAnimation(PlayerAnimationRegistry
                .getAnimation(ResourceLocation.fromNamespaceAndPath(MODID, "tianyi_dodge_left"))
                .playAnimation());

            animationStack.addAnimLayer(DODGE_ANIM_PRIORITY, playerAnimation);
        } catch (Exception e) {
            // 如果动画停止失败，保证游戏不会崩溃
            e.printStackTrace();
        }
    }
    
    // 停止闪避动画
    public static void stopDodgeAnimation(AbstractClientPlayer player) {
        try {
            AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(player);
            stack.removeLayer(DODGE_ANIM_PRIORITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
