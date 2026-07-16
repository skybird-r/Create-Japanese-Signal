package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlockEntity;
import com.skybird.create_jp_signal.client.ModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class SignalMastBlockEntityRenderer implements BlockEntityRenderer<SignalMastBlockEntity> {

    public SignalMastBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public int getViewDistance() {
        return net.minecraft.client.Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }


    @Override
    public void render(SignalMastBlockEntity be, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        BlockState blockState = be.getBlockState();
        int rotation = be.getRotation();
        int xPos = be.getXPos();
        int zPos = be.getZPos();

        int cappedRotation = (rotation + 2) % 4 - 2;

        pPoseStack.translate((float)xPos / 16F, 0, (float)zPos / 16F);
        
        {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - (rotation * 22.5F)));
            pPoseStack.translate(-0.5, 0, -0.5);
            blockRenderer.getModelRenderer().tesselateWithAO(
                be.getLevel(), ModelRegistry.signalMast, blockState, be.getBlockPos(), pPoseStack,
                pBufferSource.getBuffer(RenderType.cutout()), false, be.getLevel().getRandom(),
                pPackedLight, pPackedOverlay
            );
            pPoseStack.popPose();
        }
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - (rotation * 22.5F)));
    }
}