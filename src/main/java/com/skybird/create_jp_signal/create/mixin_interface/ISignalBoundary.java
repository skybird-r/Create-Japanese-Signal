package com.skybird.create_jp_signal.create.mixin_interface;

import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.core.BlockPos;

public interface ISignalBoundary {
    int getNextRedIndex(TrackGraph graph, BlockPos blockEntity, int max);
    double getReserverMaxSpeed(BlockPos blockEntity);
    Couple<Pair<SignalBoundary, Boolean>> getNextEntrySignals();
    int getNextRedIndex(TrackGraph graph, int currentIndex, int max, boolean first);
    boolean isRed(BlockPos blockEntity);
    Couple<Double> getReserverMaxSpeeds();
    OperationType getReserverOperationType(BlockPos blockEntity);
    Couple<OperationType> getReserverOperationTypes();
}
