package com.skybird.create_jp_signal.create.mixin_interface;

import java.util.Map;
import java.util.UUID;

import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.createmod.catnip.data.Pair;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

public interface ITrain {
    public Map<UUID, Pair<SignalBoundary, Boolean>> getActiveReservations();
    public OperationType getOperationType();
    public void setOperationType(OperationType type);
    public double getMinimumReservationDistance();
    public void setMinimumReservationDistance(double minimumReservationDistance);
    public double getSignalStoppingDistance();
    public void setSignalStoppingDistance(double signalStoppingDistance);
    public int getTickWaitBeforeDeparture();
    public void setTickWaitBeforeDeparture(int tickWaitBeforeDeparture);
    public long getSignalDepartureDelayEndTick();
    public void setSignalDepartureDelayEndTick(long signalDepartureDelayEndTick);
}
