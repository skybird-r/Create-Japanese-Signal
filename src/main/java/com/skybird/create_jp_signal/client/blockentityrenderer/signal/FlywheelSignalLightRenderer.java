package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.ModelRegistry;

import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Renders only the frequently-changing full-bright lights. Flywheel owns the
 * static signal geometry while a visualization is active.
 */
final class FlywheelSignalLightRenderer {

    private static final List<Vec3> SHUNT_LIGHT_POSITIONS = List.of(
        new Vec3(0, 0, 0),
        new Vec3(5.0 / 16, 0, 0),
        new Vec3(3.5 / 16, 3.5 / 16, 0),
        new Vec3(0, 5.0 / 16, 0)
    );

    private FlywheelSignalLightRenderer() {
    }

    static void renderColor(PoseStack poseStack, MultiBufferSource bufferSource, int overlay,
            SignalHead head, BlockEntity blockEntity, Vec3 offset, Pair<Double, Double> rotation,
            ColorLightSignalAppearance appearance) {
        SignalAspect.State aspect = head.getCurrentAspect();
        long gameTime = Minecraft.getInstance().level.getGameTime();

        poseStack.pushPose();
        rotateAndOffset(poseStack, offset, rotation);

        float lampHeight = appearance.getSignalSize() == ColorLightSignalAppearance.SignalSize.NORMAL ? 5 : 4;
        double boxOffset = appearance.getSignalSize() == ColorLightSignalAppearance.SignalSize.NORMAL ? 3.5 : 0;
        int lampCount = aspect.getLampCount() + (appearance.isRepeater() ? 1 : 0);

        poseStack.pushPose();
        poseStack.translate(0, (boxOffset + 0.25) / 16, 1.75 / 16);
        poseStack.scale(lampHeight - 0.5f, lampHeight - 0.5f, lampHeight - 0.5f);
        for (int i = 0; i < lampCount; i++) {
            LampColor color = appearance.isRepeater()
                ? i == 0 ? LampColor.PURPLE : aspect.getLampColor(i - 1, gameTime)
                : aspect.getLampColor(i, gameTime);
            renderLight(poseStack, bufferSource, overlay, blockEntity, color);
            poseStack.translate(0, lampHeight / 16 / (lampHeight - 0.5), 0);
        }
        poseStack.popPose();

        renderAccessory(poseStack, bufferSource, overlay, head, blockEntity, appearance.getAccessory().getType());
        poseStack.popPose();
    }

    static void renderShunt(PoseStack poseStack, MultiBufferSource bufferSource, int overlay,
            SignalHead head, BlockEntity blockEntity, Vec3 offset, Pair<Double, Double> rotation,
            PositionLightShuntSignalAppearance appearance) {
        SignalAspect.State aspect = head.getCurrentAspect();
        long gameTime = Minecraft.getInstance().level.getGameTime();

        poseStack.pushPose();
        rotateAndOffset(poseStack, offset, rotation);

        poseStack.pushPose();
        poseStack.translate(-2.5 / 16, 3.0 / 16, 1.75 / 16);
        for (int i = 0; i < aspect.getLampCount(); i++) {
            poseStack.pushPose();
            poseStack.translate(SHUNT_LIGHT_POSITIONS.get(i).x, SHUNT_LIGHT_POSITIONS.get(i).y,
                SHUNT_LIGHT_POSITIONS.get(i).z);
            poseStack.scale(2.5f, 2.5f, 2.5f);
            renderLight(poseStack, bufferSource, overlay, blockEntity, aspect.getLampColor(i, gameTime));
            poseStack.popPose();
        }
        poseStack.popPose();

        renderAccessory(poseStack, bufferSource, overlay, head, blockEntity, appearance.getAccessory().getType());
        poseStack.popPose();
    }

    private static void rotateAndOffset(PoseStack poseStack, Vec3 offset, Pair<Double, Double> rotation) {
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation.getFirst().floatValue()));
        poseStack.translate(offset.x, offset.y, offset.z);
    }

    private static void renderAccessory(PoseStack poseStack, MultiBufferSource bufferSource, int overlay,
            SignalHead head, BlockEntity blockEntity, SignalAccessory.Type accessory) {
        Iterator<LampColor> colors =
            SignalAccessory.getLampColors(accessory, head.getCurrentRoute()).iterator();

        switch (accessory) {
            case FORECAST -> {
                poseStack.pushPose();
                poseStack.translate(-7.0 / 16, (2.25 - 8.0) / 16, 1.75 / 16);
                poseStack.scale(3.5f, 3.5f, 3.5f);
                for (int i = 0; i < 2; i++) {
                    renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                    poseStack.translate(14.0 / 16 / 3.5, 0, 0);
                }
                poseStack.popPose();
            }
            case INDICATOR_HOME -> {
                poseStack.pushPose();
                poseStack.translate(-5.0 / 16, 0.75 / 16 - 1, 1.75 / 16);
                poseStack.scale(2.5f, 2.5f, 2.5f);
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < 3; column++) {
                        renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                        poseStack.translate(5.0 / 16 / 2.5, 0, 0);
                    }
                    poseStack.translate(-15.0 / 16 / 2.5, 5.0 / 16 / 2.5, 0);
                }
                poseStack.popPose();
            }
            case INDICATOR_DEPARTURE -> {
                poseStack.pushPose();
                poseStack.translate(0, (0.75 - 11.0) / 16, 1.75 / 16);
                poseStack.scale(2.5f, 2.5f, 2.5f);
                renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                poseStack.translate(-5.0 / 16 / 2.5, 5.0 / 16 / 2.5, 0);
                for (int i = 0; i < 3; i++) {
                    renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                    poseStack.translate(5.0 / 16 / 2.5, 0, 0);
                }
                poseStack.popPose();
            }
            case INDICATOR_SHUNT -> {
                poseStack.pushPose();
                poseStack.translate(0, (0.5 - 8.0) / 16, 1.75 / 16);
                poseStack.pushPose();
                poseStack.translate(-3.75 / 16, 0, 0);
                poseStack.scale(1.5f, 3, 1);
                for (int i = 0; i < 3; i++) {
                    renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                    poseStack.translate(3.75 / 16 / 1.5, 0, 0);
                }
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(0, 3.5 / 16, 0);
                poseStack.scale(9, 1.5f, 1);
                renderLight(poseStack, bufferSource, overlay, blockEntity, colors.next());
                poseStack.popPose();
                poseStack.popPose();
            }
        }
    }

    private static void renderLight(PoseStack poseStack, MultiBufferSource bufferSource, int overlay,
            BlockEntity blockEntity, LampColor color) {
        ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        modelRenderer.renderModel(
            poseStack.last(),
            bufferSource.getBuffer(RenderType.cutout()),
            blockEntity.getBlockState(),
            ModelRegistry.light,
            color.getRed(), color.getGreen(), color.getBlue(),
            LightTexture.FULL_BRIGHT,
            overlay
        );
    }
}
