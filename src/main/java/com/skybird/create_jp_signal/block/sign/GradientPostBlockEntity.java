package com.skybird.create_jp_signal.block.sign;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.menu.GradientPostMenu;
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

public class GradientPostBlockEntity extends BlockEntity implements MenuProvider {
    private int rotation;
    private int xPos = 8;
    private int zPos = 8;
    // Fixed-point tenths avoid rounding drift when scrolling and saving.
    private int frontGradient;
    private int backGradient;
    private boolean showDecimals = true;
    private boolean useLetterO = true;
    private boolean tunnelMode;
    public boolean clientVisualChanged = true;

    public GradientPostBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntities.GRADIENT_POST_ENTITY.get(), pos, state);
    }

    public boolean useLetterO() { return useLetterO; }

    public int getRotation() { return rotation; }
    public int getXPos() { return xPos; }
    public int getZPos() { return zPos; }
    public int getFrontGradient() { return frontGradient; }
    public int getBackGradient() { return backGradient; }
    public int getGradient(int side) { return side == 0 ? frontGradient : backGradient; }
    public boolean tunnelMode() { return tunnelMode; }
    public boolean showDecimals() { return showDecimals; }

    public void setPlacementData(int rotation, int x, int z) {
        this.rotation = rotation & 15;
        xPos = Mth.clamp(x, 0, 16);
        zPos = Mth.clamp(z, 0, 16);
        notifyUpdate();
    }

    public void setGradients(int front, int back, boolean decimals, boolean useLetterO, boolean tunnelMode) {
        int newFront = Mth.clamp(front, -9999, 9999);
        int newBack = Mth.clamp(back, -9999, 9999);
        if (frontGradient == newFront && backGradient == newBack && showDecimals == decimals && this.useLetterO == useLetterO && this.tunnelMode == tunnelMode) return;
        frontGradient = newFront;
        backGradient = newBack;
        showDecimals = decimals;
        this.useLetterO = useLetterO;
        this.tunnelMode = tunnelMode;
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
        tag.putBoolean("TunnelMode", tunnelMode);
        tag.putInt("Rotation", rotation);
        tag.putInt("XPos", xPos);
        tag.putInt("ZPos", zPos);
        tag.putInt("FrontGradient", frontGradient);
        tag.putInt("BackGradient", backGradient);
        tag.putBoolean("ShowDecimals", showDecimals);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        useLetterO = !tag.contains("UseLetterO") || tag.getBoolean("UseLetterO");
        // Missing in older worlds: false keeps their original, normal configuration.
        tunnelMode = tag.getBoolean("TunnelMode");
        rotation = tag.getInt("Rotation") & 15;
        xPos = tag.contains("XPos") ? Mth.clamp(tag.getInt("XPos"), 0, 16) : 8;
        zPos = tag.contains("ZPos") ? Mth.clamp(tag.getInt("ZPos"), 0, 16) : 8;
        frontGradient = Mth.clamp(tag.getInt("FrontGradient"), -9999, 9999);
        backGradient = Mth.clamp(tag.getInt("BackGradient"), -9999, 9999);
        showDecimals = !tag.contains("ShowDecimals") || tag.getBoolean("ShowDecimals");
        clientVisualChanged = true;
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public Component getDisplayName() { return Component.translatable("block.create_jp_signal.gradient_post"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GradientPostMenu(id, inventory, this);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return new net.minecraft.world.phys.AABB(worldPosition).inflate(0.875);
    }
}
