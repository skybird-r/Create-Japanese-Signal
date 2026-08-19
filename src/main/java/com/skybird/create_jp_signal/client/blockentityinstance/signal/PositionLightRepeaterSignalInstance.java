package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.RepeaterForm;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightRepeaterSignalInstance extends SignalHeadInstance {

    private final List<SignalModelData> staticParts = new ArrayList<>();
    private final List<SignalModelData> lightParts = new ArrayList<>();

    private static final List<Vec3> NORMAL_LIGHT_POSITIONS = List.of(
        lightPosition(0, 0), lightPosition(-5.0 / 16, 0), lightPosition(5.0 / 16, 0),
        lightPosition(-3.5 / 16, -3.5 / 16), lightPosition(3.5 / 16, 3.5 / 16),
        lightPosition(0, -5.0 / 16), lightPosition(0, 5.0 / 16),
        lightPosition(0, 20.0 / 16), lightPosition(0, 15.0 / 16), lightPosition(0, 25.0 / 16)
    );
    private static final List<Vec3> TUNNEL_LIGHT_POSITIONS = List.of(
        lightPosition(0, 0), lightPosition(-3.0 / 16, 0), lightPosition(3.0 / 16, 0),
        lightPosition(-2.0 / 16, -2.0 / 16), lightPosition(2.0 / 16, 2.0 / 16),
        lightPosition(0, -3.0 / 16), lightPosition(0, 3.0 / 16),
        lightPosition(0, 12.0 / 16), lightPosition(0, 9.0 / 16), lightPosition(0, 15.0 / 16)
    );

    private static Vec3 lightPosition(double x, double y) {
        return new Vec3(x, y, 0);
    }

    public PositionLightRepeaterSignalInstance(SignalInstanceManager materialManager, SignalHead signalHead, BlockEntity be) {
        super(materialManager, signalHead, be);
    }
    
    @Override
    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof PositionLightRepeaterSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        {
            ms.pushPose();
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(offset.x, offset.y, offset.z);
            ms.mulPose(Axis.XP.rotationDegrees(rotation.getSecond().floatValue()));

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
                ms.translate(0, caseOffset/16, 0);
                ms.translate(-0.5, -1.0/16, -0.5);
                SignalModelData casing = materialManager.create(casingModel);
                staticParts.add(casing);
                allModels.add(casing);
                casing.setTransform(ms);

                mastCouplerPositions.add(new Vec3(offset.x, (caseOffset - 2.0) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                mastCouplerPositions.add(new Vec3(offset.x, (caseOffset + modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));

                if (appearance.getForm() == RepeaterForm.DOUBLE_DISC) {
                    ms.translate(0, (modelHeight + modelGap)/16, 0);
                    SignalModelData upperCasing = materialManager.create(upperCasingModel);
                    staticParts.add(upperCasing);
                    allModels.add(upperCasing);
                    upperCasing.setTransform(ms);
                    mastCouplerPositions.add(new Vec3(offset.x, (caseOffset - 2.0 + modelGap + modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                    mastCouplerPositions.add(new Vec3(offset.x, (caseOffset + modelGap + 2 * modelHeight) / 16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));

                }

                ms.popPose();
            }

            {
                List<Vec3> positions;
                float lampScale;
                double yOffset;
                switch (appearance.getSignalSize()) {
                    case NORMAL -> {
                        positions = NORMAL_LIGHT_POSITIONS;
                        lampScale = 2.5f;
                        yOffset = 7.5 / 16;
                    }
                    case TUNNEL -> {
                        positions = TUNNEL_LIGHT_POSITIONS;
                        lampScale = 1.5f;
                        yOffset = 3.0 / 16;
                    }
                    default -> throw new IllegalStateException("Unexpected signal size: " + appearance.getSignalSize());
                }

                int lampCount = appearance.getForm() == RepeaterForm.DOUBLE_DISC ? 10 : 7;
                ms.pushPose();
                ms.translate(0, yOffset + 0.25 / 16, 1.75 / 16);
                for (int i = 0; i < lampCount; i++) {
                    ms.pushPose();
                    Vec3 position = positions.get(i);
                    ms.translate(position.x, position.y, position.z);
                    ms.scale(lampScale, lampScale, lampScale);
                    SignalModelData light = materialManager.createFullBright(PartialModelRegistry.SIGNAL_LIGHT);
                    light.setTransform(ms);
                    lightParts.add(light);
                    allModels.add(light);
                    ms.popPose();
                }
                ms.popPose();
            }
            
            ms.popPose();
        }
        updateLightColors();
    }

    @Override
    public void beginFrame(BlockPos instancePos) {
        super.beginFrame(instancePos);
        updateLightColors();
    }

    private void updateLightColors() {
        long gameTime = Minecraft.getInstance().level.getGameTime();
        for (int i = 0; i < lightParts.size(); i++) {
            LampColor color = signalHead.getCurrentAspect().getLampColor(i, gameTime);
            lightParts.get(i).setColor(color);
        }
    }

    @Override
    public void updateLight(Level level, BlockPos pos) {
        super.updateLight(level, pos);
        staticParts.forEach(model -> model.updateLight(level, pos));
    }

    @Override
    public void remove() {
        super.remove();
        staticParts.clear();
        lightParts.clear();
    }
    
}
