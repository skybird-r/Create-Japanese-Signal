package com.skybird.create_jp_signal.create.train.track;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class ReservationLimitBoundary extends SingleBlockEntityEdgePoint {

    public static final int DEFAULT_LIMIT = 1000;
    public static final int MAX_LIMIT = 2000;

    private int reservationLimit = DEFAULT_LIMIT;

    public int getReservationLimit() {
        return reservationLimit;
    }

    public void setReservationLimit(int reservationLimit) {
        this.reservationLimit = Mth.clamp(reservationLimit, 0, MAX_LIMIT);
    }

    public boolean isBoundTo(BlockPos pos) {
        return pos != null && pos.equals(blockEntityPos);
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);
        nbt.putInt("ReservationLimit", reservationLimit);
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        setReservationLimit(nbt.contains("ReservationLimit") ? nbt.getInt("ReservationLimit") : DEFAULT_LIMIT);
    }
}
