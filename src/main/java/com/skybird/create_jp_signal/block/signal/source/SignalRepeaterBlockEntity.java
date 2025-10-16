package com.skybird.create_jp_signal.block.signal.source;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.ISignalIndexSource;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class SignalRepeaterBlockEntity extends BlockEntity implements ISignalIndexSource {

    public SignalRepeaterBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_REPEATER_ENTITY.get(), pPos, pState);
    }

    public int getRedSignalIndex(int max) {
        Level level = this.getLevel();
        Set<BlockPos> visited = new HashSet<>();
        if (level == null || level.isClientSide() || this.isRemoved()) {
            return 0;
        }
    
        BlockPos previousPos = this.getBlockPos();
        Direction facing = this.getBlockState().getValue(SignalRepeaterBlock.FACING);
        BlockPos currentPos = previousPos.relative(facing);
        visited.add(previousPos);
        int maxCount = Math.min(16, max);
    
        for (int i = 0; i < maxCount; i++) {

            if (!level.isLoaded(currentPos)) {
                return i;
            }
            BlockEntity currentEntity = level.getBlockEntity(currentPos);
    
            if (currentEntity instanceof SignalRepeaterBlockEntity repeater) {
                if (repeater.isAnyRed(visited)) {
                    return i;
                }
                if (currentEntity.getBlockState().getValue(SignalRepeaterBlock.FACING) == facing) {
                    return i + 1;
                }
            } else if (currentEntity instanceof ISignalIndexSource source) {
                if (source.isRed()) {
                    return i;
                }
                
            } else {
                // sourceではないblock
                if (i == 0) {
                    return 0;
                } else {
                    BlockEntity previousEntity = level.getBlockEntity(previousPos);
                    if (previousEntity instanceof SignalRepeaterBlockEntity) {
                        return i;
                    } else if (previousEntity instanceof ISignalIndexSource source) {
                        return i - 1 + source.getRedSignalIndex(max - i + 1);
                    }
                }
            }
            previousPos = currentPos;
            currentPos = currentPos.relative(facing);
        }
        return maxCount;
    }



    public boolean isAnyRed(Set<BlockPos> visited) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide() || this.isRemoved()) {
            return true;
        }
        
    
        BlockPos startPos = this.getBlockPos();
        if (!visited.add(startPos)){
            return true;
        }
        Direction facing = this.getBlockState().getValue(SignalRepeaterBlock.FACING);
        BlockPos currentPos = startPos.relative(facing);
    
        for (int i = 0; i < 16; i++) {

            if (!level.isLoaded(currentPos)) {
                return true;
            }
            BlockEntity currentEntity = level.getBlockEntity(currentPos);
    
            if (currentEntity instanceof SignalRepeaterBlockEntity repeater) {
                if (repeater.isAnyRed(visited)) {
                    return true;
                }
                if (currentEntity.getBlockState().getValue(SignalRepeaterBlock.FACING) == facing) {
                    return false;
                }
            } else if (currentEntity instanceof ISignalIndexSource source) {
                if (source.isRed()) {
                    return true;
                }
                
            } else {
                // sourceではないblock
                break;
            }
            currentPos = currentPos.relative(facing);
        }
        return false;
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
