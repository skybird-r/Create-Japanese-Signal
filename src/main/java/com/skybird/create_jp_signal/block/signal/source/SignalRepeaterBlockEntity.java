package com.skybird.create_jp_signal.block.signal.source;

import java.util.HashSet;
import java.util.Set;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.ISignalIndexSource;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class SignalRepeaterBlockEntity extends BlockEntity implements ISignalIndexSource {

    public SignalRepeaterBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_REPEATER_ENTITY.get(), pPos, pState);
    }

    public int getRedSignalIndex(int max) {
        Level level = this.getLevel();
        if (max <= 0 || level == null || level.isClientSide() || this.isRemoved()) {
            return 0;
        }

        Direction facing = this.getBlockState().getValue(SignalRepeaterBlock.FACING);
        BlockPos currentPos = this.getBlockPos().relative(facing);
        Integer previousSourceIndex = null;

        while (true) {
            if (!level.isLoaded(currentPos)) {
                return 0;
            }

            BlockEntity currentEntity = level.getBlockEntity(currentPos);

            if (currentEntity instanceof SignalRepeaterBlockEntity repeater) {
                return 1 + repeater.getRedSignalIndex(max - 1);
            } else if (currentEntity instanceof ISignalIndexSource source) {
                int sourceIndex = source.getRedSignalIndex(max);
                if (sourceIndex <= 0) {
                    return 0;
                }
                previousSourceIndex = sourceIndex;
            } else {
                if (previousSourceIndex == null) {
                    return 0;
                }
                return Math.min(max, previousSourceIndex);
            }

            currentPos = currentPos.relative(facing);
        }
    }
    
    @Override
    public boolean isRed() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide() || this.isRemoved()) {
            return true;
        }
        if (this.getRedSignalIndex(1) > 0) {
            return false;
        }
        return true;
    }

    @Override
    public double getReserverMaxSpeed() {
        Set<BlockPos> visited = new HashSet<>();
        return getReserverMaxSpeed(visited);
    }

    private double getReserverMaxSpeed(Set<BlockPos> visited) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide() || this.isRemoved()) {
            return 0;
        }
        BlockPos currentPos = this.getBlockPos();
        if (!visited.add(currentPos)){
            return 0;
        }
        Direction facing = this.getBlockState().getValue(SignalRepeaterBlock.FACING);
    
        BlockPos nextPos = currentPos.relative(facing);

        if (!level.isLoaded(nextPos)) {
            return 0;
        }
        if (level.getBlockEntity(nextPos) instanceof SignalRepeaterBlockEntity repeater) {
            return repeater.getReserverMaxSpeed(visited);
        } else if (level.getBlockEntity(nextPos) instanceof ISignalIndexSource sis) {
            return sis.getReserverMaxSpeed();
        }
        return 0;
    }

    @Override
    public OperationType getReserverOperationType() {
        Set<BlockPos> visited = new HashSet<>();
        return getReserverOperationType(visited);
    }

    private OperationType getReserverOperationType(Set<BlockPos> visited) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide() || this.isRemoved()) {
            return OperationType.TRAIN;
        }
        BlockPos currentPos = this.getBlockPos();
        if (!visited.add(currentPos)){
            return OperationType.TRAIN;
        }
        Direction facing = this.getBlockState().getValue(SignalRepeaterBlock.FACING);
    
        BlockPos nextPos = currentPos.relative(facing);

        if (!level.isLoaded(nextPos)) {
            return OperationType.TRAIN;
        }
        if (level.getBlockEntity(nextPos) instanceof SignalRepeaterBlockEntity repeater) {
            return repeater.getReserverOperationType(visited);
        } else if (level.getBlockEntity(nextPos) instanceof ISignalIndexSource sis) {
            return sis.getReserverOperationType();
        }
        return OperationType.TRAIN;
    }
}
