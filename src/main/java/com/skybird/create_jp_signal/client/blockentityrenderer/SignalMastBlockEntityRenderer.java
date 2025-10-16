package com.skybird.create_jp_signal.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.SignalMastBlock;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.client.ModelRegistry;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SignalMastBlockEntityRenderer implements BlockEntityRenderer<SignalMastBlockEntity> {

    public SignalMastBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public int getViewDistance() {
        return net.minecraft.client.Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    
    //private static final ResourceLocation SIGNALMAST_TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/block/signalmast");

    @Override
    public void render(SignalMastBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        BlockState blockState = pBlockEntity.getBlockState();
        ISignalType signalType = pBlockEntity.getSignalType();
        Map<AttachmentSlot, SignalHead> heads = pBlockEntity.getSignalHeads();

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        pPoseStack.pushPose();

        int rotation = blockState.getValue(SignalMastBlock.ROTATION);
        int xPos = blockState.getValue(SignalMastBlock.X_POS);
        int zPos = blockState.getValue(SignalMastBlock.Z_POS);

        pPoseStack.translate((float)xPos / 16F, 0, (float)zPos / 16F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - (rotation * 22.5F)));
        pPoseStack.pushPose();
        
        {
            blockRenderer.getModelRenderer().tesselateWithAO(
                pBlockEntity.getLevel(), ModelRegistry.signalMast, blockState, pBlockEntity.getBlockPos(), pPoseStack,
                pBufferSource.getBuffer(RenderType.cutout()), false, pBlockEntity.getLevel().getRandom(),
                pPackedLight, pPackedOverlay
            );
        }

        pPoseStack.popPose();

        if (signalType == null || heads.isEmpty()) {
            pPoseStack.popPose();
            return;
        }


        ISignalHeadRenderer headRenderer = signalType.getRenderer();

        for (Map.Entry<AttachmentSlot, SignalHead> entry : heads.entrySet()) {
            pPoseStack.pushPose(); 
            {

                if (entry.getKey() == AttachmentSlot.SECONDARY) {
                    pPoseStack.translate(0.3, 0, 0.2);
                } else if (heads.size() > 1) {
                    pPoseStack.translate(-0.3, 0, 0.2);
                } else {
                    pPoseStack.translate(0, 0, 0.2);
                }
                
                // 専門家に描画を委譲
                headRenderer.render(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, entry.getValue(), pBlockEntity, pPackedLight, pPackedOverlay, Vec3.ZERO, Pair.of(0.0, 0.0));
            }
            pPoseStack.popPose();
        }
        
        pPoseStack.popPose();
    }
}