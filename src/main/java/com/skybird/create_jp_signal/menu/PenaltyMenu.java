package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.track.PenaltyBlockEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PenaltyMenu extends AbstractContainerMenu {

    public final PenaltyBlockEntity blockEntity;
    public final Level level;

    public PenaltyMenu(int containerId, Inventory inventory, PenaltyBlockEntity blockEntity) {
        super(AllMenuTypes.PENALTY_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = blockEntity.getLevel();
    }

    public PenaltyMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, getBlockEntity(inventory, extraData));
    }

    private static PenaltyBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf extraData) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(extraData.readBlockPos());
        return blockEntity instanceof PenaltyBlockEntity penalty ? penalty : null;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.getBlockPos().distSqr(player.blockPosition()) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
