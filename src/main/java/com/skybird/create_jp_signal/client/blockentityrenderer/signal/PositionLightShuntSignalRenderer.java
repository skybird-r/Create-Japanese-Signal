package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance.ShuntType;
import com.skybird.create_jp_signal.client.ModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PositionLightShuntSignalRenderer implements ISignalHeadRenderer{

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, SignalHead headData, BlockEntity blockEntity , int packedLight, int packedOverlay, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(headData.getAppearance() instanceof PositionLightShuntSignalAppearance appearance)) {
            return;
        }

        SignalAspect.State currentAspect = headData.getCurrentAspect();
        long gameTime = Minecraft.getInstance().level.getGameTime();


        // --- 描画処理 ---
        poseStack.pushPose();
        {
            
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            
            poseStack.mulPose(Axis.YP.rotationDegrees((float)(double)rotation.getFirst()));
            poseStack.translate(offset.x, offset.y, offset.z);
            double x = offset.x, z = offset.z;
            double distance = Math.sqrt(x * x + z * z);

            poseStack.pushPose();
            {
                poseStack.pushPose();
                poseStack.translate(-x, 0, -z);
                blockRenderer.getModelRenderer().tesselateWithAO(
                    blockEntity.getLevel(), ModelRegistry.mastCoupler, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                    packedLight, packedOverlay
                );
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotation((float)Math.atan2(x,z)));
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

            ShuntType type = appearance.getType();
            int totalLampCount = 0;
            if (type == ShuntType.TWO_WHITE || type == ShuntType.TWO_RED) {
                totalLampCount = 3;
            } else if (type == ShuntType.THREE_WHITE || type == ShuntType.THREE_RED) {
                totalLampCount = 4;
            }

            {
                poseStack.pushPose();
                poseStack.translate(0, 2.0/16, 0);
                if (totalLampCount == 3) {
                    blockRenderer.getModelRenderer().tesselateWithAO(
                        blockEntity.getLevel(), ModelRegistry.shunt2SignalCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                        bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                        packedLight, packedOverlay
                    );
                } else if (totalLampCount == 4) {
                    blockRenderer.getModelRenderer().tesselateWithAO(
                        blockEntity.getLevel(), ModelRegistry.shunt3SignalCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                        bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                        packedLight, packedOverlay
                    );
                }
                poseStack.translate(-2.5/16, 0.75/16, 0);
                double xList[] = {0, 5.0/16, 3.5/16, 0};
                double yList[] = {0, 0, 3.5/16, 5.0/16};
                for (int i = 0; i < totalLampCount; i++) {
                    LampColor color = currentAspect.getLampColor(i);
                    {
                        poseStack.pushPose();

                        poseStack.translate(xList[i], yList[i], 0);
                        
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.lampBox3, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        ); 
                        poseStack.translate(0, 0.25/16, 1.75/16);
                        poseStack.scale(2.5f, 2.5f, 2.5f);
                        modelRenderer.renderModel(
                            poseStack.last(),
                            bufferSource.getBuffer(RenderType.solid()),
                            blockEntity.getBlockState(),
                            ModelRegistry.light,
                            color.getRed(), color.getGreen(), color.getBlue(),
                            LightTexture.FULL_BRIGHT,
                            overlay
                        );
                        
                        poseStack.popPose();
                    }
                }
                poseStack.popPose();

            }
            
            // Accessory

            poseStack.pushPose();
            {
                SignalAccessory.Type accessory = appearance.getAccessory().getType();
                SignalAccessory.Route route = headData.getCurrentRoute();

                // 進路予告機
                if (accessory == SignalAccessory.Type.INDICATOR_SHUNT) {
                    poseStack.translate(0, -8.0 / 16, 0);
                    {   
                        //連結部
                        poseStack.pushPose();
                        poseStack.translate(-x, -2.0/16, -z);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.mastCoupler, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        {
                            poseStack.pushPose();
                            poseStack.mulPose(Axis.YP.rotation((float)Math.atan2(x,z)));
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
                    blockRenderer.getModelRenderer().tesselateWithAO(
                        blockEntity.getLevel(), ModelRegistry.routeForecastCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                        bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                        packedLight, packedOverlay
                    );
                    for (boolean isRight : Iterate.trueAndFalse) {
                        poseStack.pushPose();
                        poseStack.translate((isRight ? 7.0f/16 : -7.0f/16), 2.0f/16, 0);
                        SignalAspect.LampColor color = SignalAspect.LampColor.OFF;
                        if (route == SignalAccessory.Route.RIGHT && isRight || route == SignalAccessory.Route.LEFT && !isRight) color = SignalAspect.LampColor.WHITE;
                        if (route == SignalAccessory.Route.CENTER) color = SignalAspect.LampColor.WHITE;
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.lampBox4, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        
                        {
                            poseStack.pushPose(); 
                            poseStack.translate(0, 0.25/16, 1.75/16);
                            poseStack.scale(3.5f, 3.5f, 3.5f);
                            modelRenderer.renderModel(
                                poseStack.last(),
                                bufferSource.getBuffer(RenderType.solid()),
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
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    
}
