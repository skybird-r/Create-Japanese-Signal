package com.skybird.create_jp_signal.client.blockentityrenderer.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import com.skybird.create_jp_signal.block.sign.RailwaySignType;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.text.SignText;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RailwaySignRenderer implements BlockEntityRenderer<RailwaySignBlockEntity> {
    public RailwaySignRenderer(BlockEntityRendererProvider.Context context) { }
    private void model(PartialModel model, RailwaySignBlockEntity sign, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(),
            buffers.getBuffer(RenderType.cutout()), sign.getBlockState(), model.get(), 1, 1, 1, light, overlay);
    }
    @Override public void render(RailwaySignBlockEntity sign, float partialTick, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(sign.getLevel())) return;
        var settings = sign.settings();
        pose.pushPose(); RailwaySignLayout.placement(pose, settings);
        pose.pushPose(); RailwaySignLayout.pole(pose, settings, sign.signType());
        model(RailwaySignLayout.poleModel(sign.signType()), sign, pose, buffers, light, overlay); pose.popPose();
        pose.pushPose(); RailwaySignLayout.board(pose, settings, sign.signType());
        model(RailwaySignLayout.model(sign.signType(), settings.variant()), sign, pose, buffers, light, overlay); pose.popPose();
        if (sign.signType() == RailwaySignType.SPEED_LIMIT || sign.signType().numberedMast()) {
            RailwaySignLayout.numberFace(pose, settings, sign.signType());
            java.util.function.ToIntFunction<String> advance = text ->
                SignText.font().width(SignText.sequence(text, SignText.FontMode.MINECRAFT_ENGLISH));
            var digits = sign.signType().numberedMast()
                ? RailwaySignLayout.numberPlateDigits(settings.number(), settings.useLetterO(), advance)
                : RailwaySignLayout.digits(settings.speed(), settings.useLetterO(), advance);
            for (var digit : digits) {
                pose.pushPose(); RailwaySignLayout.digit(pose, digit);
                SignText.font().drawInBatch(SignText.sequence(digit.text(), SignText.FontMode.MINECRAFT_ENGLISH),
                    0, 0, sign.signType().numberedMast() ? 0xFFFFFFFF : 0xFF000000, false, pose.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
                pose.popPose();
            }
        }
        pose.popPose();
    }
}
