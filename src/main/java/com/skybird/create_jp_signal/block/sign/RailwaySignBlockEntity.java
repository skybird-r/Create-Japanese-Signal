package com.skybird.create_jp_signal.block.sign;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.menu.SpeedLimitSignMenu;
import com.skybird.create_jp_signal.menu.BlockSignalNumberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RailwaySignBlockEntity extends BlockEntity implements MenuProvider {
    private final RailwaySignSettings settings = new RailwaySignSettings();
    public boolean clientVisualChanged = true;
    public RailwaySignBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.RAILWAY_SIGN_ENTITY.get(), pos, state);
    }
    public RailwaySignSettings settings() { return settings; }
    public RailwaySignType signType() { return ((RailwaySignBlock)getBlockState().getBlock()).signType(); }
    public void setPlacementData(int rotation, int x, int z) { settings.placement(rotation, x, z); notifyUpdate(); }
    public void cycleHeight() { settings.cycleHeight(); notifyUpdate(); }
    public void configureSpeed(int speed, RailwaySignSettings.Variant variant, boolean useLetterO) {
        settings.configure(speed, variant, useLetterO); notifyUpdate();
    }
    public void configureNumber(int number, boolean useLetterO) {
        settings.configureNumber(number, useLetterO); notifyUpdate();
    }
    private void notifyUpdate() {
        setChanged(); clientVisualChanged = true;
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); settings.save(tag); }
    @Override public void load(CompoundTag tag) { super.load(tag); settings.load(tag); clientVisualChanged = true; }
    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public Component getDisplayName() { return Component.translatable(getBlockState().getBlock().getDescriptionId()); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return signType().numberedMast() ? new BlockSignalNumberMenu(id, inventory, this)
            : new SpeedLimitSignMenu(id, inventory, this);
    }
    @Override public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - .5, worldPosition.getY() - .875, worldPosition.getZ() - .5,
            worldPosition.getX() + 1.5, worldPosition.getY() + 2.25, worldPosition.getZ() + 1.5);
    }
}
