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
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightShuntSignalInstance extends SignalHeadInstance {

    private final List<ModelData> staticParts = new ArrayList<>();

    public PositionLightShuntSignalInstance(MaterialManager materialManager, SignalHead signalHead, BlockEntity be) {
        super(materialManager, signalHead, be);
    }
    
    @Override
    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof PositionLightShuntSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        TransformStack msr = TransformStack.cast(ms);
        {
            ms.pushPose();
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst()).translate(offset).rotateX(rotation.getSecond());
            PartialModel casingModel;

            casingModel = switch (appearance.getType()) {
                case TWO_WHITE   -> PartialModelRegistry.SHUNT_2_SIGNAL_CASING;
                case TWO_RED     -> PartialModelRegistry.SHUNT_2_SIGNAL_CASING;
                case THREE_WHITE -> PartialModelRegistry.SHUNT_3_SIGNAL_CASING;
                case THREE_RED   -> PartialModelRegistry.SHUNT_3_SIGNAL_CASING;
            };
            
            {
                ms.pushPose();
                msr.translate(0, 2.0/16, 0);
                msr.translate(-0.5, -2.0/16, -0.5);
                ModelData casing = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(casingModel)
                    .createInstance();
                staticParts.add(casing);
                allModels.add(casing);
                casing.setTransform(ms);

                mastCouplerPositions.add(new Vec3(offset.x, offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
            
                ms.popPose();
            }
            ms.popPose();
        }
    }

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
