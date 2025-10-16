package com.skybird.create_jp_signal.block.signal.debug;

import java.lang.management.OperatingSystemMXBean;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.ISignalIndexSource;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

public class DebugInputBlockEntity extends BlockEntity implements ISignalIndexSource {

    private int index = 0;
    private boolean shunt = false;

    public DebugInputBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.DEBUG_INPUT_ENTITY.get(), pPos, pState);
    }

    public int getRedSignalIndex(int max) {
        return Math.min(this.index, max);
    }

    public boolean isRed() {
        return this.index == 0;
    }

    public double getReserverMaxSpeed() {
        return 0.0;
    }

    public OperationType getReserverOperationType() {
        return shunt ? OperationType.SHUNT : OperationType.TRAIN;
    }

    public void cycleIndex() {
        this.index = (this.index + 1) % 4;
        if (this.index == 0) {
            this.shunt = !this.shunt;
        }
        this.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Index", this.index);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.index = tag.getInt("Index");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}