package com.skybird.create_jp_signal.client;

import javax.annotation.Nonnull;

import com.jozufozu.flywheel.core.PartialModel;
import com.skybird.create_jp_signal.JpSignals;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = JpSignals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PartialModelRegistry {
    public static final PartialModel SPEED_LIMIT_MARKER = block("track_overlay/speed_limit_marker");

    private static PartialModel block(@Nonnull String path) {
        return new PartialModel(JpSignals.asResource("block/" + path));
    }
}
