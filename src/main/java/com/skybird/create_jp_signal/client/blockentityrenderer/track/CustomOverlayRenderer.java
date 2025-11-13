package com.skybird.create_jp_signal.client.blockentityrenderer.track;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels; // Createの標準モデル（SIGNAL_OVERLAY）を使う場合
import com.simibubi.create.content.schematics.SchematicWorld;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomOverlayRenderer {

    public static void renderOverlay(LevelAccessor level, BlockPos pos, Direction.AxisDirection direction,
                                     BezierTrackPointLocation bezier, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                     PartialModel model, float scale) {
        
        if (level instanceof SchematicWorld && !(level instanceof PonderWorld))
            return;

        BlockState trackState = level.getBlockState(pos);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock))
            return;

        ms.pushPose();

        prepareTrackOverlay(level, pos, trackState, bezier, direction, ms);

        if (model != null)
            CachedBufferer.partial(model, trackState)
                .translate(.5, 0, .5)
                .scale(scale)
                .translate(-.5, 0, -.5)
                .light(LevelRenderer.getLightColor(level, pos))
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        ms.popPose();
    }

    private static void prepareTrackOverlay(BlockGetter world, BlockPos pos, BlockState state,
                                          BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction,
                                          PoseStack ms) {
        TransformStack msr = TransformStack.cast(ms);
        Vec3 axis = null;
        Vec3 diff = null;
        Vec3 normal = null;
        Vec3 offset = null;

        if (bezierPoint != null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackBE) {
            BezierConnection bc = trackBE.getConnections().get(bezierPoint.curveTarget());
			if (bc != null) {
				double length = Mth.floor(bc.getLength() * 2);
				int seg = bezierPoint.segment() + 1;
				double t = seg / length;
				double tpre = (seg - 1) / length;
				double tpost = (seg + 1) / length;

				offset = bc.getPosition(t);
				normal = bc.getNormal(t);
				diff = bc.getPosition(tpost)
					.subtract(bc.getPosition(tpre))
					.normalize();

				msr.translate(offset.subtract(Vec3.atBottomCenterOf(pos)));
				msr.translate(0, -4 / 16f, 0);
			} else
				return;
        }

        if (normal == null) {
            axis = state.getValue(TrackBlock.SHAPE).getAxes().get(0);
            diff = axis.scale(direction.getStep()).normalize();
            normal = ((ITrackBlock) state.getBlock()).getUpNormal(world, pos, state);
        }

        Vec3 angles = TrackRenderer.getModelAngles(normal, diff);
        msr.centre().rotateYRadians(angles.y).rotateXRadians(angles.x).unCentre();

        if (axis != null) {
            msr.translate(0, axis.y != 0 ? 7 / 16f : 0, axis.y != 0 ? direction.getStep() * 2.5f / 16f : 0);
        } else {
            msr.translate(0, 4 / 16f, 0);
			if (direction == AxisDirection.NEGATIVE)
				msr.rotateCentered(Direction.UP, Mth.PI);
        }

        if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE && trackTE.isTilted()) {
			double yOffset = 0;
			for (BezierConnection bc : trackTE.getConnections().values())
				yOffset += bc.starts.getFirst().y - pos.getY();
			msr.centre()
				.rotateX(-direction.getStep() * trackTE.tilt.smoothingAngle.get())
				.unCentre()
				.translate(0, yOffset / 2, 0);
        }
    }
}