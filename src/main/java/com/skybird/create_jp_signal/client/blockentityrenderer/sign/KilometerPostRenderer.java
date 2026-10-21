package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.text.SignText;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class KilometerPostRenderer implements BlockEntityRenderer<KilometerPostBlockEntity> {
    public KilometerPostRenderer(BlockEntityRendererProvider.Context context) { }

    @Override public void render(KilometerPostBlockEntity marker, float partialTick, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(marker.getLevel())) return;
        pose.pushPose();
        KilometerPostLayout.placement(pose, marker);
        pose.pushPose();
        pose.translate(-0.5, 0, -0.5);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(),
            buffers.getBuffer(RenderType.cutout()), marker.getBlockState(), PartialModelRegistry.KILOMETER_POST.get(),
            1, 1, 1, light, overlay);
        pose.popPose();
        String text = Integer.toString(marker.getDistance());
        if (marker.useLetterO()) text = text.replace('0', 'O');
        for (int side = 0; side < 2; side++) {
            pose.pushPose();
            KilometerPostLayout.face(pose, side);
            for (int i = 0; i < text.length(); i++) {
                var sequence = SignText.sequence(text.substring(i, i + 1), SignText.FontMode.MINECRAFT_ENGLISH);
                pose.pushPose();
                KilometerPostLayout.digit(pose, i, text.length(),
                    KilometerPostLayout.digitCenterX(SignText.font().width(sequence)), 4);
                SignText.font().drawInBatch(sequence, 0, 0, 0xFF000000, false, pose.last().pose(), buffers,
                    Font.DisplayMode.NORMAL, 0, light);
                pose.popPose();
            }
            pose.popPose();
        }
        pose.popPose();
    }
}
