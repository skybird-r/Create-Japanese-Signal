package com.skybird.create_jp_signal.network;

import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class UnlinkIndexPacket {
    private final BlockPos controlBoxPos;
    private final BlockPos indexPosToUnlink;

    public UnlinkIndexPacket(BlockPos controlBoxPos, BlockPos indexPosToUnlink) {
        this.controlBoxPos = controlBoxPos;
        this.indexPosToUnlink = indexPosToUnlink;
    }

    public static void encode(UnlinkIndexPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.controlBoxPos);
        buffer.writeBlockPos(packet.indexPosToUnlink);
    }

    public static UnlinkIndexPacket decode(FriendlyByteBuf buffer) {
        return new UnlinkIndexPacket(buffer.readBlockPos(), buffer.readBlockPos());
    }

    public static void handle(UnlinkIndexPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || !player.level().isLoaded(packet.controlBoxPos)) return;

            if (player.level().getBlockEntity(packet.controlBoxPos) instanceof ControlBoxBlockEntity be) {
                be.unlinkIndexSource(packet.indexPosToUnlink);
            }
        });
        context.get().setPacketHandled(true);
    }
}