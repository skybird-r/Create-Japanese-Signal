package com.skybird.create_jp_signal.create.mixin_interface;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;

import net.createmod.catnip.data.Pair;

public interface INavigation {

    public List<Pair<Double, MutableDouble>> getActiveSpeedLimits();
    
}
