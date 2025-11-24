package com.tianyi.tianyidodge;

import com.tianyi.tianyidodge.client.DodgeKeyHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = TianyiDodge.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TianyiDodge.MODID, value = Dist.CLIENT)
public class TianyiDodgeClient {
    public TianyiDodgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        TianyiDodge.LOGGER.info("HELLO FROM CLIENT SETUP");
        TianyiDodge.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        
        // 按键处理器通过@EventBusSubscriber自动注册，无需手动注册
    }
}