package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.sign.GradientPostBlockEntity;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.text.GradientPostText;
import com.skybird.create_jp_signal.client.text.SignText;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GradientPostRenderer implements BlockEntityRenderer<GradientPostBlockEntity> {
    public GradientPostRenderer(BlockEntityRendererProvider.Context context) { }

    private void model(PartialModel model, GradientPostBlockEntity post, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(),
            buffers.getBuffer(RenderType.cutout()), post.getBlockState(), model.get(), 1, 1, 1, light, overlay);
    }

    @Override public void render(GradientPostBlockEntity post, float partialTick, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(post.getLevel())) return;
        pose.pushPose();
        GradientPostLayout.placement(pose, post);
        pose.pushPose();
        pose.translate(-.5, 0, -.5);
        model(PartialModelRegistry.GRADIENT_POST, post, pose, buffers, light, overlay);
        pose.popPose();
        for (int side = 0; side < 2; side++) {
            pose.pushPose();
            GradientPostLayout.barRotation(pose, post.getGradient(side));
            pose.pushPose();
            GradientPostLayout.barModel(pose, side);
            model(PartialModelRegistry.GRADIENT_POST_BAR, post, pose, buffers, light, overlay);
            pose.popPose();
            GradientPostLayout.textFace(pose, side);
            var glyphs = GradientPostText.glyphs(post.getGradient(side), post.showDecimals(), post.useLetterO(), text ->
                SignText.font().width(SignText.sequence(text, SignText.FontMode.MINECRAFT_ENGLISH)));
            for (var glyph : glyphs) {
                pose.pushPose();
                GradientPostLayout.glyph(pose, glyph);
                SignText.font().drawInBatch(SignText.sequence(glyph.text(), SignText.FontMode.MINECRAFT_ENGLISH),
                    0, 0, 0xFF000000, false, pose.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
                pose.popPose();
            }
            pose.popPose();
        }
        pose.popPose();
    }
}
