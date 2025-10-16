package com.skybird.create_jp_signal.create.mixin_interface;

import java.util.Map;
import java.util.UUID;

import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

public interface ITrain {
    public Map<UUID, Pair<SignalBoundary, Boolean>> getActiveReservations();
    public OperationType getOperationType();
    public void setOperationType(OperationType type);
}
