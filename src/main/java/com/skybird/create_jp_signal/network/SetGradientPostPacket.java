package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;
import com.skybird.create_jp_signal.block.sign.GradientPostBlockEntity;
import com.skybird.create_jp_signal.menu.GradientPostMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetGradientPostPacket(BlockPos pos, int front, int back, boolean showDecimals, boolean useLetterO, boolean tunnelMode) {
    public static void encode(SetGradientPostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.front);
        buffer.writeVarInt(packet.back);
        buffer.writeBoolean(packet.showDecimals);
        buffer.writeBoolean(packet.useLetterO);
        buffer.writeBoolean(packet.tunnelMode);
    }
    public static SetGradientPostPacket decode(FriendlyByteBuf buffer) {
        return new SetGradientPostPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }
    public static void handle(SetGradientPostPacket packet, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !player.mayBuild() || packet.front < -9999 || packet.front > 9999
                    || packet.back < -9999 || packet.back > 9999) return;
            if (!(player.containerMenu instanceof GradientPostMenu menu) || !menu.pos.equals(packet.pos) || !menu.stillValid(player)) return;
            if (player.level().getBlockEntity(packet.pos) instanceof GradientPostBlockEntity post)
                post.setGradients(packet.front, packet.back, packet.showDecimals, packet.useLetterO, packet.tunnelMode);
        });
        context.setPacketHandled(true);
    }
}
