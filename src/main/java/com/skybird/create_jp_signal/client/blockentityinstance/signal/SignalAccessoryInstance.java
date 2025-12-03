package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.BasicData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SignalAccessoryInstance {

    private final List<ModelData> staticParts = new ArrayList<>();
    private final MaterialManager materialManager;
    private SignalHead signalHead;

    private final List<Vec3> mastCouplerPositions = new ArrayList<>();

    public SignalAccessoryInstance(MaterialManager materialManager, SignalHead signalHead) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
    }

    // offset済み
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        this.signalHead = signalHead;
        delete();

        TransformStack msr = TransformStack.cast(ms);
        {
            ms.pushPose();
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst()).translate(offset);//.rotateX(rotation.getSecond());
            SignalAccessory.Type type = null;
            if (signalHead != null) {
                type = signalHead.getAppearance().getAccessory().getType();
            }

            switch (type) {
                case FORECAST -> {
                    ModelData forecast = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(PartialModelRegistry.ROUTE_FORECAST_CASING)
                        .createInstance();
                    staticParts.add(forecast);
                    {
                        ms.pushPose();
                        msr.translate(0, -8.0/16, 0).unCentre();
                        forecast.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -10.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_HOME -> {
                    ModelData indicator = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(PartialModelRegistry.ROUTE_INDICATOR_HOME_CASING)
                        .createInstance();
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        msr.translate(0, -16.0/16, 0).translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -18.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_DEPARTURE -> {
                    ModelData indicator = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(PartialModelRegistry.ROUTE_INDICATOR_DEPARTURE_CASING)
                        .createInstance();
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        msr.translate(0, -11.0/16, 0).translate(-0.5, -1.0/16, -0.5);
                        indicator.setTransform(ms);
                        ms.popPose();
                    }
                    mastCouplerPositions.add(new Vec3(offset.x, -13.0/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                }
                case INDICATOR_SHUNT -> {
                    ModelData indicator = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(PartialModelRegistry.ROUTE_INDICATOR_SHUNT_CASING)
                        .createInstance();
                    staticParts.add(indicator);
                    {
                        ms.pushPose();
                        msr.translate(0, -8.0/16, 0).translate(-0.5, -1.0/16, -0.5);
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
        staticParts.forEach(ModelData::delete);
        staticParts.clear();
        mastCouplerPositions.clear();
    }

    public List<Vec3> getMastCouplerPositions() {
        return mastCouplerPositions;
    }
}
