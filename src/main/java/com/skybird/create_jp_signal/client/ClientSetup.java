package com.skybird.create_jp_signal.client;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.client.blockentityrenderer.Signal3LBlockEntityRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.BaseSignalBlockEntityRenderer;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.SignalMastBlockEntityRenderer;
import com.skybird.create_jp_signal.client.gui.ControlBoxScreen;
import com.skybird.create_jp_signal.client.gui.MastConfigScreen;
import com.skybird.create_jp_signal.client.gui.SignalLinkScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = JpSignals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Signal3LBlockEntityが描画されるときに、Signal3LBlockEntityRendererを使うように登録
            BlockEntityRenderers.register(
                AllBlockEntities.SIGNAL_3L_ENTITY.get(),
                Signal3LBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.SIGNAL_MAST_ENTITY.get(), 
                SignalMastBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.COLOR_SINGLE_ROUND_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.COLOR_SINGLE_SQUARE_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.COLOR_SINGLE_TUNNEL_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.REPEATER_SINGLE_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.REPEATER_SINGLE_TUNNEL_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );
            BlockEntityRenderers.register(
                AllBlockEntities.SHUNT_SINGLE_SIGNAL_MAST_ENTITY.get(), 
                BaseSignalBlockEntityRenderer::new
            );

            MenuScreens.register(AllMenuTypes.CONTROL_BOX_MENU.get(), ControlBoxScreen::new);
            MenuScreens.register(AllMenuTypes.MAST_CONFIG_MENU.get(), MastConfigScreen::new);
            MenuScreens.register(AllMenuTypes.SIGNAL_LINK_MENU.get(), SignalLinkScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(JpSignals.MODID, "block/light"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/light_green"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/light_yellow"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/light_red"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/light_purple"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_mast"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/white_light"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_5x5"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_4x4"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_3x3"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/lampbox/lampbox_2x2"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_middle"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_bottom_square"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/backplate_bottom_round"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_indicator_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_indicator_shunt_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_indicator_departure_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/route_forecast_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/repeater_signal_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/repeater_signal_upper_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/repeater_signal_tunnel_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/repeater_signal_tunnel_upper_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/shunt_2_signal_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_casing/shunt_3_signal_casing"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_parts/mast_coupler"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_parts/mast_pipe"));
        event.register(new ResourceLocation(JpSignals.MODID, "block/signal_parts/signal_joint"));


        
    }

    
}