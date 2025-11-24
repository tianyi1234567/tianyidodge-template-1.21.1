package com.tianyi.tianyidodge;

import com.tianyi.tianyidodge.client.DodgeKeyHandler;
import com.tianyi.tianyidodge.client.clientPlayer;
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
        // 客户端设置
    }
}