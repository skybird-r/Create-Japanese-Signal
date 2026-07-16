// package com.skybird.create_jp_signal.network;

// import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
// import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
// import java.util.UUID;
// import java.util.function.Supplier;
// import net.minecraft.core.BlockPos;
// import net.minecraft.network.FriendlyByteBuf;
// import net.minecraft.network.chat.Component;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.level.block.entity.BlockEntity;
// import net.minecraftforge.network.NetworkEvent;

// public class FinalizeLinkPacket {
//     private final BlockPos controlBoxPos;
//     private final BlockPos mastPos;
//     private final UUID headIdToClaim;

//     public FinalizeLinkPacket(BlockPos controlBoxPos, BlockPos mastPos, UUID headIdToClaim) {
//         this.controlBoxPos = controlBoxPos;
//         this.mastPos = mastPos;
//         this.headIdToClaim = headIdToClaim;
//     }

//     public static void encode(FinalizeLinkPacket packet, FriendlyByteBuf buffer) {
//         buffer.writeBlockPos(packet.controlBoxPos);
//         buffer.writeBlockPos(packet.mastPos);
//         buffer.writeUUID(packet.headIdToClaim);
//     }

//     public static FinalizeLinkPacket decode(FriendlyByteBuf buffer) {
//         return new FinalizeLinkPacket(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readUUID());
//     }

//     public static void handle(FinalizeLinkPacket packet, Supplier<NetworkEvent.Context> context) {
//         context.get().enqueueWork(() -> {
//             ServerPlayer player = context.get().getSender();
//             if (player == null || !player.level().isLoaded(packet.controlBoxPos) || !player.level().isLoaded(packet.mastPos)) {
//                 return;
//             }
    
//             BlockEntity controlBoxBE = player.level().getBlockEntity(packet.controlBoxPos);
//             BlockEntity be = player.level().getBlockEntity(packet.mastPos);

//             if (controlBoxBE instanceof ControlBoxBlockEntity controlBox && be instanceof BaseSignalBlockEntity signal) {
                
//                 boolean success = controlBox.linkToHead(signal, packet.headIdToClaim);
                
//                 if (success) {
//                     player.displayClientMessage(Component.literal("信号機に紐付け完了。"), true);
//                 } else {
//                     player.displayClientMessage(Component.literal("エラー: 紐付けに失敗しました。"), true);
//                 }
//             }
//         });
//         context.get().setPacketHandled(true);
//     }
// }