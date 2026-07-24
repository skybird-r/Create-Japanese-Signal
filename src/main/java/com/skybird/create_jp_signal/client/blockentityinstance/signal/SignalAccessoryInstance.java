package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SignalAccessoryInstance {

    private final List<SignalModelData> staticParts = new ArrayList<>();
    private final SignalInstanceManager materialManager;
    private SignalHead signalHead;

    private final List<Vec3> mastCouplerPositions = new ArrayList<>();

    public SignalAccessoryInstance(SignalInstanceManager materialManager, SignalHead signalHead) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
    }

    // offset済み
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        this.signalHead = signalHead;
        delete();

        {
            ms.pushPose();
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(offset.x, offset.y, offset.z);
            SignalAccessory.Type type = null;
            if (signalHead != null) {
                type = signalHead.getAppearance().getAccessory().getType();
            }

            switch (type) {
                case FORECAST -> {
                    SignalModelData forecast = materialManager.create(PartialModelRegistry.ROUTE_FORECAST_CASING);
                    staticParts.add(forecast);
                    {
                        ms.pushPose();
                        ms.translate(0, -8.0/16, 0);
                        ms.translate(-0.5, -0.5, -0.5);
                        forecast.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -10.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_HOME -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_HOME_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -16.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -18.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_DEPARTURE -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_DEPARTURE_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -11.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -13.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_SHUNT -> {
                    SignalModelData indicator = materialManager.create(PartialModelRegistry.ROUTE_INDICATOR_SHUNT_CASING);
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        ms.translate(0, -8.0/16, 0);
                        ms.translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -10.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
            }
            ms.popPose();
        }
    }

    public void updateLight(Level level, BlockPos pos) {
        staticParts.forEach(model -> model.updateLight(level, pos));
    }

    public void delete() {
        staticParts.forEach(SignalModelData::delete);
        staticParts.clear();
        mastCouplerPositions.clear();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        staticParts.forEach(model -> consumer.accept(model.instance()));
    }

    public List<Vec3> getMastCouplerPositions() {
        return mastCouplerPositions;
    }
}
