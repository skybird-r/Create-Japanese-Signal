package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.RepeaterForm;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightRepeaterSignalInstance extends SignalHeadInstance {

    private final List<ModelData> staticParts = new ArrayList<>();

    public PositionLightRepeaterSignalInstance(MaterialManager materialManager, SignalHead signalHead, BlockEntity be) {
        super(materialManager, signalHead, be);
    }
    
    @Override
    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof PositionLightRepeaterSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        TransformStack msr = TransformStack.cast(ms);
        {
            ms.pushPose();
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst()).translate(offset).rotateX(rotation.getSecond());

            PartialModel casingModel;
            PartialModel upperCasingModel;
            double modelHeight;
            double modelGap;
            double caseOffset;

            switch (appearance.getSignalSize()) {
                case NORMAL -> {
                    casingModel = PartialModelRegistry.REPEATER_SIGNAL_CASING;
                    upperCasingModel = PartialModelRegistry.REPEATER_SIGNAL_UPPER_CASING;
                    modelHeight = 14;
                    modelGap = 6;
                    caseOffset = 2;
                }
                case TUNNEL -> {
                    casingModel = PartialModelRegistry.REPEATER_SIGNAL_TUNNEL_CASING;
                    upperCasingModel = PartialModelRegistry.REPEATER_SIGNAL_TUNNEL_UPPER_CASING;
                    modelHeight = 8;
                    modelGap = 4;
                    caseOffset = 0;
                }
                default -> {
                    casingModel = PartialModelRegistry.REPEATER_SIGNAL_CASING;
                    upperCasingModel = PartialModelRegistry.REPEATER_SIGNAL_UPPER_CASING;
                    modelHeight = 14;
                    modelGap = 6;
                    caseOffset = 2;
                }
            }

            {
                ms.pushPose();
                msr.translate(0, caseOffset/16, 0);
                msr.translate(-0.5, -1.0/16, -0.5);
                ModelData casing = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(casingModel)
                    .createInstance();
                staticParts.add(casing);
                allModels.add(casing);
                casing.setTransform(ms);

                mastCouplerPositions.add(new Vec3(offset.x, (caseOffset - 2.0) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                mastCouplerPositions.add(new Vec3(offset.x, (caseOffset + modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));

                if (appearance.getForm() == RepeaterForm.DOUBLE_DISC) {
                    msr.translate(0, (modelHeight + modelGap)/16, 0);
                    ModelData upperCasing = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(upperCasingModel)
                        .createInstance();
                    staticParts.add(upperCasing);
                    allModels.add(upperCasing);
                    upperCasing.setTransform(ms);
                    mastCouplerPositions.add(new Vec3(offset.x, (caseOffset - 2.0 + modelGap + modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                    mastCouplerPositions.add(new Vec3(offset.x, (caseOffset + modelGap + 2 * modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));

                }

                ms.popPose();
            }
            
            ms.popPose();
        }
    }

    // berにやらせる
    @Override
    public void beginFrame(BlockPos instancePos) {
        super.beginFrame(instancePos);
    }

    @Override
    public void updateLight(Level level, BlockPos pos) {
        super.updateLight(level, pos);
        staticParts.forEach(model -> model.updateLight(level, pos));
    }

    @Override
    public void remove() {
        super.remove();
        staticParts.forEach(ModelData::delete);
        staticParts.clear();
    }
    
}
