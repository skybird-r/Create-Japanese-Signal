package com.skybird.create_jp_signal.network;

import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class UpdateControlBoxDataPacket {
    private final BlockPos pos;
    private final String appearanceTypeId;
    private final CompoundTag appearanceData;
    private final Map<BlockPos, AspectMapping> sourceMappings;

    public UpdateControlBoxDataPacket(BlockPos pos, ISignalAppearance appearance, Map<BlockPos, AspectMapping> sourceMappings) {
        this.pos = pos;
        this.appearanceTypeId = appearance.getTypeId();
        this.appearanceData = new CompoundTag();
        appearance.writeNbt(this.appearanceData);
        this.sourceMappings = sourceMappings;
    }
    
    private UpdateControlBoxDataPacket(BlockPos pos, String id, CompoundTag appearance, Map<BlockPos, AspectMapping> mappings) {
        this.pos = pos;
        this.appearanceTypeId = id;
        this.appearanceData = appearance;
        this.sourceMappings = mappings;
    }

    public static void encode(UpdateControlBoxDataPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.appearanceTypeId);
        buffer.writeNbt(packet.appearanceData);

        buffer.writeVarInt(packet.sourceMappings.size());
        packet.sourceMappings.forEach((sourcePos, mapping) -> {
            buffer.writeBlockPos(sourcePos);
            CompoundTag mappingTag = new CompoundTag();
            mapping.writeNbt(mappingTag);
            buffer.writeNbt(mappingTag);
        });
    }

    public static UpdateControlBoxDataPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        String id = buffer.readUtf();
        CompoundTag appearance = buffer.readNbt();
        
        Map<BlockPos, AspectMapping> mappings = new HashMap<>();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            BlockPos sourcePos = buffer.readBlockPos();
            CompoundTag mappingTag = buffer.readNbt();
            if (mappingTag != null) {
                mappings.put(sourcePos, AspectMapping.fromNbt(mappingTag));
            }
        }
        return new UpdateControlBoxDataPacket(pos, id, appearance, mappings);
    }

    public static void handle(UpdateControlBoxDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || !player.level().isLoaded(packet.pos)) return;
    
            if (player.level().getBlockEntity(packet.pos) instanceof ControlBoxBlockEntity be) {
                ISignalAppearance newAppearance = AllSignalTypes.createAppearanceFromId(packet.appearanceTypeId, packet.appearanceData);
    
                be.setAppearance(newAppearance);
                be.setSourceMappings(packet.sourceMappings);
            }
        });
        context.get().setPacketHandled(true);
    }
}