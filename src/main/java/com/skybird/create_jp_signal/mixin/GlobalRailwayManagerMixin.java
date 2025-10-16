package com.skybird.create_jp_signal.mixin;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public abstract class GlobalRailwayManagerMixin {

    @Shadow public Map<UUID, SignalEdgeGroup> signalEdgeGroups;
    
    // @Inject(
    //     method = "lambda$tick$5",
    //     at = @At(
    //         value = "FIELD",
    //         target = "Lcom/simibubi/create/content/trains/signal/SignalEdgeGroup;reserved:Lcom/simibubi/create/content/trains/signal/SignalBoundary;",
    //         shift = At.Shift.AFTER
    //     ),
    //     locals = LocalCapture.CAPTURE_FAILHARD
    // )
    // private static void create_jp_signal_onGroupReset(UUID id, SignalEdgeGroup group, CallbackInfo ci) {
    //     ((ISignalEdgeGroup)group).setReserverMaxSpeed(0.0);
    // }
   
}
