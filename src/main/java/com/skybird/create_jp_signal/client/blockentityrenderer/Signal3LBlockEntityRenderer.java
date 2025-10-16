package com.skybird.create_jp_signal.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.Signal3LBlock;
import com.skybird.create_jp_signal.block.Signal3LBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import com.skybird.create_jp_signal.JpSignals;


public class Signal3LBlockEntityRenderer implements BlockEntityRenderer<Signal3LBlockEntity> {
    
    private BakedModel lightModel;

    public Signal3LBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public int getViewDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }


    
    private static final ResourceLocation LIGHT_GREEN_MODEL_LOCATION = new ResourceLocation(JpSignals.MODID, "block/light_green");
    private static final ResourceLocation LIGHT_YELLOW_MODEL_LOCATION = new ResourceLocation(JpSignals.MODID, "block/light_yellow");
    private static final ResourceLocation LIGHT_RED_MODEL_LOCATION = new ResourceLocation(JpSignals.MODID, "block/light_red");
    private static final ResourceLocation LIGHT_PURPLE_MODEL_LOCATION = new ResourceLocation(JpSignals.MODID, "block/light_purple");
    
    private static final ResourceLocation LIGHT_GREEN_TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/block/light_green.png");
    private static final ResourceLocation LIGHT_YELLOW_TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/block/light_yellow.png");
    private static final ResourceLocation LIGHT_RED_TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/block/light_red.png");
    private static final ResourceLocation LIGHT_PURPLE_TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/block/light_purple.png");

    @Override
    public void render(Signal3LBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        
        

        BlockState blockState = pBlockEntity.getBlockState();
        Direction direction = blockState.getValue(Signal3LBlock.FACING);
        float angle = direction.toYRot();
        
        pPoseStack.pushPose();
        {
            pPoseStack.translate(0.5, 0.5, 0.5);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - angle));
            pPoseStack.translate(-0.5, -0.5, -0.5);
            
            BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
            ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

            modelRenderer.tesselateWithAO(
                pBlockEntity.getLevel(), bakedModel, blockState, pBlockEntity.getBlockPos(), pPoseStack, 
                pBufferSource.getBuffer(RenderType.cutout()), false, 
                pBlockEntity.getLevel().getRandom(), pPackedLight, pPackedOverlay
            );
        }
        pPoseStack.popPose();

        
        double offset = 0;
        ResourceLocation modelLocation = LIGHT_RED_MODEL_LOCATION;
        ResourceLocation textureLocation = LIGHT_RED_TEXTURE;
        boolean activate = true;

        switch (pBlockEntity.aspect) {
            case RED:
                modelLocation = LIGHT_RED_MODEL_LOCATION;
                textureLocation = LIGHT_RED_TEXTURE;
                offset = 0;
                break;
            case YELLOW:
                modelLocation = LIGHT_YELLOW_MODEL_LOCATION;
                textureLocation = LIGHT_YELLOW_TEXTURE;
                offset = 4.5/16.0;
                break;
            case GREEN:
                modelLocation = LIGHT_GREEN_MODEL_LOCATION;
                textureLocation = LIGHT_GREEN_TEXTURE;
                offset = 9.0/16.0;
                break;
            default:
                activate = false;
                break;
        }

    

        if (activate) {
            if (this.lightModel == null) {
                this.lightModel = Minecraft.getInstance().getModelManager().getModel(modelLocation);
            }
            pPoseStack.pushPose();
            {
                pPoseStack.translate(0.5, 0.5, 0.5);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - angle));
                pPoseStack.translate(-0.5, -0.5, -0.5);
                
                pPoseStack.translate(0, offset, 0);

                VertexConsumer lightBuffer = pBufferSource.getBuffer(RenderType.eyes(textureLocation));
                
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    pPoseStack.last(),
                    lightBuffer,
                    null,
                    this.lightModel,
                    1.0f, // R
                    1.0f, // G
                    1.0f, // B
                    LightTexture.FULL_BRIGHT,
                    pPackedOverlay
                );
            }
            pPoseStack.popPose();
        }
    }
}