package com.skybird.create_jp_signal.client;

import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = JpSignals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegistry {

    // --- ResourceLocations ---
    private static final ResourceLocation SIGNAL_MAST_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_mast");

    public static final ResourceLocation LIGHT_LOC = new ResourceLocation(JpSignals.MODID, "block/white_light");
    public static final ResourceLocation LAMPBOX_5x5_LOC = new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_5x5");
    public static final ResourceLocation LAMPBOX_4x4_LOC = new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_4x4");
    public static final ResourceLocation LAMPBOX_3x3_LOC = new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_3x3");
    public static final ResourceLocation BACKPLATE_MIDDLE_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_middle");
    public static final ResourceLocation BACKPLATE_BOTTOM_ROUND_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_bottom_round");
    public static final ResourceLocation BACKPLATE_BOTTOM_SQUARE_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_bottom_square");
    public static final ResourceLocation ROUTE_INDICATOR_CASING_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_indicator_casing");
    public static final ResourceLocation ROUTE_INDICATOR_DEPARTURE_CASING_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_indicator_departure_casing");
    public static final ResourceLocation ROUTE_FORECAST_CASING_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_forecast_casing");
    public static final ResourceLocation REPEATER_SIGNAL_CASING_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_casing/repeater_signal_casing");
    public static final ResourceLocation MAST_COUPLER_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_parts/mast_coupler");
    public static final ResourceLocation MAST_PIPE_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_parts/mast_pipe");
    public static final ResourceLocation SIGNAL_JOINT_LOC = new ResourceLocation(JpSignals.MODID, "block/signal_parts/signal_joint");
    
    

    // --- BakedModels ---
    public static BakedModel signalMast;

    public static BakedModel light;
    public static BakedModel lampBox5;
    public static BakedModel lampBox4;
    public static BakedModel lampBox3;
    public static BakedModel backplateMiddle;
    public static BakedModel backplateBottomRound;
    public static BakedModel backplateBottomSquare;
    public static BakedModel routeIndicatorCasing;
    public static BakedModel routeIndicatorDepartureCasing;
    public static BakedModel routeForecastCasing;
    public static BakedModel repeaterSignalCasing;
    public static BakedModel mastCoupler;
    public static BakedModel mastPipe;
    public static BakedModel signalJoint;


    
    @SubscribeEvent
    public static void onModelBake(ModelEvent.BakingCompleted event) {
        signalMast = event.getModelManager().getModel(SIGNAL_MAST_LOC);
        lampBox5 = event.getModelManager().getModel(LAMPBOX_5x5_LOC);
        lampBox4 = event.getModelManager().getModel(LAMPBOX_4x4_LOC);
        lampBox3 = event.getModelManager().getModel(LAMPBOX_3x3_LOC);
        light = event.getModelManager().getModel(LIGHT_LOC);
        backplateMiddle = event.getModelManager().getModel(BACKPLATE_MIDDLE_LOC);
        backplateBottomRound = event.getModelManager().getModel(BACKPLATE_BOTTOM_ROUND_LOC);
        backplateBottomSquare = event.getModelManager().getModel(BACKPLATE_BOTTOM_SQUARE_LOC);
        routeIndicatorCasing = event.getModelManager().getModel(ROUTE_INDICATOR_CASING_LOC);
        routeIndicatorDepartureCasing = event.getModelManager().getModel(ROUTE_INDICATOR_DEPARTURE_CASING_LOC);
        routeForecastCasing = event.getModelManager().getModel(ROUTE_FORECAST_CASING_LOC);
        repeaterSignalCasing = event.getModelManager().getModel(REPEATER_SIGNAL_CASING_LOC);
        mastCoupler = event.getModelManager().getModel(MAST_COUPLER_LOC);
        mastPipe = event.getModelManager().getModel(MAST_PIPE_LOC);
        signalJoint = event.getModelManager().getModel(SIGNAL_JOINT_LOC);
    }
    
}
