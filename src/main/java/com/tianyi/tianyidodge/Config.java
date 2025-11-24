package com.tianyi.tianyidodge;

import net.neoforged.neoforge.common.ModConfigSpec;

// 闪避模组的配置类
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 闪避冷却时间（tick）
    public static final ModConfigSpec.IntValue DODGE_COOLDOWN = BUILDER
            .comment("闪避技能的冷却时间（单位：tick，20tick=1秒）")
            .defineInRange("dodgeCooldown", 20, 0, 1024);
    
    // 闪避距离
    public static final ModConfigSpec.DoubleValue DODGE_DISTANCE = BUILDER
            .comment("闪避的距离")
            .defineInRange("dodgeDistance", 4.0, 1.0, 20.0);
    
    // 无敌帧持续时间（tick）
    public static final ModConfigSpec.IntValue INVULNERABILITY_TICKS = BUILDER
            .comment("闪避后的无敌时间（单位：tick，20tick=1秒）")
            .defineInRange("invulnerabilityTicks", 10, 0, 100);

    static final ModConfigSpec SPEC = BUILDER.build();
}
