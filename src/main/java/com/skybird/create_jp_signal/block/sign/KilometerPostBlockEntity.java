package com.skybird.create_jp_signal.block.sign;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.menu.KilometerPostMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KilometerPostBlockEntity extends BlockEntity implements MenuProvider {
    private int rotation;
    private int xPos = 8;
    private int zPos = 8;
    private int distance;
    private boolean useLetterO;
    public boolean clientVisualChanged = true;

    public KilometerPostBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.KILOMETER_POST_ENTITY.get(), pos, state);
    }

    public boolean useLetterO() { return useLetterO; }

    public int getRotation() { return rotation; }
    public int getXPos() { return xPos; }
    public int getZPos() { return zPos; }
    public int getDistance() { return distance; }

    public void setPlacementData(int rotation, int x, int z) {
        this.rotation = rotation & 15;
        xPos = Mth.clamp(x, 0, 16);
        zPos = Mth.clamp(z, 0, 16);
        notifyUpdate();
    }

    public void setDisplayData(int distance, boolean useLetterO) {
        int value = Mth.clamp(distance, 0, 999);
        if (this.distance == value && this.useLetterO == useLetterO) return;
        this.distance = value;
        this.useLetterO = useLetterO;
        notifyUpdate();
    }

    private void notifyUpdate() {
        setChanged();
        clientVisualChanged = true;
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("UseLetterO", useLetterO);
        tag.putInt("Rotation", rotation);
        tag.putInt("XPos", xPos);
        tag.putInt("ZPos", zPos);
        tag.putInt("Distance", distance);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        useLetterO = tag.getBoolean("UseLetterO");
        rotation = tag.getInt("Rotation") & 15;
        xPos = tag.contains("XPos") ? Mth.clamp(tag.getInt("XPos"), 0, 16) : 8;
        zPos = tag.contains("ZPos") ? Mth.clamp(tag.getInt("ZPos"), 0, 16) : 8;
        distance = Mth.clamp(tag.getInt("Distance"), 0, 999);
        clientVisualChanged = true;
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public Component getDisplayName() { return Component.translatable("block.create_jp_signal.kilometer_post"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new KilometerPostMenu(id, inventory, this);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return new net.minecraft.world.phys.AABB(worldPosition).inflate(0.375);
    }
}
