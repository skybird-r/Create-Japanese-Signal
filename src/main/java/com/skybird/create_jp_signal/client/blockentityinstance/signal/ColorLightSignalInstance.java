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
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ColorLightSignalInstance extends SignalHeadInstance {

    private final List<ModelData> staticParts = new ArrayList<>();

    public ColorLightSignalInstance(MaterialManager materialManager, SignalHead headData, BlockEntity be) {
        super(materialManager, headData, be);
    }

    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof ColorLightSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        TransformStack msr = TransformStack.cast(ms);
        {
            ms.pushPose();
            msr.translate(0.5, 0, 0.5).rotateY(rotation.getFirst()).translate(offset).rotateX(rotation.getSecond());

            PartialModel backplateBottomModel = switch (appearance.getBackplateType()) {
                case ROUND -> PartialModelRegistry.BACKPLATE_BOTTOM_ROUND;
                case SQUARE -> PartialModelRegistry.BACKPLATE_BOTTOM_SQUARE;
                case NONE -> null;
            };

            PartialModel lampBox = switch (appearance.getSignalSize()) {
                case NORMAL -> PartialModelRegistry.LAMPBOX_5x5;
                case TUNNEL -> PartialModelRegistry.LAMPBOX_4x4;
            };

            float lampHeight = switch (appearance.getSignalSize()) {
                case NORMAL -> 5;
                case TUNNEL -> 4;
            };

            int totalLampCount = this.signalHead.getCurrentAspect().getLampCount() + (appearance.isRepeater() ? 1 : 0);

            if (backplateBottomModel != null) {
                ModelData backplateBottom = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(backplateBottomModel)
                    .createInstance();
                staticParts.add(backplateBottom);
                allModels.add(backplateBottom);

                ModelData backplateTop = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(backplateBottomModel)
                    .createInstance();
                staticParts.add(backplateTop);
                allModels.add(backplateTop);

                ModelData backplateMiddle = materialManager.defaultCutout()
                    .material(Materials.TRANSFORMED)
                    .getModel(PartialModelRegistry.BACKPLATE_MIDDLE)
                    .createInstance();
                staticParts.add(backplateMiddle);
                allModels.add(backplateMiddle);

                // 4*4はbackplate多分バグる
                {
                    ms.pushPose();
                    msr.unCentre();
                    backplateBottom.setTransform(ms);
                    msr.centre().translate(0, (5.0 * totalLampCount + 7.0)/16.0, 0).rotateZ(180).unCentre();
                    backplateTop.setTransform(ms);
                    ms.popPose();
                }
                {
                    ms.pushPose();
                    float yScale = 1.0f/5 + totalLampCount;
                    msr.unCentre().translate(0, 11.0/16 - 0.5 * yScale, 0).scale(1, yScale, 1);
                    backplateMiddle.setTransform(ms);
                    ms.popPose();
                }
            }

            {
                ms.pushPose();
                msr.translate(0, 3.5/16, 0).unCentre();
                for (int i = 0; i < totalLampCount; i++) {
                    ModelData box = materialManager.defaultCutout()
                        .material(Materials.TRANSFORMED)
                        .getModel(lampBox)
                        .createInstance();
                    staticParts.add(box);
                    allModels.add(box);
                    box.setTransform(ms);
                    msr.translate(0, lampHeight/16, 0);
                }
                mastCouplerPositions.add(new Vec3(offset.x, 1.5/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                mastCouplerPositions.add(new Vec3(offset.x, (3.5 + lampHeight * totalLampCount) / 16.0 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                
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