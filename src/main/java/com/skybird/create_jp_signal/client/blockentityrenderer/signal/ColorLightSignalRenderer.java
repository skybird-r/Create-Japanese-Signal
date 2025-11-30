package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import java.util.Iterator;

import com.ibm.icu.text.AlphabeticIndex.Bucket.LabelType;
import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalAccessory.Route;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.client.ModelRegistry;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ColorLightSignalRenderer implements ISignalHeadRenderer {

    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, SignalHead headData, BlockEntity blockEntity , int packedLight, int packedOverlay, Vec3 offset, Pair<Double, Double> rotation) {

        if (!(headData.getAppearance() instanceof ColorLightSignalAppearance appearance)) {
            return;
        }


        SignalAspect.State currentAspect = headData.getCurrentAspect();
        long gameTime = Minecraft.getInstance().level.getGameTime();

        // flywheel使用中
        boolean flywheelActive = Backend.canUseInstancing(blockEntity.getLevel());
        if (flywheelActive) {
            poseStack.pushPose();

            float lampHeight = switch (appearance.getSignalSize()) {
                case NORMAL -> 5;
                case TUNNEL -> 4;
            };

            poseStack.mulPose(Axis.YP.rotationDegrees((float)(double)rotation.getFirst()));
            poseStack.translate(offset.x, offset.y, offset.z);

            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

            poseStack.translate(0, 3.5/16, 0);

            int totalLampCount = currentAspect.getLampCount() + (appearance.isRepeater() ? 1 : 0);
            for (int i = 0; i < totalLampCount; i++) {
                
                SignalAspect.LampColor color;
                if (appearance.isRepeater() && i == 0) {
                    color = SignalAspect.LampColor.PURPLE;
                } else {
                    int aspectIndex = appearance.isRepeater() ? i - 1 : i;
                    color = currentAspect.getLampColor(aspectIndex);
                }
                if (!currentAspect.isLit(gameTime)) color = LampColor.OFF;
                
                
                
                {
                    poseStack.pushPose();   
                    poseStack.translate(0, 0.25/16, 1.75/16);
                    poseStack.scale(lampHeight - 0.5f, lampHeight - 0.5f, lampHeight - 0.5f);
                    // こっちのが軽い
                    modelRenderer.renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(RenderType.cutout()),
                        blockEntity.getBlockState(),
                        ModelRegistry.light,
                        color.getRed(), color.getGreen(), color.getBlue(),
                        LightTexture.FULL_BRIGHT,
                        overlay
                    );
                    // VertexConsumer vb = bufferSource.getBuffer(RenderType.cutout());
                    // CachedBufferer.partial(PartialModelRegistry.SIGNAL_LIGHT, blockEntity.getBlockState())
                    //     .transform(poseStack)
                    //     .color(color.getByteRed(), color.getByteGreen(), color.getByteBlue(), 255)
                    //     .renderInto(new PoseStack(), vb);
                    poseStack.popPose();    
                }
                
                poseStack.translate(0, lampHeight/16, 0);
            }
            poseStack.popPose();
            return;
        }


        
        {
            poseStack.pushPose();
            ColorLightSignalAppearance.BackplateType backplate = appearance.getBackplateType();


            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
            
            
            BakedModel backplateBottomBakedModel;

            poseStack.mulPose(Axis.YP.rotationDegrees((float)(double)rotation.getFirst()));
            poseStack.translate(offset.x, offset.y, offset.z);

            if (backplate == ColorLightSignalAppearance.BackplateType.ROUND) {
                backplateBottomBakedModel = ModelRegistry.backplateBottomRound;
            } else {
                backplateBottomBakedModel = ModelRegistry.backplateBottomSquare;
            }

            int totalLampCount = currentAspect.getLampCount() + (appearance.isRepeater() ? 1 : 0);
            {
                poseStack.pushPose();
                if (backplate != ColorLightSignalAppearance.BackplateType.NONE) {
                    
                    // backplate
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -0.5, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), backplateBottomBakedModel, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        {
                            poseStack.pushPose();
                            float yScale = 1.0f/5 + totalLampCount;
                            poseStack.translate(0, 11.0/16 - 0.5 * yScale, 0);
                            poseStack.scale(1, yScale, 1);
                            blockRenderer.getModelRenderer().tesselateWithAO(
                                blockEntity.getLevel(), ModelRegistry.backplateMiddle, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                packedLight, packedOverlay
                            );
                            poseStack.popPose();
                        }
                        poseStack.popPose();
                    }
                    {
                        poseStack.pushPose();
                        poseStack.translate(0, (5.0 * totalLampCount + 7.0)/16, 0);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                        poseStack.translate(-0.5, -0.5, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), backplateBottomBakedModel, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }

                    
                    {
                        poseStack.pushPose();
                        double x = offset.x, z = offset.z;
                        double distance = Math.sqrt(x * x + z * z);
                        for (boolean top : Iterate.trueAndFalse) {
                            poseStack.pushPose();
                            if (top) poseStack.translate(0, (5.0 * totalLampCount + 2.0) / 16.0, 0);
                            poseStack.translate(- x - 0.5, 1.5/16.0 - 0.5, - z - 0.5);
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
                        poseStack.translate(0, 3.5/16, 0);
                        for (int i = 0; i < totalLampCount; i++) {
                            
                            SignalAspect.LampColor color;
                            if (appearance.isRepeater() && i == 0) {
                                color = SignalAspect.LampColor.PURPLE;
                            } else {
                                int aspectIndex = appearance.isRepeater() ? i - 1 : i;
                                color = currentAspect.getLampColor(aspectIndex);
                            }
                            if (!currentAspect.isLit(gameTime)) color = LampColor.OFF;
                            
                            {
                                poseStack.pushPose();
                                poseStack.translate(-0.5, -0.5, -0.5);
                                blockRenderer.getModelRenderer().tesselateWithAO(
                                    blockEntity.getLevel(), ModelRegistry.lampBox5, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                                    bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                                    packedLight, packedOverlay
                                );
                                poseStack.popPose();
                            }
                            
                            
                            {
                                poseStack.pushPose();   
                                poseStack.translate(0, 0.25/16, 1.75/16);
                                poseStack.scale(4.5f, 4.5f, 4.5f);
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

                } else {
                    for (int i = 0; i < totalLampCount; i++) {
                        
                        SignalAspect.LampColor color;
                        if (appearance.isRepeater() && i == 0) {
                            color = SignalAspect.LampColor.PURPLE;
                        } else {
                            int aspectIndex = appearance.isRepeater() ? i - 1 : i;
                            color = currentAspect.getLampColor(aspectIndex);
                        }
                        if (!currentAspect.isLit(gameTime)) color = LampColor.OFF;
                        
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
                        
                        poseStack.translate(0, 4.0/16, 0);
                    }
                }
                poseStack.popPose();
            }
            
            // Accessory

            
            {
                poseStack.pushPose();
                SignalAccessory.Type accessory = appearance.getAccessory().getType();
                SignalAccessory.Route route = headData.getCurrentRoute();

                // 場内信号用進路表示器
                if (accessory == SignalAccessory.Type.INDICATOR_HOME) {
                    poseStack.translate(0,-1,0);
                    double x = offset.x, z = offset.z;
                    double distance = Math.sqrt(x * x + z * z);
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
                        poseStack.translate(x, 0, z);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.signalJoint, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }

                    //本体
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -1.0/16, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.routeIndicatorCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    poseStack.translate(0, 0.5/16, 0);
                    for (int i = -1; i < 2; i++) {
                        SignalAspect.LampColor color = SignalAspect.LampColor.OFF;
                        if (route == SignalAccessory.Route.CENTER && i == 0) color = SignalAspect.LampColor.WHITE;
                        else if (route == SignalAccessory.Route.RIGHT && i == -1) color = SignalAspect.LampColor.WHITE;
                        else if (route == SignalAccessory.Route.LEFT && i == 1) color = SignalAspect.LampColor.WHITE;
                        poseStack.pushPose();
                        poseStack.translate(5.0/16*i, 0, 0);
                        for (int j = 0; j < 3; j++) {
                            if (j == 2 && (route == SignalAccessory.Route.LEFT || route == SignalAccessory.Route.RIGHT)) color = SignalAspect.LampColor.WHITE;
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
                } else if (accessory == SignalAccessory.Type.FORECAST) {
                    poseStack.translate(0, -8.0 / 16, 0);
                    double x = offset.x, z = offset.z;
                    double distance = Math.sqrt(x * x + z * z);
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
                } else if (accessory == SignalAccessory.Type.INDICATOR_DEPARTURE) {
                    poseStack.translate(0,-0.6875,0);
                    double x = offset.x, z = offset.z;
                    double distance = Math.sqrt(x * x + z * z);
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
                        poseStack.translate(x, 0, z);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.signalJoint, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }

                    //本体
                    {
                        poseStack.pushPose();
                        poseStack.translate(-0.5, -1.0/16, -0.5);
                        blockRenderer.getModelRenderer().tesselateWithAO(
                            blockEntity.getLevel(), ModelRegistry.routeIndicatorDepartureCasing, blockEntity.getBlockState(), blockEntity.getBlockPos(), poseStack,
                            bufferSource.getBuffer(RenderType.cutout()), false, blockEntity.getLevel().getRandom(),
                            packedLight, packedOverlay
                        );
                        poseStack.popPose();
                    }
                    
                    poseStack.translate(0, 0.5/16, 0);

                    LampColor color = LampColor.OFF;
                    boolean left = Route.LEFT == route;
                    boolean right = Route.RIGHT == route;

                    if (left || right) color = LampColor.WHITE;
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
                    {
                        poseStack.pushPose();
                        poseStack.translate(-5.0/16, 5.0/16, 0);
                        for (int i = 0; i < 3; i++) {
                            if (i == 0 && left || i == 2 && right || i == 1 && (left || right)) {
                                color = LampColor.WHITE;
                            } else {
                                color = LampColor.OFF;
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
                            poseStack.translate(5.0/16, 0, 0);
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