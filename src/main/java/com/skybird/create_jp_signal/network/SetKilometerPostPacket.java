package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockEntity;
import com.skybird.create_jp_signal.menu.KilometerPostMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetKilometerPostPacket(BlockPos pos, int distance, boolean useLetterO) {
    public static void encode(SetKilometerPostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.distance);
        buffer.writeBoolean(packet.useLetterO);
    }
    public static SetKilometerPostPacket decode(FriendlyByteBuf buffer) {
        return new SetKilometerPostPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readBoolean());
    }
    public static void handle(SetKilometerPostPacket packet, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !player.mayBuild() || packet.distance < 0 || packet.distance > 999) return;
            if (!(player.containerMenu instanceof KilometerPostMenu menu) || !menu.pos.equals(packet.pos) || !menu.stillValid(player)) return;
            if (player.level().getBlockEntity(packet.pos) instanceof KilometerPostBlockEntity marker) marker.setDisplayData(packet.distance, packet.useLetterO);
        });
        context.setPacketHandled(true);
    }
}
