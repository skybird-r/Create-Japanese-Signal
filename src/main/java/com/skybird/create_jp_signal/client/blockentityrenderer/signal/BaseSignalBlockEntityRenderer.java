package com.skybird.create_jp_signal.client.blockentityrenderer.signal;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.client.ModelRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BaseSignalBlockEntityRenderer implements BlockEntityRenderer<BaseSignalBlockEntity> {

    public BaseSignalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public int getViewDistance() {
        return net.minecraft.client.Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    @Override
    public void render(BaseSignalBlockEntity be, float pPartialTick, PoseStack pPoseStack,
            MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ISignalType signalType = be.getSignalType();
        Map<AttachmentSlot, SignalHead> heads = be.getSignalHeads();

        if (signalType == null || heads.isEmpty()) {
            return;
        }
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        {
            pPoseStack.pushPose();

            if (be instanceof BaseSignalMastBlockEntity mast) {
                BlockState blockState = mast.getBlockState();
                int rotation = mast.getRotation();
                int xPos = mast.getXPos();
                int zPos = mast.getZPos();

                int cappedRotation = (rotation + 2) % 4 - 2;

                pPoseStack.translate((float)xPos / 16F, 0, (float)zPos / 16F);
                
                {
                    pPoseStack.pushPose();
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - (rotation * 22.5F)));
                    pPoseStack.translate(-0.5, 0, -0.5);
                    blockRenderer.getModelRenderer().tesselateWithAO(
                        be.getLevel(), ModelRegistry.signalMast, blockState, be.getBlockPos(), pPoseStack,
                        pBufferSource.getBuffer(RenderType.cutout()), false, be.getLevel().getRandom(),
                        pPackedLight, pPackedOverlay
                    );
                    pPoseStack.popPose();
                }
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - (rotation * 22.5F)));
            }

            ISignalHeadRenderer headRenderer = signalType.getRenderer();

            for (Map.Entry<AttachmentSlot, SignalHead> entry : heads.entrySet()) {
                pPoseStack.pushPose();
                Vec3 offset = be.getHeadOffset(entry.getKey());
                Pair<Double, Double> rotation = be.getHeadRotation(entry.getKey());
                headRenderer.render(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, entry.getValue(), be, pPackedLight, pPackedOverlay, offset, rotation);
                pPoseStack.popPose();
            }
            
            pPoseStack.popPose();
        }
    }
}
    

