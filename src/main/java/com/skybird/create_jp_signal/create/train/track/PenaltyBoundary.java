package com.skybird.create_jp_signal.create.train.track;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class PenaltyBoundary extends SingleBlockEntityEdgePoint {

    public static final int MAX_PENALTY = 2000;

    private int penalty = 100;

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = Mth.clamp(penalty, 0, MAX_PENALTY);
    }

    public boolean isBoundTo(BlockPos pos) {
        return pos != null && pos.equals(this.blockEntityPos);
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);
        nbt.putInt("Penalty", penalty);
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        setPenalty(nbt.getInt("Penalty"));
    }
}
