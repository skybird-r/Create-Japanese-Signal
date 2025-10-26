package com.skybird.create_jp_signal.network;

import java.util.function.Supplier;

import com.skybird.create_jp_signal.item.SignalMastWithSignalItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class SelectSignalMastPacket {
    private final String blockId;

    public SelectSignalMastPacket(String blockId) {
        this.blockId = blockId;
    }

    public static void encode(SelectSignalMastPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.blockId);
    }

    public static SelectSignalMastPacket decode(FriendlyByteBuf buf) {
        return new SelectSignalMastPacket(buf.readUtf());
    }

    public static void handle(SelectSignalMastPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            // プレイヤーが正しいアイテムを持っているかサーバー側で検証
            if (stack.getItem() instanceof SignalMastWithSignalItem) {
                stack.getOrCreateTag().putString("SelectedBlockType", packet.blockId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
