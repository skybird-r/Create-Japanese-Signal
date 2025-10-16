package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class MastLinkMenu extends AbstractContainerMenu {
    public final SignalMastBlockEntity blockEntity;
    public final BlockPos controlBoxPos;

    public MastLinkMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(AllMenuTypes.MAST_LINK_MENU.get(), pContainerId);

        final BlockPos mastPos = extraData.readBlockPos();
        final BlockPos controlBoxPos = extraData.readBlockPos();

        Level level = inv.player.level();
        BlockEntity be = level.getBlockEntity(mastPos);
        
        if (be instanceof SignalMastBlockEntity mastBE) {
            this.blockEntity = mastBE;
        } else {
            this.blockEntity = null;
        }
        
        this.controlBoxPos = controlBoxPos;
    }
    /*
    public MastLinkMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, 
            (SignalMastBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), 
            extraData.readBlockPos()
        );
    }
    */

    public MastLinkMenu(int pContainerId, Inventory inv, SignalMastBlockEntity be, BlockPos controlBoxPos) {
        super(AllMenuTypes.MAST_LINK_MENU.get(), pContainerId);
        this.blockEntity = be;
        this.controlBoxPos = controlBoxPos;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity.getBlockState().is(AllBlocks.SIGNAL_MAST.get());
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}