package com.skybird.create_jp_signal.create.train.track;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.skybird.create_jp_signal.JpSignals;

public class AllEdgePointTypes {
    public static final EdgePointType<SpeedLimitBoundary> SPEED_LIMIT = 
        EdgePointType.register(JpSignals.asResource("speed_limit"), SpeedLimitBoundary::new);
}
