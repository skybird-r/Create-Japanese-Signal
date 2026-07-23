package com.skybird.create_jp_signal.client.blockentityrenderer.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;

import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.ponder.api.level.PonderLevel;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomOverlayRenderer {

    public static void renderOverlay(
        LevelAccessor level,
        BlockPos pos,
        AxisDirection direction,
        BezierTrackPointLocation bezier,
        PoseStack ms,
        MultiBufferSource buffer,
        int light,
        int overlay,
        PartialModel model,
        float scale
    ) {
        if (level instanceof SchematicLevel
            && !(level instanceof PonderLevel)) {
            return;
        }

        BlockState trackState = level.getBlockState(pos);

        if (!(trackState.getBlock() instanceof ITrackBlock track)) {
            return;
        }

        ms.pushPose();

        var transform = TransformStack.of(ms);

        // 戻り値はCreate標準のオーバーレイモデル。
        // 今回は変形だけ利用し、独自modelを下で描画する。
        track.prepareTrackOverlay(
            transform,
            level,
            pos,
            trackState,
            bezier,
            direction,
            RenderedTrackOverlayType.SIGNAL
        );

        if (model != null) {
            CachedBuffers.partial(model, trackState)
                .translate(.5, 0, .5)
                .scale(scale)
                .translate(-.5, 0, -.5)
                .light(LevelRenderer.getLightColor(level, pos))
                .renderInto(
                    ms,
                    buffer.getBuffer(RenderType.cutoutMipped())
                );
        }

        ms.popPose();
    }
}