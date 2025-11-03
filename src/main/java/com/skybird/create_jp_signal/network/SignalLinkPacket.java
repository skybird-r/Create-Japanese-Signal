package com.skybird.create_jp_signal.network;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import com.skybird.create_jp_signal.util.Lang;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class SignalLinkPacket {
    private final BlockPos controlBoxPos;
    private final BlockPos signalPos;
    private final UUID headIdToClaim;

    public SignalLinkPacket(BlockPos controlBoxPos, BlockPos signalPos, UUID headIdToClaim) {
        this.controlBoxPos = controlBoxPos;
        this.signalPos = signalPos;
        this.headIdToClaim = headIdToClaim;
    }

    public static void encode(SignalLinkPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.controlBoxPos);
        buffer.writeBlockPos(packet.signalPos);
        buffer.writeUUID(packet.headIdToClaim);
    }

    public static SignalLinkPacket decode(FriendlyByteBuf buffer) {
        return new SignalLinkPacket(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readUUID());
    }

    public static void handle(SignalLinkPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || !player.level().isLoaded(packet.controlBoxPos) || !player.level().isLoaded(packet.signalPos)) {
                return;
            }
    
            BlockEntity controlBoxBE = player.level().getBlockEntity(packet.controlBoxPos);
            BlockEntity signalBE = player.level().getBlockEntity(packet.signalPos);

            if (controlBoxBE instanceof ControlBoxBlockEntity controlBox && signalBE instanceof BaseSignalBlockEntity signal) {
                
                boolean success = controlBox.linkToHead(signal, packet.headIdToClaim);
            
                if (success) {
                    player.displayClientMessage(Lang.translatable("network.signal_link.success"), true);
                } else {
                    player.displayClientMessage(Lang.translatable("network.signal_link.fail"), true);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}