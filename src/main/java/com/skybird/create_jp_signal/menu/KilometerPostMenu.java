package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class KilometerPostMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final boolean useLetterO;
    private final KilometerPostBlockEntity marker;
    private final DataSlot distance = DataSlot.standalone();

    public KilometerPostMenu(int id, Inventory inventory, KilometerPostBlockEntity marker) {
        super(AllMenuTypes.KILOMETER_POST_MENU.get(), id);
        this.marker = marker;
        this.pos = marker.getBlockPos();
        useLetterO = marker.useLetterO();
        distance.set(marker.getDistance());
        addDataSlot(distance);
    }

    public KilometerPostMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(AllMenuTypes.KILOMETER_POST_MENU.get(), id);
        pos = buffer.readBlockPos();
        marker = null;
        distance.set(buffer.readVarInt());
        useLetterO = buffer.readBoolean();
        addDataSlot(distance);
    }

    public int getDistance() { return distance.get(); }

    @Override public boolean stillValid(Player player) {
        return marker != null && !marker.isRemoved() && player.level() == marker.getLevel()
            && player.level().getBlockEntity(pos) == marker && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
