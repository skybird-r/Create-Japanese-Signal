package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.track.SpeedLimitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpeedLimitMenu extends AbstractContainerMenu {
    
    public final SpeedLimitBlockEntity blockEntity;
    public final Level level;

    public SpeedLimitMenu(int pContainerId, Inventory inv, SpeedLimitBlockEntity be) {
        super(AllMenuTypes.SPEED_LIMIT_MENU.get(), pContainerId);
        this.blockEntity = be;
        this.level = be.getLevel();
    }

    public SpeedLimitMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, getBlockEntity(inv, extraData));
    }
    
    private static SpeedLimitBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf extraData) {
        Player player = inv.player;
        BlockEntity be = player.level().getBlockEntity(extraData.readBlockPos());
        return (be instanceof SpeedLimitBlockEntity) ? (SpeedLimitBlockEntity) be : null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.blockEntity != null && this.blockEntity.getBlockPos().distSqr(pPlayer.blockPosition()) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}