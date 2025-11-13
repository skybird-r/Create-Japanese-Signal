package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.track.SpeedLimitBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SetSpeedLimitPacket {
    private final BlockPos pos;
    private final double speed;
    private final double distance;

    public SetSpeedLimitPacket(BlockPos pos, double speed, double distance) {
        this.pos = pos;
        this.speed = speed;
        this.distance = distance;
    }

    public SetSpeedLimitPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.speed = buf.readDouble();
        this.distance = buf.readDouble();
    }

    public static void encode(SetSpeedLimitPacket packet, FriendlyByteBuf buf) {
        // JpSignals.LOGGER.info("Encode: " + Double.toString(packet.speed) + " " + Double.toString(packet.distance));
        buf.writeBlockPos(packet.pos);
        buf.writeDouble(packet.speed);
        buf.writeDouble(packet.distance);
    }

    public static SetSpeedLimitPacket decode(FriendlyByteBuf buffer) {
        // BlockPos pos = buffer.readBlockPos();
        // double speed = buffer.readDouble();
        // double distance = buffer.readDouble();
        // JpSignals.LOGGER.info("Decode: " + Double.toString(speed) + " " + Double.toString(distance));
        // return new SetSpeedLimitPacket(pos, speed, distance);
        return new SetSpeedLimitPacket(buffer.readBlockPos(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(SetSpeedLimitPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            
            if (level.isLoaded(packet.pos) && level.getBlockEntity(packet.pos) instanceof SpeedLimitBlockEntity be) {
                be.setSpeedLimitAndDistance(packet.speed, packet.distance);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
