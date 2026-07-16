package com.skybird.create_jp_signal.create.mixin_interface;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import com.simibubi.create.foundation.utility.Pair;

public interface INavigation {

    public List<Pair<Double, MutableDouble>> getActiveSpeedLimits();
    
}
