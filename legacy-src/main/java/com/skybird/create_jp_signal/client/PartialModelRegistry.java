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

    public static final PartialModel REPEATER_SIGNAL_CASING = block("signal_casing/repeater_signal_casing");
    public static final PartialModel REPEATER_SIGNAL_UPPER_CASING = block("signal_casing/repeater_signal_upper_casing");
    public static final PartialModel REPEATER_SIGNAL_TUNNEL_CASING = block("signal_casing/repeater_signal_tunnel_casing");
    public static final PartialModel REPEATER_SIGNAL_TUNNEL_UPPER_CASING = block("signal_casing/repeater_signal_tunnel_upper_casing");
    public static final PartialModel SHUNT_2_SIGNAL_CASING = block("signal_casing/shunt_2_signal_casing");
    public static final PartialModel SHUNT_3_SIGNAL_CASING = block("signal_casing/shunt_3_signal_casing");

    public static final PartialModel BACKPLATE_MIDDLE = block("signal_casing/backplate_middle");
    public static final PartialModel BACKPLATE_BOTTOM_ROUND = block("signal_casing/backplate_bottom_round");
    public static final PartialModel BACKPLATE_BOTTOM_SQUARE = block("signal_casing/backplate_bottom_square");
    public static final PartialModel ROUTE_INDICATOR_HOME_CASING = block("signal_casing/route_indicator_casing");
    public static final PartialModel ROUTE_INDICATOR_DEPARTURE_CASING = block("signal_casing/route_indicator_departure_casing");
    public static final PartialModel ROUTE_INDICATOR_SHUNT_CASING = block("signal_casing/route_indicator_shunt_casing");
    public static final PartialModel ROUTE_FORECAST_CASING = block("signal_casing/route_forecast_casing");
    
    public static final PartialModel MAST_COUPLER = block("signal_parts/mast_coupler");
    public static final PartialModel MAST_PIPE = block("signal_parts/mast_pipe");
    public static final PartialModel SIGNAL_JOINT = block("signal_parts/signal_joint");

    private static PartialModel block(@Nonnull String path) {
        return new PartialModel(JpSignals.asResource("block/" + path));
    }
}
