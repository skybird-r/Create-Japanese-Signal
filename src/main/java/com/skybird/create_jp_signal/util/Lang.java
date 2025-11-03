package com.skybird.create_jp_signal.util;


import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Lang {
    public static MutableComponent translatable(String pKey) {
        return Component.translatable(JpSignals.MODID + "." + pKey);
    }

    public static MutableComponent translatable(String pKey, Object... pArgs) {
        return Component.translatable(JpSignals.MODID + "." + pKey, pArgs);
    }
}
