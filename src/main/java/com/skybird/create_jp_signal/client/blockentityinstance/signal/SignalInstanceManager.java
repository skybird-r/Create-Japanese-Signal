package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.LightShaders;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Small compatibility layer between the original Flywheel 0.6 signal code and
 * Flywheel 1.0's instancer API.
 */
final class SignalInstanceManager {

    private static final RendererReloadCache<PartialModel, Model> CUTOUT_MODELS =
        new RendererReloadCache<>(partial -> BakedModelBuilder.create(partial.get())
            .materialFunc((renderType, shaded) ->
                shaded ? Materials.CUTOUT_BLOCK : Materials.CUTOUT_UNSHADED_BLOCK)
            .build());

    private static final RendererReloadCache<PartialModel, Model> FULL_BRIGHT_CUTOUT_MODELS =
        new RendererReloadCache<>(partial -> BakedModelBuilder.create(partial.get())
            .materialFunc((renderType, shaded) -> SimpleMaterial.builderOf(Materials.CUTOUT_UNSHADED_BLOCK)
                // The default embedded light shader applies ambient occlusion on contraptions.
                // Flat lighting keeps the instance light value without multiplying in AO.
                .light(LightShaders.FLAT)
                .build())
            .build());

    private final InstancerProvider instancerProvider;

    SignalInstanceManager(InstancerProvider instancerProvider) {
        this.instancerProvider = instancerProvider;
    }

    SignalModelData create(PartialModel model) {
        TransformedInstance instance = instancerProvider
            .instancer(InstanceTypes.TRANSFORMED, CUTOUT_MODELS.get(model))
            .createInstance();
        return new SignalModelData(instance);
    }

    SignalModelData createFullBright(PartialModel model) {
        TransformedInstance instance = instancerProvider
            .instancer(InstanceTypes.TRANSFORMED, FULL_BRIGHT_CUTOUT_MODELS.get(model))
            .createInstance();
        instance.light(LightTexture.FULL_BRIGHT);
        instance.setChanged();
        return new SignalModelData(instance);
    }

    static final class SignalModelData {

        private final TransformedInstance instance;

        private SignalModelData(TransformedInstance instance) {
            this.instance = instance;
        }

        void setTransform(PoseStack poseStack) {
            instance.setTransform(poseStack).setChanged();
        }

        void updateLight(Level level, BlockPos pos) {
            instance.light(LevelRenderer.getLightColor(level, pos));
            instance.setChanged();
        }

        void setColor(LampColor color) {
            instance.color(color.getRed(), color.getGreen(), color.getBlue());
            instance.setChanged();
        }

        void delete() {
            instance.delete();
        }

        Instance instance() {
            return instance;
        }
    }
}
