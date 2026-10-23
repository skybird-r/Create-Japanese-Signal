package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalName;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.text.FlywheelText;
import com.skybird.create_jp_signal.client.text.SignText;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class SignalNameBoardRenderer {
    private SignalNameBoardRenderer() {}

    public static void render(PoseStack pose, MultiBufferSource buffers, int light, int overlay,
            SignalHead head, BlockEntity be, Vec3 offset, Pair<Double, Double> rotation) {
        if (!SignalNameBoardLayout.visible(head)) return;
        var cells = head.getSignalNameCells();
        int lamps = SignalNameBoardLayout.lampCount(head);
        pose.pushPose();
        SignalNameBoardLayout.head(pose, offset, rotation);
        var renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        pose.pushPose();
        SignalNameBoardLayout.post(pose, lamps, cells.size());
        renderer.renderModel(pose.last(), buffers.getBuffer(RenderType.cutout()), be.getBlockState(),
            PartialModelRegistry.SIGNAL_NAME_BOARD_POST.get(), 1, 1, 1, light, overlay);
        pose.popPose();
        pose.pushPose();
        SignalNameBoardLayout.board(pose, lamps, cells.size());
        renderer.renderModel(pose.last(), buffers.getBuffer(RenderType.cutout()), be.getBlockState(),
            PartialModelRegistry.SIGNAL_NAME_BOARD.get(), 1, 1, 1, light, overlay);
        pose.popPose();
        for (int i = 0; i < cells.size(); i++) {
            var cell = cells.get(i);
            var glyph = FlywheelText.model(cell.text(), SignText.FontMode.CURRENT);
            pose.pushPose();
            SignalNameBoardLayout.cell(pose, lamps, cells.size(), i, cell, glyph);
            SignText.font().drawInBatch(SignText.sequence(cell.text(), SignText.FontMode.CURRENT),
                0, 0, 0xFFFFFFFF, false, pose.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
            pose.popPose();
        }
        pose.popPose();
    }
}
