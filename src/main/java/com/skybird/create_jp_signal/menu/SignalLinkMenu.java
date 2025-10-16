package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlock;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class SignalLinkMenu extends AbstractContainerMenu {
    public final BaseSignalBlockEntity blockEntity;
    public final BlockPos controlBoxPos;

    public SignalLinkMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(AllMenuTypes.SIGNAL_LINK_MENU.get(), pContainerId);


        final BlockPos signalPos = extraData.readBlockPos();
        final BlockPos controlBoxPos = extraData.readBlockPos();

        Level level = inv.player.level();
        BlockEntity be = level.getBlockEntity(signalPos);
        
        if (be instanceof BaseSignalBlockEntity signalBE) {
            this.blockEntity = signalBE;
        } else {
            // エラーが発生した場合、ダミーのBlockEntityを一時的に設定してクラッシュを防ぐ (GUIは正しく機能しない)
            this.blockEntity = null;
        }
        
        this.controlBoxPos = controlBoxPos;
    }

    public SignalLinkMenu(int pContainerId, Inventory inv, BaseSignalBlockEntity be, BlockPos controlBoxPos) {
        super(AllMenuTypes.SIGNAL_LINK_MENU.get(), pContainerId);
        this.blockEntity = be;
        this.controlBoxPos = controlBoxPos;
    }


    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.blockEntity == null || this.blockEntity.isRemoved()) {
            return false;
        }

        Level level = pPlayer.level();
        BlockPos pos = this.blockEntity.getBlockPos();

        if (!(level.getBlockState(pos).getBlock() instanceof BaseSignalBlock)) {
            return false;
        }

        // プレイヤーブロック距離
        return pPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}