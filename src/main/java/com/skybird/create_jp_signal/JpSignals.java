package com.skybird.create_jp_signal;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(JpSignals.MODID)
public class JpSignals {
    
    public static final String MODID = "create_jp_signal";

    public static final Logger LOGGER = LogUtils.getLogger();

    public JpSignals(IEventBus modEventBus, ModContainer modContainer) {

    }

}
