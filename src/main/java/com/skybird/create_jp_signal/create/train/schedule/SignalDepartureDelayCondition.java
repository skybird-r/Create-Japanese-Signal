package com.skybird.create_jp_signal.create.train.schedule;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class SignalDepartureDelayCondition extends ScheduledDelay {

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        ITrain trainExtension = (ITrain) train;
        long delayEndTick = level.getGameTime() + totalWaitTicks();
        trainExtension.setSignalDepartureDelayEndTick(Math.max(
            trainExtension.getSignalDepartureDelayEndTick(),
            delayEndTick
        ));
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("signal_departure_delay");
    }
}
