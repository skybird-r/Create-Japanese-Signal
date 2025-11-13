package com.skybird.create_jp_signal.create.train.track;

import javax.annotation.Nullable;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpeedLimitBoundary extends SingleBlockEntityEdgePoint {

    private double speedLimit = 100.0; // km/h
    private double limitDistance = 0.0; // blocks


    public SpeedLimitBoundary() {}

    public double getSpeedLimit() { return speedLimit; }
    public double getLimitDistance() { return limitDistance; }
    public void setSpeedLimit(double speedLimit) { this.speedLimit = speedLimit; }
    public void setLimitDistance(double limitDistance) { this.limitDistance = limitDistance; }

    public boolean isBoundTo(BlockPos pos) {
        if (pos == null)
            return false;
        return pos.equals(this.blockEntityPos);
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions); 
        
        nbt.putDouble("SpeedLimit", this.speedLimit);
        nbt.putDouble("LimitDistance", this.limitDistance);

        // JpSignals.LOGGER.info("Boundary write: " + Double.toString(this.speedLimit) + " " + Double.toString(this.limitDistance));
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions); 
        
        this.speedLimit = nbt.getDouble("SpeedLimit");
        this.limitDistance = nbt.getDouble("LimitDistance");
    }
}
