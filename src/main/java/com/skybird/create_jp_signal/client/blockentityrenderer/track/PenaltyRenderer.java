package com.skybird.create_jp_signal.client.blockentityrenderer.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.skybird.create_jp_signal.block.track.PenaltyBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.create.train.track.PenaltyBoundary;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PenaltyRenderer extends SmartBlockEntityRenderer<PenaltyBlockEntity> {

    public PenaltyRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PenaltyBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
        MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, poseStack, buffer, light, overlay);
        TrackTargetingBehaviour<PenaltyBoundary> target = blockEntity.edgePoint;
        if (!blockEntity.shouldRenderOverlay())
            return;

        BlockPos pos = blockEntity.getBlockPos();
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = blockEntity.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock))
            return;

        poseStack.pushPose();
        poseStack.translate(targetPosition.getX() - pos.getX(), targetPosition.getY() - pos.getY(),
            targetPosition.getZ() - pos.getZ());
        CustomOverlayRenderer.renderOverlay(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(),
            poseStack, buffer, light, overlay, PartialModelRegistry.PENALTY_MARKER, 1.0f);
        poseStack.popPose();
    }
}
