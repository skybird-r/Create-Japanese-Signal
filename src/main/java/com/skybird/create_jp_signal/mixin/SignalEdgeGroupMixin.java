package com.skybird.create_jp_signal.mixin;

import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.skybird.create_jp_signal.create.mixin_interface.ISignalEdgeGroup;

@Mixin(value = SignalEdgeGroup.class, remap = false)
public abstract class SignalEdgeGroupMixin implements ISignalEdgeGroup {

    @Shadow public Set<Train> trains;
    @Shadow public SignalBoundary reserved;
    @Shadow public Set<SignalEdgeGroup> intersectingResolved;

    @Shadow protected abstract void walkIntersecting(Consumer<SignalEdgeGroup> callback);

    /*
    @Inject(
        method = "isThisOccupiedUnless",
        at = @At("HEAD"),
        cancellable = true
    )
    private void create_jp_signal_onIsThisOccupiedUnless(SignalBoundary boundary, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(!trains.isEmpty() && reserved != boundary || reserved != null && reserved != boundary);
	}
    */

  public boolean isThisOccupiedUnless2(SignalBoundary boundary) {
		return !trains.isEmpty() && reserved != boundary || reserved != null && reserved != boundary;
	}

	public boolean isOccupiedUnless2(SignalBoundary boundary) {
		if (intersectingResolved.isEmpty())
			walkIntersecting(intersectingResolved::add);
		for (SignalEdgeGroup group : intersectingResolved)
			if (((ISignalEdgeGroup)group).isThisOccupiedUnless2(boundary))
				return true;
		return false;
	}
}
