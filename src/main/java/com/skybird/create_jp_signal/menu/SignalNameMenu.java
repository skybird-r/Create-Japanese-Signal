package com.skybird.create_jp_signal.menu;

import java.util.List;
import java.util.UUID;
import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity.AttachmentSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SignalNameMenu extends AbstractContainerMenu {
    public record Entry(AttachmentSlot slot, UUID id, String name) {}
    public final BlockPos pos;
    public final List<Entry> entries;
    private final BaseSignalBlockEntity signal;

    public SignalNameMenu(int id, Inventory inventory, BaseSignalBlockEntity signal) {
        super(AllMenuTypes.SIGNAL_NAME_MENU.get(), id);
        this.signal = signal;
        pos = signal.getBlockPos();
        entries = signal.getSignalHeads().entrySet().stream()
            .map(entry -> new Entry(entry.getKey(), entry.getValue().getUniqueId(), entry.getValue().getSignalName())).toList();
    }

    public SignalNameMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(AllMenuTypes.SIGNAL_NAME_MENU.get(), id);
        signal = null;
        pos = buffer.readBlockPos();
        int count = buffer.readVarInt();
        if (count < 1 || count > AttachmentSlot.values().length) throw new IllegalArgumentException("Invalid head count");
        var list = new java.util.ArrayList<Entry>();
        for (int i = 0; i < count; i++)
            list.add(new Entry(buffer.readEnum(AttachmentSlot.class), buffer.readUUID(), buffer.readUtf(64)));
        entries = List.copyOf(list);
    }

    public static void writeOpeningData(FriendlyByteBuf buffer, BaseSignalBlockEntity signal) {
        buffer.writeBlockPos(signal.getBlockPos());
        buffer.writeVarInt(signal.getSignalHeads().size());
        signal.getSignalHeads().forEach((slot, head) -> {
            buffer.writeEnum(slot);
            buffer.writeUUID(head.getUniqueId());
            buffer.writeUtf(head.getSignalName(), 64);
        });
    }

    public BaseSignalBlockEntity signal() { return signal; }

    @Override public boolean stillValid(Player player) {
        return signal != null && !signal.isRemoved() && player.level() == signal.getLevel()
            && player.level().getBlockEntity(pos) == signal
            && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64
            && entries.stream().allMatch(entry -> signal.getSignalHead(entry.id()) != null);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
