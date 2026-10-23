package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalName;
import com.skybird.create_jp_signal.client.text.FlywheelText.TextModel;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.phys.Vec3;

/** All dimensions are relative to the same local origin as the lamp boxes. */
public final class SignalNameBoardLayout {
    private SignalNameBoardLayout() {}

    public static boolean visible(SignalHead head) {
        return !head.getSignalName().isEmpty() && head.getAppearance() instanceof ColorLightSignalAppearance appearance
            && appearance.getSignalSize() == ColorLightSignalAppearance.SignalSize.NORMAL
            && appearance.getBackplateType() == ColorLightSignalAppearance.BackplateType.ROUND;
    }

    public static int lampCount(SignalHead head) {
        return head.getCurrentAspect().getLampCount()
            + (((ColorLightSignalAppearance) head.getAppearance()).isRepeater() ? 1 : 0);
    }

    public static void head(PoseStack pose, Vec3 offset, Pair<Double, Double> rotation) {
        pose.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
        pose.translate(offset.x, offset.y, offset.z);
        pose.mulPose(Axis.XP.rotationDegrees(rotation.getSecond().floatValue()));
    }

    public static void post(PoseStack pose, int lamps, int cells) {
        double bottom = SignalName.boxTopPixels(lamps);
        double height = SignalName.boardBottomPixels(lamps) + SignalName.boardHeightPixels(cells) - bottom;
        // The backplate starts at Z = 1.5 px; the post front touches its rear face.
        pose.translate(-.5, bottom / 16, -3.0 / 16);
        pose.scale(1, (float)(height / 16), 1);
    }

    public static void board(PoseStack pose, int lamps, int cells) {
        pose.translate(-.5, SignalName.boardBottomPixels(lamps) / 16, -3.0 / 16);
        pose.scale(1, (float)(SignalName.boardHeightPixels(cells) / 16), 1);
    }

    public static void cell(PoseStack pose, int lamps, int count, int index,
            SignalName.Cell cell, TextModel glyph) {
        double top = SignalName.boardBottomPixels(lamps) + SignalName.boardHeightPixels(count);
        double y = top - 1 - 4 * index - 2;
        // 3.25 model pixels per eight font pixels. Only non-paired characters are stretched.
        double scaleY = 3.25 / 8 / 16;
        double scaleX = scaleY * (cell.horizontalDigits() ? 1 : 1.5);
        double inkWidth = glyph.maxX() - glyph.minX();
        double inkHeight = glyph.maxY() - glyph.minY();
        double fit = inkWidth == 0 ? 1 : Math.min(1, (5.0 / 16) / (inkWidth * scaleX));
        if (inkHeight > 0) fit = Math.min(fit, (3.25 / 16) / (inkHeight * scaleY));
        pose.translate(0, y / 16, 2.125 / 16);
        pose.scale((float)(scaleX * fit), (float)(-scaleY * fit), (float)scaleY);
        // Glyph quads include transparent right padding. Center the font's visible
        // advance, as on the kilometer posts, rather than the padded quad bounds.
        pose.translate(-(glyph.advance() - 1) / 2.0, -glyph.centerY(), 0);
    }
}
