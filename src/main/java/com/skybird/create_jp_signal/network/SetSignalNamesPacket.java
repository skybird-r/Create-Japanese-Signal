package com.skybird.create_jp_signal.network;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import com.skybird.create_jp_signal.menu.SignalNameMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetSignalNamesPacket(BlockPos pos, int containerId, Map<UUID, String> names) {
    public static void encode(SetSignalNamesPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.containerId);
        buffer.writeVarInt(packet.names.size());
        packet.names.forEach((id, name) -> { buffer.writeUUID(id); buffer.writeUtf(name, 64); });
    }
    public static SetSignalNamesPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int container = buffer.readVarInt(), count = buffer.readVarInt();
        if (count < 1 || count > 2) throw new IllegalArgumentException("Invalid head count");
        Map<UUID, String> names = new java.util.HashMap<>();
        for (int i = 0; i < count; i++) {
            UUID id = buffer.readUUID();
            if (names.put(id, buffer.readUtf(64)) != null) throw new IllegalArgumentException("Duplicate head");
        }
        return new SetSignalNamesPacket(pos, container, Map.copyOf(names));
    }
    public static void handle(SetSignalNamesPacket packet, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !player.mayBuild() || player.isSpectator()) return;
            if (!(player.containerMenu instanceof SignalNameMenu menu) || menu.containerId != packet.containerId
                || !menu.pos.equals(packet.pos) || !menu.stillValid(player)) return;
            var expected = menu.entries.stream().map(SignalNameMenu.Entry::id).collect(java.util.stream.Collectors.toSet());
            if (!expected.equals(packet.names.keySet())) return;
            menu.signal().updateSignalNames(packet.names);
        });
        context.setPacketHandled(true);
    }
}
