package com.tianyi.tianyidodge;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TianyiDodge.MODID)
public class TianyiDodge {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tianyi_dodge";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public TianyiDodge(IEventBus modEventBus, ModContainer modContainer) {
        // 注册配置
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}