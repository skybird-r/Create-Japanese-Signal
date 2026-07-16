package com.skybird.create_jp_signal.create.mixin_interface;

import com.simibubi.create.content.trains.signal.SignalBoundary;

public interface ISignalEdgeGroup {

    public boolean isThisOccupiedUnless2(SignalBoundary boundary);
    public boolean isOccupiedUnless2(SignalBoundary boundary);

}
