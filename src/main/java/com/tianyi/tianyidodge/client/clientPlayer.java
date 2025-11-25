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
    
    private static final int DODGE_ANIM_PRIORITY = 42;//动画等级（能打断小于自己的动画的等级）

    //闪避动画实现方法
    public static void playDodgeAnimation(AbstractClientPlayer player, String direction) {
        try {
            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(player);
            ModifierLayer<IAnimation> playerAnimation = new ModifierLayer<>();

            //根据方向选择不同的动画（这样写最省事）
            String animationName = "tianyi_dodge_" + direction;
            playerAnimation.setAnimation(PlayerAnimationRegistry
                .getAnimation(ResourceLocation.fromNamespaceAndPath(MODID, animationName))
                .playAnimation());

            animationStack.addAnimLayer(DODGE_ANIM_PRIORITY, playerAnimation);
        } catch (Exception e) {
            //如果动画播放失败，保证游戏不会崩溃（以防万一用的）
            e.printStackTrace();
        }
    }
    
    //停止闪避动画（防止崩溃）
    public static void stopDodgeAnimation(AbstractClientPlayer player) {
        try {
            AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(player);
            stack.removeLayer(DODGE_ANIM_PRIORITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
