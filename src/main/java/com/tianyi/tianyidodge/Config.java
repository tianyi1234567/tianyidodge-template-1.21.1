package com.tianyi.tianyidodge;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 闪避冷却时间
    public static final ModConfigSpec.IntValue DODGE_COOLDOWN = BUILDER
            .comment("闪避技能的冷却时间（单位：tick，20tick=1秒）")
            .defineInRange("dodgeCooldown", 20, 0, 1024);
    
    // 闪避距离
    public static final ModConfigSpec.DoubleValue DODGE_DISTANCE = BUILDER
            .comment("闪避的距离")
            .defineInRange("dodgeDistance", 3.0, 1.0, 20.0);
    
    // 无敌帧持续时间
    public static final ModConfigSpec.IntValue INVULNERABILITY_TICKS = BUILDER
            .comment("闪避后的无敌时间（单位：tick，20tick=1秒）")
            .defineInRange("invulnerabilityTicks", 10, 0, 100);

    // 末影人粒子效果开关
    public static final ModConfigSpec.BooleanValue DODGE_PARTICLE_EFFECT = BUILDER
            .comment("是否在闪避后显示粒子效果")
            .define("dodgeParticleEffect", true);

    // 末影人传送声音效果开关
    public static final ModConfigSpec.BooleanValue DODGE_SOUND_EFFECT = BUILDER
            .comment("是否在闪避时播放末影人传送的声音效果")
            .define("dodgeSoundEffect", true);

    // 闪避时禁用重力开关
    public static final ModConfigSpec.BooleanValue DODGE_DISABLE_GRAVITY = BUILDER
            .comment("是否在闪避时禁用重力")
            .define("dodgeDisableGravity", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
