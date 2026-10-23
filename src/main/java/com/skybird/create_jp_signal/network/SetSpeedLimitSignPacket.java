package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import com.skybird.create_jp_signal.block.sign.RailwaySignSettings.Variant;
import com.skybird.create_jp_signal.menu.SpeedLimitSignMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetSpeedLimitSignPacket(BlockPos pos, int speed, Variant variant, boolean useLetterO) {
    public static void encode(SetSpeedLimitSignPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos); buffer.writeVarInt(packet.speed);
        buffer.writeEnum(packet.variant); buffer.writeBoolean(packet.useLetterO);
    }
    public static SetSpeedLimitSignPacket decode(FriendlyByteBuf buffer) {
        return new SetSpeedLimitSignPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readEnum(Variant.class), buffer.readBoolean());
    }
    public static void handle(SetSpeedLimitSignPacket packet, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !player.mayBuild() || packet.speed < 5 || packet.speed > 500) return;
            if (!(player.containerMenu instanceof SpeedLimitSignMenu menu) || !menu.pos.equals(packet.pos) || !menu.stillValid(player)) return;
            if (player.level().getBlockEntity(packet.pos) instanceof RailwaySignBlockEntity sign)
                sign.configureSpeed(packet.speed, packet.variant, packet.useLetterO);
        });
        context.setPacketHandled(true);
    }
}
