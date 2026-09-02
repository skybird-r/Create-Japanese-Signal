package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;

import com.skybird.create_jp_signal.block.track.PenaltyBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SetPenaltyPacket {

    private final BlockPos pos;
    private final int penalty;

    public SetPenaltyPacket(BlockPos pos, int penalty) {
        this.pos = pos;
        this.penalty = penalty;
    }

    public SetPenaltyPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readVarInt());
    }

    public static void encode(SetPenaltyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.penalty);
    }

    public static SetPenaltyPacket decode(FriendlyByteBuf buffer) {
        return new SetPenaltyPacket(buffer);
    }

    public static void handle(SetPenaltyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.blockPosition().distSqr(packet.pos) > 64)
                return;
            Level level = player.level();
            if (level.isLoaded(packet.pos) && level.getBlockEntity(packet.pos) instanceof PenaltyBlockEntity blockEntity)
                blockEntity.setPenalty(packet.penalty);
        });
        context.setPacketHandled(true);
    }
}
