package com.skybird.create_jp_signal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.skybird.create_jp_signal.create.train.track.AllEdgePointTypes;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.blockentityrenderer.track.CustomOverlayRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackTargetingClient.class, remap = false)
public abstract class TrackTargetingClientMixin {

    @Shadow static EdgePointType<?> lastType;
    @Shadow static BlockPos lastHovered;
    @Shadow static boolean lastDirection;
    @Shadow static BezierTrackPointLocation lastHoveredBezierSegment;

    @Inject(
        method = "render",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETSTATIC,
            target = "Lcom/simibubi/create/content/trains/track/TrackTargetingClient;lastType:Lcom/simibubi/create/content/trains/graph/EdgePointType;",
            ordinal = 0
        ),
        cancellable = true
    )
    private static void create_jp_signal_onRender(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, CallbackInfo ci) {
        if (lastType == AllEdgePointTypes.SPEED_LIMIT) {
            Minecraft mc = Minecraft.getInstance();
            BlockPos pos = lastHovered;
            Level level = mc.level;
            if (pos == null || level == null) 
                return;
            int light = LevelRenderer.getLightColor(level, pos);
            Direction.AxisDirection direction = lastDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

            {
                ms.pushPose();
                ms.translate(Vec3.atLowerCornerOf(pos).subtract(camera).x(),
                            Vec3.atLowerCornerOf(pos).subtract(camera).y(),
                            Vec3.atLowerCornerOf(pos).subtract(camera).z());
                
                CustomOverlayRenderer.renderOverlay(mc.level, pos, direction, lastHoveredBezierSegment, ms, buffer, light,
                    OverlayTexture.NO_OVERLAY, 
                    PartialModelRegistry.SPEED_LIMIT_MARKER,
                    1 + 1 / 16f);
                
                ms.popPose();
            }
            
            ci.cancel();
        }
    }
}
