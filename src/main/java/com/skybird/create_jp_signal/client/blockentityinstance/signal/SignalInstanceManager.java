package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Small compatibility layer between the original Flywheel 0.6 signal code and
 * Flywheel 1.0's instancer API.
 */
final class SignalInstanceManager {

    private final InstancerProvider instancerProvider;

    SignalInstanceManager(InstancerProvider instancerProvider) {
        this.instancerProvider = instancerProvider;
    }

    SignalModelData create(PartialModel model) {
        TransformedInstance instance = instancerProvider
            .instancer(InstanceTypes.TRANSFORMED, Models.partial(model))
            .createInstance();
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

        void delete() {
            instance.delete();
        }

        Instance instance() {
            return instance;
        }
    }
}
