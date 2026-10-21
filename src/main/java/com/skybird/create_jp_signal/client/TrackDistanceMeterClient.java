package com.skybird.create_jp_signal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.skybird.create_jp_signal.item.TrackDistanceMeterItem;
import com.skybird.create_jp_signal.network.MeasureTrackCurvePacket;
import com.skybird.create_jp_signal.network.PacketHandler;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "create_jp_signal", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TrackDistanceMeterClient {
    private TrackDistanceMeterClient() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCurveClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.player.isSpectator()) return;
        InteractionHand hand = event.getHand();
        if (!(mc.player.getItemInHand(hand).getItem() instanceof TrackDistanceMeterItem)) return;
        // Sneak-use follows vanilla air/block use so resetting needs no curve target.
        if (mc.player.isShiftKeyDown()) return;
        var selection = TrackBlockOutline.result;
        if (selection == null) return;
        // Create handles curves separately from vanilla block hits; claim this input
        // before its generic curved-track handler or the ordinary air-use path.
        event.setCanceled(true);
        event.setSwingHand(true);
        if (mc.player.getCooldowns().isOnCooldown(mc.player.getItemInHand(hand).getItem())) return;
        PacketHandler.sendToServer(new MeasureTrackCurvePacket(selection.blockEntity().getBlockPos(),
            selection.loc().curveTarget(), selection.loc().segment(), hand, mc.player.getInventory().selected));
    }

    @SubscribeEvent
    public static void renderStart(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = mc.player.getItemInHand(hand);
            if (!(stack.getItem() instanceof TrackDistanceMeterItem)) continue;
            CompoundTag start = TrackDistanceMeterItem.getStart(stack);
            if (start == null || !start.getString("Dimension").equals(mc.level.dimension().location().toString())) continue;
            Vec3 anchor = VecHelper.readNBT(start.getList("Marker", 6));
            if (anchor.distanceToSqr(event.getCamera().getPosition()) > 128 * 128) continue;
            PoseStack pose = event.getPoseStack();
            Vec3 offset = anchor.subtract(event.getCamera().getPosition()).add(0, .15, 0);
            pose.pushPose();
            pose.translate(offset.x, offset.y, offset.z);
            VertexConsumer lines = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
            // A small golden pole and flag, rendered locally without placing a block/entity.
            LevelRenderer.renderLineBox(pose, lines, new AABB(-.025, 0, -.025, .025, .85, .025), 1, .85f, .15f, 1);
            LevelRenderer.renderLineBox(pose, lines, new AABB(.025, .55, -.015, .38, .85, .015), 1, .45f, .1f, 1);
            pose.popPose();
        }
        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
    }
}
