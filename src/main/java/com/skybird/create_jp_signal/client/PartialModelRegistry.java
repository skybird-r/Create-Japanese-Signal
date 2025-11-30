package com.skybird.create_jp_signal.client;

import javax.annotation.Nonnull;

import com.jozufozu.flywheel.core.PartialModel;
import com.skybird.create_jp_signal.JpSignals;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = JpSignals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PartialModelRegistry {

    public static final PartialModel SPEED_LIMIT_MARKER = block("track_overlay/speed_limit_marker");

    public static final PartialModel SIGNAL_MAST = block("signal_mast");

    public static final PartialModel SIGNAL_LIGHT = block("white_light");
    public static final PartialModel LAMPBOX_5x5 = block("lampbox/lampbox_5x5");
    public static final PartialModel LAMPBOX_4x4 = block("lampbox/lampbox_4x4");
    public static final PartialModel LAMPBOX_3x3 = block("lampbox/lampbox_3x3");
    public static final PartialModel LAMPBOX_2x2 = block("lampbox/lampbox_2x2");


    public static final PartialModel BACKPLATE_MIDDLE = block("signal_casing/backplate_middle");
    public static final PartialModel BACKPLATE_BOTTOM_ROUND = block("signal_casing/backplate_bottom_round");
    public static final PartialModel BACKPLATE_BOTTOM_SQUARE = block("signal_casing/backplate_bottom_round");

    private static PartialModel block(@Nonnull String path) {
        return new PartialModel(JpSignals.asResource("block/" + path));
    }
}
