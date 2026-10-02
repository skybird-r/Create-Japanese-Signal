package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;

import com.skybird.create_jp_signal.block.track.ReservationLimitBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SetReservationLimitPacket {

    private final BlockPos pos;
    private final int reservationLimit;

    public SetReservationLimitPacket(BlockPos pos, int reservationLimit) {
        this.pos = pos;
        this.reservationLimit = reservationLimit;
    }

    public SetReservationLimitPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readVarInt());
    }

    public static void encode(SetReservationLimitPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.reservationLimit);
    }

    public static SetReservationLimitPacket decode(FriendlyByteBuf buffer) {
        return new SetReservationLimitPacket(buffer);
    }

    public static void handle(SetReservationLimitPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.blockPosition().distSqr(packet.pos) > 64)
                return;
            Level level = player.level();
            if (level.isLoaded(packet.pos)
                && level.getBlockEntity(packet.pos) instanceof ReservationLimitBlockEntity blockEntity)
                blockEntity.setReservationLimit(packet.reservationLimit);
        });
        context.setPacketHandled(true);
    }
}
