package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import java.time.format.SignStyle;
import java.util.Iterator;
import java.util.List;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.RepeaterForm;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.SignalSize;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.client.ModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightRepeaterSignalRenderer implements ISignalHeadRenderer {

    private static final List<Vec3> NORMAL_VEC_LIST = List.of(vec(0, 0), vec(-5.0/16,0), vec(5.0/16,0), vec(-3.5/16, -3.5/16), vec(3.5/16, 3.5/16), vec(0, -5.0/16), vec(0, 5.0/16), vec(0, 20.0/16), vec(0, (20.0-5.0)/16), vec(0, (20.0+5.0)/16));
    private static final List<Vec3> TUNNEL_VEC_LIST = List.of(vec(0, 0), vec(-3.0/16,0), vec(3.0/16,0), vec(-2.0/16, -2.0/16), vec(2.0/16, 2.0/16), vec(0, -3.0/16), vec(0, 3.0/16), vec(0, 12.0/16), vec(0, (12.0-3.0)/16), vec(0, (12.0+3.0)/16));

    private static Vec3 vec(double x, double y) {
        return new Vec3(x, y, 0);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, SignalHead headData, BlockEntity blockEntity , int packedLight, int packedOverlay, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(headData.getAppearance() instanceof PositionLightRepeaterSignalAppearance appearance)) {
            return;
        }

        SignalAspect.State currentAspect = headData.getCurrentAspect();
        long gameTime = Minecraft.getInstance().level.getGameTime();


        boolean flywheelActive = Backend.canUseInstancing(blockEntity.getLevel());
        if (flywheelActive) {
            PoseStack ms = poseStack;
            TransformStack msr = TransformStack.cast(ms);
            ms.pushPose();

            List<Vec3> vecList;
            float lampScale;
            double yOffset;

            switch (appearance.getSignalSize()) {
                case NORMAL -> {
                    vecList = NORMAL_VEC_LIST;
                    lampScale = 2.5f;
                    yOffset = 7.5/16;
                }
                case TUNNEL -> {
                    vecList = TUNNEL_VEC_LIST;
                    lampScale = 1.5f;
                    yOffset = 3.0/16;
                }
                default -> {
                    vecList = NORMAL_VEC_LIST;
                    lampScale = 2.5f;
                    yOffset = 7.5/16;
                }
            }

            msr.multiply(Axis.YP.rotationDegrees((float)(double)rotation.getFirst())).translate(offset.x, offset.y, offset.z);

            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

            {
                ms.pushPose();
                msr.translate(0, yOffset, 0).translate(0, 0.25/16, 1.75/16);
                LampColor color;
                for (int i = 0; i < currentAspect.getLampCount(); i++) {
                    ms.pushPose();
                    color = currentAspect.getLampColor(i, gameTime);
                    msr.translate(vecList.get(i)).scale(lampScale);
                    modelRenderer.renderModel(
                        ms.last(),
                        bufferSource.getBuffer(RenderType.cutout()),
                        blockEntity.getBlockState(),
                        ModelRegistry.light,
                        color.getRed(), color.getGreen(), color.getBlue(),
                        LightTexture.FULL_BRIGHT,
                        overlay
                    );
                    ms.popPose();
                }
                ms.popPose();
            }
            
            SignalAccessory.Type accessory = appearance.getAccessory().getType();
            SignalAccessory.Route route = headData.getCurrentRoute();

            switch (accessory) {
                case FORECAST -> {
                    ms.pushPose();
                    Iterator<LampColor> colors = SignalAccessory.getLampColors(accessory, route).iterator();
                    msr.translate(-7.0/16, (2.25-8.0)/16, 1.75/16).scale(3.5f);
                    for (int i = 0; i < 2; i++) {
                        LampColor color = colors.next();
                        modelRenderer.renderModel(
                            ms.last(),
                            bufferSource.getBuffer(RenderType.cutout()),
                            blockEntity.getBlockState(),
                            ModelRegistry.light,
                            color.getRed(), color.getGreen(), color.getBlue(),
                            LightTexture.FULL_BRIGHT,
                            overlay
                        );
                        msr.translate(14.0/16/3.5, 0, 0);
                    }
                    ms.popPose();
                }
                case INDICATOR_HOME -> {
                    ms.pushPose();
                    Iterator<LampColor> colors = SignalAccessory.getLampColors(accessory, route).iterator();
                    msr.translate(-5.0/16, 0.75/16 - 1, 1.75/16).scale(2.5f);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            LampColor color = colors.next();
                            modelRenderer.renderModel(
                                ms.last(),
                                bufferSource.getBuffer(RenderType.cutout()),
                                blockEntity.getBlockState(),
                                ModelRegistry.light,
                                color.getRed(), color.getGreen(), color.getBlue(),
                                LightTexture.FULL_BRIGHT,
                                overlay
                            );
                            msr.translate(5.0/16/2.5, 0, 0);
                        }
                        msr.translate(-5.0*3/16/2.5, 5.0/16/2.5, 0);
                    }
                    ms.popPose();
                }
                case INDICATOR_DEPARTURE -> {
                    ms.pushPose();
                    Iterator<LampColor> colors = SignalAccessory.getLampColors(accessory, route).iterator();
                    msr.translate(0, (0.75 - 11.0)/16, 1.75/16).scale(2.5f);
                    LampColor color = colors.next();
                    modelRenderer.renderModel(
                        ms.last(),
                        bufferSource.getBuffer(RenderType.cutout()),
                        blockEntity.getBlockState(),
                        ModelRegistry.light,
                        color.getRed(), color.getGreen(), color.getBlue(),
                        LightTexture.FULL_BRIGHT,
                        overlay
                    );
                    msr.translate(-5.0/16/2.5, 5.0/16/2.5, 0);
                    for (int i = 0; i < 3; i++) {
                        color = colors.next();
                        modelRenderer.renderModel(
                            ms.last(),
                            bufferSource.getBuffer(RenderType.cutout()),
                            blockEntity.getBlockState(),
                            ModelRegistry.light,
                            color.getRed(), color.getGreen(), color.getBlue(),
                            LightTexture.FULL_BRIGHT,
                            overlay
                        );
                        msr.translate(5.0/16/2.5, 0, 0);
                    }
                    ms.popPose();
                }
                case INDICATOR_SHUNT -> {
                    ms.pushPose();
                    Iterator<LampColor> colors = SignalAccessory.getLampColors(accessory, route).iterator();
                    msr.translate(0, (0.5 - 8.0)/16, 1.75/16);
                    {
                        ms.pushPose();
                        msr.translate(-3.75/16, 0, 0).scale(1.5f, 3f, 1f);
                        for (int i = 0; i < 3; i++) {
                            LampColor color = colors.next();
                            modelRenderer.renderModel(
                                ms.last(),
                                bufferSource.getBuffer(RenderType.cutout()),
                                blockEntity.getBlockState(),
                                ModelRegistry.light,
                                color.getRed(), color.getGreen(), color.getBlue(),
                                LightTexture.FULL_BRIGHT,
                                overlay
                            );
                            msr.translate(3.75/16/1.5, 0, 0);
                        }
                        ms.popPose();
                    }
                    {
                        ms.pushPose();
                        msr.translate(0, 3.5/16, 0).scale(9f, 1.5f, 1f);
                        LampColor color = colors.next();
                        modelRenderer.renderModel(
                            ms.last(),
                            bufferSource.getBuffer(RenderType.cutout()),
                            blockEntity.getBlockState(),
                            ModelRegistry.light,
                            color.getRed(), color.getGreen(), color.getBlue(),
                            LightTexture.FULL_BRIGHT,
                            overlay
                        );
                        ms.popPose();
                    }
                    ms.popPose();
                }
                
            }
            ms.popPose();
            return;
        }
        // --- 描画処理 ---
        
        {
            poseStack.pushPose();
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            
            poseStack.mulPose(Axis.YP.rotationDegrees((float)(double)rotation.getFirst()));
            poseStack.translate(offset.x, offset.y, offset.z);
            double x = offset.x, z = offset.z;
            double distance = Math.sqrt(x * x + z * z);
            if (appearance.getSignalSize() == SignalSize.NORMAL) {
                {
                    poseStack.pushPose();
                    for (boolean top : Iterate.trueAndFalse) {
                        poseStack.pushPose();
                        if (top) poseStack.translate(0, 1, 0);
                        poseStack.translate(-x-0.5, -0.5, -z-0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.mastCoupler, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.pushPose();
                        poseStack.translate(0.5, 0.5, 0.5);
                        poseStack.mulPose(Axis.YP.rotation((float)Math.atan2(x,z)));
                        poseStack.translate(-0.5, -0.5, 0);
                        poseStack.scale(1, 1, (float)distance * 16);
                        
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.mastPipe, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                        poseStack.translate(x, 0, z);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.signalJoint, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
                {
                    poseStack.pushPose();
                    poseStack.translate(0, 2.0/16, 0);
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -1.0/16, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.repeaterSignalCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    double xList[] = {5.0/16, 3.5/16, 0};
                    double yList[] = {0, 3.5/16, 5.0/16};
                    for (int i = -1; i < 3; i++) {
                        poseStack.pushPose();
                        LampColor color;
                        poseStack.translate(0, 5.5/16, 0);
                        if (i == -1) {
                            color = LampColor.WHITE;
                        } else {
                            color = currentAspect.getLampColor(i);
                        }
                        
                        for (boolean first : Iterate.trueAndFalse) {
                            poseStack.pushPose();
                            if (i == -1) {
                                if (first) {
                                    poseStack.popPose();
                                    continue;
                                }
                            } else if (first) {
                                poseStack.translate(xList[i], yList[i], 0);
                            } else {
                                poseStack.translate(-xList[i], -yList[i], 0);
                            }

                            {
                                poseStack.pushPose();
                                poseStack.translate(-0.5, -0.5, -0.5);
                                blockRenderer.getModelRenderer().tesselateWithAO(
                                    blockEntity.getLevel(), ModelRegistry.lampBox3, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                    packedLight, packedOverlay
                                );
                                poseStack.popPose();
                            }
                            {
                                poseStack.pushPose();
                                poseStack.translate(0, 0.25/16, 1.75/16);
                                poseStack.scale(2.5f, 2.5f, 2.5f);
                                modelRenderer.renderModel(
                                    poseStack.last(),
                                    bufferSource.getBuffer(RenderType.cutout()),
                                    blockEntity.getBlockState(),
                                    ModelRegistry.light,
                                    color.getRed(), color.getGreen(), color.getBlue(),
                                    LightTexture.FULL_BRIGHT,
                                    overlay
                                );
                                poseStack.popPose();
                            }
                            poseStack.popPose();
                            
                        }
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }

                if (appearance.getForm() == RepeaterForm.DOUBLE_DISC && currentAspect.getLampCount() >= 7) {
                    poseStack.pushPose();
                    poseStack.translate(0, 1.25, 0);
                    {
                        poseStack.pushPose();
                        for (boolean top : Iterate.trueAndFalse) {
                            poseStack.pushPose();
                            if (top) poseStack.translate(0, 1, 0);
                            poseStack.translate(-x-0.5, -0.5, -z-0.5);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.mastCoupler, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.pushPose();
                            poseStack.translate(0.5, 0.5, 0.5);
                            poseStack.mulPose(Axis.YP.rotation((float)Math.atan2(x,z)));
                            poseStack.translate(-0.5, -0.5, 0);
                            poseStack.scale(1, 1, (float)distance * 16);
                            
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.mastPipe, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                            poseStack.translate(x, 0, z);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.signalJoint, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        poseStack.popPose();
                    }
                    {
                        poseStack.pushPose();
                        poseStack.translate(0, 2.0/16, 0);
                        {
                            poseStack.pushPose();
                            poseStack.translate(-0.5, -1.0/16, -0.5);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.repeaterSignalUpperCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        poseStack.translate(0, 0.5/16, 0);
                        LampColor color = currentAspect.getLampColor(7);
                        for (int i = 0; i < 3; i++) {
                            {
                                poseStack.pushPose();
                                poseStack.translate(-0.5, -0.5, -0.5);
                                blockRenderer.getModelRenderer().tesselateWithAO(
                                    blockEntity.getLevel(), ModelRegistry.lampBox3, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                    packedLight, packedOverlay
                                );
                                poseStack.popPose();
                            }
                            {
                                poseStack.pushPose();
                                poseStack.translate(0, 0.25/16, 1.75/16);
                                poseStack.scale(2.5f, 2.5f, 2.5f);
                                modelRenderer.renderModel(
                                    poseStack.last(),
                                    bufferSource.getBuffer(RenderType.cutout()),
                                    blockEntity.getBlockState(),
                                    ModelRegistry.light,
                                    color.getRed(), color.getGreen(), color.getBlue(),
                                    LightTexture.FULL_BRIGHT,
                                    overlay
                                );
                                poseStack.popPose();
                            }
                            poseStack.translate(0, 5.0/16, 0);
                        }
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
            } else if (appearance.getSignalSize() == SignalSize.TUNNEL) {
                {
                    poseStack.pushPose();
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -1.0/16, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.repeaterSignalTunnelCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    double xList[] = {3.0/16, 2.0/16, 0};
                    double yList[] = {0, 2.0/16, 3.0/16};
                    for (int i = -1; i < 3; i++) {
                        poseStack.pushPose();
                        LampColor color;
                        poseStack.translate(0, 3.0/16, 0);
                        if (i == -1) {
                            color = LampColor.WHITE;
                        } else {
                            color = currentAspect.getLampColor(i);
                        }
                        
                        for (boolean first : Iterate.trueAndFalse) {
                            poseStack.pushPose();
                            if (i == -1) {
                                if (first) {
                                    poseStack.popPose();
                                    continue;
                                }
                            } else if (first) {
                                poseStack.translate(xList[i], yList[i], 0);
                            } else {
                                poseStack.translate(-xList[i], -yList[i], 0);
                            }

                            {
                                poseStack.pushPose();
                                poseStack.translate(-0.5, -0.5, -0.5);
                                blockRenderer.getModelRenderer().tesselateWithAO(
                                    blockEntity.getLevel(), ModelRegistry.lampBox2, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                    packedLight, packedOverlay
                                );
                                poseStack.popPose();
                            }
                            {
                                poseStack.pushPose();
                                poseStack.translate(0, 0.25/16, 1.75/16);
                                poseStack.scale(1.5f, 1.5f, 1.5f);
                                modelRenderer.renderModel(
                                    poseStack.last(),
                                    bufferSource.getBuffer(RenderType.cutout()),
                                    blockEntity.getBlockState(),
                                    ModelRegistry.light,
                                    color.getRed(), color.getGreen(), color.getBlue(),
                                    LightTexture.FULL_BRIGHT,
                                    overlay
                                );
                                poseStack.popPose();
                            }
                            poseStack.popPose();
                            
                        }
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }

                if (appearance.getForm() == RepeaterForm.DOUBLE_DISC && currentAspect.getLampCount() >= 7) {
                    poseStack.pushPose();
                    poseStack.translate(0, 12.0/16, 0);
                    {
                        poseStack.pushPose();
                        {
                            poseStack.pushPose();
                            poseStack.translate(-0.5, -1.0/16, -0.5);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.repeaterSignalTunnelUpperCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        LampColor color = currentAspect.getLampColor(7);
                        for (int i = 0; i < 3; i++) {
                            {
                                poseStack.pushPose();
                                poseStack.translate(-0.5, -0.5, -0.5);
                                blockRenderer.getModelRenderer().tesselateWithAO(
                                    blockEntity.getLevel(), ModelRegistry.lampBox2, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                    packedLight, packedOverlay
                                );
                                poseStack.popPose();
                            }
                            {
                                poseStack.pushPose();
                                poseStack.translate(0, 0.25/16, 1.75/16);
                                poseStack.scale(1.5f, 1.5f, 1.5f);
                                modelRenderer.renderModel(
                                    poseStack.last(),
                                    bufferSource.getBuffer(RenderType.cutout()),
                                    blockEntity.getBlockState(),
                                    ModelRegistry.light,
                                    color.getRed(), color.getGreen(), color.getBlue(),
                                    LightTexture.FULL_BRIGHT,
                                    overlay
                                );
                                poseStack.popPose();
                            }
                            poseStack.translate(0, 3.0/16, 0);
                        }
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
            }
            // Accessory

            
            {
                poseStack.pushPose();
                SignalAccessory.Type accessory = appearance.getAccessory().getType();
                SignalAccessory.Route route = headData.getCurrentRoute();

                // 進路予告機
                if (accessory == SignalAccessory.Type.FORECAST) {
                    poseStack.translate(0, -8.0 / 16, 0);
                    {   
                        //連結部
                        poseStack.pushPose();
                        poseStack.translate(-x-0.5, -2.0/16-0.5, -z-0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.mastCoupler, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        {
                            poseStack.pushPose();
                            poseStack.translate(0.5, 0.5, 0.5);
                            poseStack.mulPose(Axis.YP.rotation((float)Math.atan2(x,z)));
                            poseStack.translate(-0.5, -0.5, 0);
                            poseStack.scale(1, 1, (float)distance * 16);
                            
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.mastPipe, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        poseStack.popPose();
                    }
                    //本体
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -0.5, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.routeForecastCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    for (boolean isRight : Iterate.trueAndFalse) {
                        poseStack.pushPose();
                        poseStack.translate((isRight ? 7.0f/16 : -7.0f/16), 2.0f/16, 0);
                        SignalAspect.LampColor color = SignalAspect.LampColor.OFF;
                        if (route == SignalAccessory.Route.RIGHT && isRight || route == SignalAccessory.Route.LEFT && !isRight) color = SignalAspect.LampColor.WHITE;
                        if (route == SignalAccessory.Route.CENTER) color = SignalAspect.LampColor.WHITE;
                        {
                            poseStack.pushPose();
                            poseStack.translate(-0.5, -0.5, -0.5);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.lampBox4, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        
                        {
                            poseStack.pushPose(); 
                            poseStack.translate(0, 0.25/16, 1.75/16);
                            poseStack.scale(3.5f, 3.5f, 3.5f);
                            modelRenderer.renderModel(
                                poseStack.last(),
                                bufferSource.getBuffer(RenderType.cutout()),
                                blockEntity.getBlockState(),
                                ModelRegistry.light,
                                color.getRed(), color.getGreen(), color.getBlue(),
                                LightTexture.FULL_BRIGHT,
                                overlay
                            );
                            poseStack.popPose();
                        }
                        poseStack.popPose();
                    }
                }
                poseStack.popPose();
            }
            
            poseStack.popPose();
        }
        
    }
}