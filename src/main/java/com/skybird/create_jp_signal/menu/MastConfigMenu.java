package com.skybird.create_jp_signal.menu;

import javax.annotation.Nonnull;

import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MastConfigMenu extends AbstractContainerMenu {
    public final SignalMastBlockEntity blockEntity;

    public MastConfigMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public MastConfigMenu(int pContainerId, Inventory inv, BlockEntity be) {
        super(AllMenuTypes.MAST_CONFIG_MENU.get(), pContainerId);
        this.blockEntity = (SignalMastBlockEntity) be;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity.getBlockState().is(AllBlocks.SIGNAL_MAST.get());
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        // このGUIにはアイテムスロットがないため、空のItemStackを返すだけでOK
        return ItemStack.EMPTY;
    }
}