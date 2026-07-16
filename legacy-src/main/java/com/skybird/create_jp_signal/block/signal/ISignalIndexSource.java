package com.skybird.create_jp_signal.block.signal;

import com.skybird.create_jp_signal.create.train.schedule.OperationType;

public interface ISignalIndexSource {

    public boolean isRed();
    public double getReserverMaxSpeed();
    public int getRedSignalIndex(int max);
    public OperationType getReserverOperationType();

}