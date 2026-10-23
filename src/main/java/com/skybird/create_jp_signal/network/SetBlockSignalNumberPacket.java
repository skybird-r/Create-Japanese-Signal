package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import com.skybird.create_jp_signal.menu.BlockSignalNumberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetBlockSignalNumberPacket(BlockPos pos, int number, boolean useLetterO) {
    public static void encode(SetBlockSignalNumberPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos); buffer.writeVarInt(packet.number);
        buffer.writeBoolean(packet.useLetterO);
    }
    public static SetBlockSignalNumberPacket decode(FriendlyByteBuf buffer) {
        return new SetBlockSignalNumberPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readBoolean());
    }
    public static void handle(SetBlockSignalNumberPacket packet, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !player.mayBuild() || packet.number < 0 || packet.number > 99) return;
            if (!(player.containerMenu instanceof BlockSignalNumberMenu menu) || !menu.pos.equals(packet.pos) || !menu.stillValid(player)) return;
            if (player.level().getBlockEntity(packet.pos) instanceof RailwaySignBlockEntity sign)
                sign.configureNumber(packet.number, packet.useLetterO);
        });
        context.setPacketHandled(true);
    }
}
