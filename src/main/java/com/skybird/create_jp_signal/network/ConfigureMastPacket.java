package com.skybird.create_jp_signal.network;

import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ConfigureMastPacket {
    private final BlockPos pos;
    private final ResourceLocation typeId;
    private final CompoundTag appearanceData;
    private final int headCount;

    public ConfigureMastPacket(BlockPos pos, ResourceLocation typeId, CompoundTag appearanceData, int headCount) {
        this.pos = pos;
        this.typeId = typeId;
        this.appearanceData = appearanceData;
        this.headCount = headCount;
    }

    public static void encode(ConfigureMastPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeResourceLocation(packet.typeId);
        buffer.writeNbt(packet.appearanceData);
        buffer.writeInt(packet.headCount);
    }

    public static ConfigureMastPacket decode(FriendlyByteBuf buffer) {
        return new ConfigureMastPacket(buffer.readBlockPos(), buffer.readResourceLocation(), buffer.readNbt(), buffer.readInt());
    }

    public static void handle(ConfigureMastPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || !player.level().isLoaded(packet.pos)) return;

            if (player.level().getBlockEntity(packet.pos) instanceof SignalMastBlockEntity be) {
                be.reconfigureMast(packet.typeId, packet.appearanceData, packet.headCount);
            }
        });
        context.get().setPacketHandled(true);
    }
}