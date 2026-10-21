package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.skybird.create_jp_signal.item.TrackDistanceMeterItem;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;

public record MeasureTrackCurvePacket(BlockPos pos, BlockPos target, int segment, InteractionHand hand, int slot) {
    public static void encode(MeasureTrackCurvePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeBlockPos(packet.target);
        buf.writeVarInt(packet.segment);
        buf.writeEnum(packet.hand);
        buf.writeVarInt(packet.slot);
    }

    public static MeasureTrackCurvePacket decode(FriendlyByteBuf buf) {
        return new MeasureTrackCurvePacket(buf.readBlockPos(), buf.readBlockPos(), buf.readVarInt(),
            buf.readEnum(InteractionHand.class), buf.readVarInt());
    }

    public static void handle(MeasureTrackCurvePacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isSpectator() || player.getInventory().selected != packet.slot) return;
            ItemStack stack = player.getItemInHand(packet.hand);
            if (!(stack.getItem() instanceof TrackDistanceMeterItem)) return;
            if (!player.level().hasChunkAt(packet.pos)) return;
            if (!(player.level().getBlockEntity(packet.pos) instanceof TrackBlockEntity be)) return;
            BezierConnection connection = be.getConnections().get(packet.target);
            if (connection == null || !connection.isPrimary() || packet.segment < 0
                || packet.segment >= connection.getSegmentCount() - 1) return;
            Vec3 selected = TrackDistanceMeterItem.curvePoint(connection, packet.segment);
            double reach = player.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + 2;
            if (player.getEyePosition().distanceToSqr(selected) > reach * reach) return;
            TrackDistanceMeterItem.select(player.level(), player, stack, packet.pos,
                new BezierTrackPointLocation(packet.target, packet.segment));
        });
        context.setPacketHandled(true);
    }
}
