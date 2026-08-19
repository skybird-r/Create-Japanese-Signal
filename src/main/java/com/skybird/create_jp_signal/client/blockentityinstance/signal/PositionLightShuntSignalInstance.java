package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightShuntSignalInstance extends SignalHeadInstance {

    private final List<SignalModelData> staticParts = new ArrayList<>();
    private final List<SignalModelData> lightParts = new ArrayList<>();

    private static final List<Vec3> LIGHT_POSITIONS = List.of(
        new Vec3(0, 0, 0),
        new Vec3(5.0 / 16, 0, 0),
        new Vec3(3.5 / 16, 3.5 / 16, 0),
        new Vec3(0, 5.0 / 16, 0)
    );

    public PositionLightShuntSignalInstance(SignalInstanceManager materialManager, SignalHead signalHead, BlockEntity be) {
        super(materialManager, signalHead, be);
    }
    
    @Override
    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof PositionLightShuntSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        {
            ms.pushPose();
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(offset.x, offset.y, offset.z);
            ms.mulPose(Axis.XP.rotationDegrees(rotation.getSecond().floatValue()));
            PartialModel casingModel;

            casingModel = switch (appearance.getType()) {
                case TWO_WHITE   -> PartialModelRegistry.SHUNT_2_SIGNAL_CASING;
                case TWO_RED     -> PartialModelRegistry.SHUNT_2_SIGNAL_CASING;
                case THREE_WHITE -> PartialModelRegistry.SHUNT_3_SIGNAL_CASING;
                case THREE_RED   -> PartialModelRegistry.SHUNT_3_SIGNAL_CASING;
            };
            
            {
                ms.pushPose();
                ms.translate(0, 2.0/16, 0);
                ms.translate(-0.5, -2.0/16, -0.5);
                SignalModelData casing = materialManager.create(casingModel);
                staticParts.add(casing);
                allModels.add(casing);
                casing.setTransform(ms);

                mastCouplerPositions.add(new Vec3(offset.x, offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
            
                ms.popPose();
            }

            {
                int lampCount = switch (appearance.getType()) {
                    case TWO_WHITE, TWO_RED -> 3;
                    case THREE_WHITE, THREE_RED -> 4;
                };
                ms.pushPose();
                ms.translate(-2.5 / 16, 3.0 / 16, 1.75 / 16);
                for (int i = 0; i < lampCount; i++) {
                    ms.pushPose();
                    Vec3 position = LIGHT_POSITIONS.get(i);
                    ms.translate(position.x, position.y, position.z);
                    ms.scale(2.5f, 2.5f, 2.5f);
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
