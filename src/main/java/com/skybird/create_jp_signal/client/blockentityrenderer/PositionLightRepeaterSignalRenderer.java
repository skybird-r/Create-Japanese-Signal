package com.skybird.create_jp_signal.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalHead;
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

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, SignalHead headData, BlockEntity blockEntity , int packedLight, int packedOverlay, Vec3 offset, Pair<Double, Double> rotation) {
        if (!(headData.getAppearance() instanceof PositionLightRepeaterSignalAppearance appearance)) {
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
                
                for (boolean top : Iterate.trueAndFalse) {
                    poseStack.pushPose();
                    if (top) poseStack.translate(0, 1, 0);
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
            }
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0, 2.0/16, 0);
            blockRenderer.getModelRenderer().tesselateWithAO(
                blockEntity.getLevel(), ModelRegistry.repeaterSignalCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                packedLight, packedOverlay
            );

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
                            
                    }
                    poseStack.popPose();
                    
                }
                poseStack.popPose();
            }
            poseStack.popPose();
            
            // Accessory

            poseStack.pushPose();
            {
                SignalAccessory.Type accessory = appearance.getAccessory().getType();
                SignalAccessory.Route route = headData.getCurrentRoute();

                // 進路予告機
                if (accessory == SignalAccessory.Type.FORECAST) {
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