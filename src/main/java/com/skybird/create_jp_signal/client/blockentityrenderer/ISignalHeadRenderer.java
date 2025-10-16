package com.skybird.create_jp_signal.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface ISignalHeadRenderer {
    void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, SignalHead headData, BlockEntity blockEntity, int pPackedLight, int pPackedOverlay, Vec3 offset, Pair<Double, Double> rotation);
}