package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class BlockSignalNumberMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final int number;
    public final boolean useLetterO;
    private final RailwaySignBlockEntity sign;
    public BlockSignalNumberMenu(int id, Inventory inventory, RailwaySignBlockEntity sign) {
        super(AllMenuTypes.BLOCK_SIGNAL_NUMBER_MENU.get(), id);
        this.sign = sign; pos = sign.getBlockPos();
        number = sign.settings().number(); useLetterO = sign.settings().useLetterO();
    }
    public BlockSignalNumberMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(AllMenuTypes.BLOCK_SIGNAL_NUMBER_MENU.get(), id);
        sign = null; pos = buffer.readBlockPos(); number = buffer.readVarInt();
        useLetterO = buffer.readBoolean();
    }
    @Override public boolean stillValid(Player player) {
        return sign != null && !sign.isRemoved() && sign.signType().numberedMast()
            && player.level() == sign.getLevel() && player.level().getBlockEntity(pos) == sign
            && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
