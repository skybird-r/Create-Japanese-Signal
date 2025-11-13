package com.skybird.create_jp_signal.client.blockentityrenderer.track;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.skybird.create_jp_signal.block.track.SpeedLimitBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.create.train.track.SpeedLimitBoundary;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpeedLimitRenderer extends SmartBlockEntityRenderer<SpeedLimitBlockEntity> {

    public SpeedLimitRenderer(Context context) {
        super(context);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void renderSafe(SpeedLimitBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        TrackTargetingBehaviour<SpeedLimitBoundary> target = be.edgePoint;
        if (!be.shouldRenderOverlay()) {
            return;
        }
        BlockPos pos = be.getBlockPos();
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = be.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock))
            return;

        {
            ms.pushPose();

            ms.translate(targetPosition.getX() - pos.getX(),
                        targetPosition.getY() - pos.getY(),
                        targetPosition.getZ() - pos.getZ());
            
            CustomOverlayRenderer.renderOverlay(level, targetPosition, 
                target.getTargetDirection(), target.getTargetBezier(), ms,
                buffer, light, overlay, 
                PartialModelRegistry.SPEED_LIMIT_MARKER,
                1.0f);
                
            ms.popPose();
        }
    }
    
}
