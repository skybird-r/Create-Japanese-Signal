package com.skybird.create_jp_signal.menu;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ControlBoxMenu extends AbstractContainerMenu {

    public final ControlBoxBlockEntity blockEntity;
    public final ISignalAppearance initialAppearance;

    public ControlBoxMenu(int pContainerId, Inventory inv, FriendlyByteBuf buf) {
        super(AllMenuTypes.CONTROL_BOX_MENU.get(), pContainerId);
        
        BlockPos bePos = buf.readBlockPos();
        this.blockEntity = (ControlBoxBlockEntity) inv.player.level().getBlockEntity(bePos);
    
        String typeId = buf.readUtf();
        CompoundTag tag = buf.readNbt();
    
        if (!typeId.isEmpty()) {
            this.initialAppearance = AllSignalTypes.createAppearanceFromId(typeId, tag);
        } else {
            this.initialAppearance = null;
        }
    }

    public ControlBoxMenu(int pContainerId, Inventory inv, BlockEntity be) {
        super(AllMenuTypes.CONTROL_BOX_MENU.get(), pContainerId);
        this.blockEntity = (ControlBoxBlockEntity) be;
        Level level = blockEntity.getLevel();
        if (level != null && !level.isClientSide() && blockEntity.isLinked()) {
            BlockPos signalPos = blockEntity.getLinkedSignalPos();
            UUID headId = blockEntity.getLinkedHeadId();

            if (level.isLoaded(signalPos) && level.getBlockEntity(signalPos) instanceof BaseSignalBlockEntity signal) {
                SignalHead head = signal.getSignalHeads().values().stream()
                    .filter(h -> h.getUniqueId().equals(headId))
                    .findFirst().orElse(null);
                
                if (head != null && head.getAppearance() != null) {
                    blockEntity.setAppearance(head.getAppearance().copy());
                }
            }
        }
        this.initialAppearance = ((ControlBoxBlockEntity) be).getAppearance();
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        return blockEntity.getBlockState().is(AllBlocks.CONTROL_BOX.get());
    }

    @Override
    public ItemStack quickMoveStack(@Nonnull Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }
}