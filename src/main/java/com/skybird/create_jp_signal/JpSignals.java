package com.skybird.create_jp_signal;

import com.mojang.logging.LogUtils;
import com.skybird.create_jp_signal.network.PacketHandler;

import org.slf4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JpSignals.MODID)
public class JpSignals {

    public static final String MODID = "create_jp_signal";

    public static final Logger LOGGER = LogUtils.getLogger();

    public JpSignals() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AllCreativeTabs.TABS.register(bus);
        AllBlocks.BLOCKS.register(bus);
        AllBlocks.ITEMS.register(bus);
        AllItems.ITEMS.register(bus);
        AllBlockEntities.BLOCK_ENTITIES.register(bus);
        AllMenuTypes.MENUS.register(bus);
        PacketHandler.init();

    }

}
