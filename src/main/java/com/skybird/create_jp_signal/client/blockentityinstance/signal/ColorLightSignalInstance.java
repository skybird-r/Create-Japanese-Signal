package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ColorLightSignalInstance extends SignalHeadInstance {

    private final List<SignalModelData> staticParts = new ArrayList<>();
    private final List<SignalModelData> lightParts = new ArrayList<>();

    public ColorLightSignalInstance(SignalInstanceManager materialManager, SignalHead headData, BlockEntity be) {
        super(materialManager, headData, be);
    }

    public void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(this.signalHead.getAppearance() instanceof ColorLightSignalAppearance appearance)) 
            return;
        
        this.remove();

        

        {
            ms.pushPose();
            ms.translate(0.5, 0, 0.5);
            ms.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
            ms.translate(offset.x, offset.y, offset.z);
            ms.mulPose(Axis.XP.rotationDegrees(rotation.getSecond().floatValue()));

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
            double boxOffset = switch (appearance.getSignalSize()) {
                case NORMAL -> 3.5;
                case TUNNEL -> 0;
            };

            int totalLampCount = this.signalHead.getCurrentAspect().getLampCount() + (appearance.isRepeater() ? 1 : 0);

            if (backplateBottomModel != null) {
                SignalModelData backplateBottom = materialManager.create(backplateBottomModel);
                staticParts.add(backplateBottom);
                allModels.add(backplateBottom);

                SignalModelData backplateTop = materialManager.create(backplateBottomModel);
                staticParts.add(backplateTop);
                allModels.add(backplateTop);

                SignalModelData backplateMiddle = materialManager.create(PartialModelRegistry.BACKPLATE_MIDDLE);
                staticParts.add(backplateMiddle);
                allModels.add(backplateMiddle);

                // 4*4はbackplate多分バグる
                {
                    ms.pushPose();
                    ms.translate(-0.5, -0.5, -0.5);
                    backplateBottom.setTransform(ms);
                    ms.translate(0.5, 0.5, 0.5);
                    ms.translate(0, (5.0 * totalLampCount + 7.0)/16.0, 0);
                    ms.mulPose(Axis.ZP.rotationDegrees(180));
                    ms.translate(-0.5, -0.5, -0.5);
                    backplateTop.setTransform(ms);
                    ms.popPose();
                }
                {
                    ms.pushPose();
                    float yScale = 1.0f/5 + totalLampCount;
                    ms.translate(-0.5, -0.5, -0.5);
                    ms.translate(0, 11.0/16 - 0.5 * yScale, 0);
                    ms.scale(1, yScale, 1);
                    backplateMiddle.setTransform(ms);
                    ms.popPose();
                }
            }

            {
                ms.pushPose();
                ms.translate(0, boxOffset/16, 0);
                ms.translate(-0.5, -0.5, -0.5);
                for (int i = 0; i < totalLampCount; i++) {
                    SignalModelData box = materialManager.create(lampBox);
                    staticParts.add(box);
                    allModels.add(box);
                    box.setTransform(ms);
                    ms.translate(0, lampHeight/16, 0);
                }
                mastCouplerPositions.add(new Vec3(offset.x, (boxOffset - 2.0)/16 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                mastCouplerPositions.add(new Vec3(offset.x, (boxOffset + lampHeight * totalLampCount) / 16.0 + offset.y, offset.z).yRot((float)(double)rotation.getFirst()));
                
                ms.popPose();
            }

            {
                ms.pushPose();
                ms.translate(0, (boxOffset + 0.25) / 16, 1.75 / 16);
                ms.scale(lampHeight - 0.5f, lampHeight - 0.5f, lampHeight - 0.5f);
                int lampCount = appearance.getHeadType().getLampCount() + (appearance.isRepeater() ? 1 : 0);
                for (int i = 0; i < lampCount; i++) {
                    SignalModelData light = materialManager.createFullBright(PartialModelRegistry.SIGNAL_LIGHT);
                    light.setTransform(ms);
                    lightParts.add(light);
                    allModels.add(light);
                    ms.translate(0, lampHeight / 16 / (lampHeight - 0.5), 0);
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
        if (!(signalHead.getAppearance() instanceof ColorLightSignalAppearance appearance)) {
            return;
        }
        long gameTime = Minecraft.getInstance().level.getGameTime();
        for (int i = 0; i < lightParts.size(); i++) {
            LampColor color = appearance.isRepeater()
                ? i == 0 ? LampColor.PURPLE : signalHead.getCurrentAspect().getLampColor(i - 1, gameTime)
                : signalHead.getCurrentAspect().getLampColor(i, gameTime);
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
