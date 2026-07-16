package com.skybird.create_jp_signal.block.signal.signal_mast;

import java.util.UUID;

import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.RepeaterForm;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance.SignalSize;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RepeaterSingleTunnelSignalMastBlockEntity extends BaseSignalMastBlockEntity {

    public RepeaterSingleTunnelSignalMastBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.REPEATER_SINGLE_TUNNEL_SIGNAL_MAST_ENTITY.get(), pPos, pState, AllSignalTypes.POSITION_LIGHT_REPEATER_SIGNAL);

        ISignalAppearance appearance = new PositionLightRepeaterSignalAppearance(RepeaterForm.SINGLE_DISC, SignalSize.TUNNEL);
        
        this.signalHeads.put(AttachmentSlot.PRIMARY, new SignalHead(UUID.randomUUID(), appearance.copy(), null));
    }
    
    @Override
    public void cycleLayout() {
        int hStep = this.layout.globalHorizontalStep; // -2 to 2
        int vStep = this.layout.verticalSteps.get(AttachmentSlot.PRIMARY); // 0 to 1

        if (hStep < 2) {
            this.layout.globalHorizontalStep++;
        } else if (vStep == 0) {
            this.layout.verticalSteps.put(AttachmentSlot.PRIMARY, vStep + 1);
            this.layout.globalHorizontalStep = -2;
        } else {
            this.layout.verticalSteps.put(AttachmentSlot.PRIMARY, 0);
            this.layout.globalHorizontalStep = -2;
        }
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public Vec3 getHeadOffset(AttachmentSlot slot) {
        int hStep = this.layout.globalHorizontalStep;
        int vStep = this.layout.verticalSteps.get(slot);
        double x = switch (hStep) {
            case -2 ->  -3.0 / 16.0;
            case -1 ->  -1.0 / 16.0;
            case 0  ->   0.0 / 16.0;
            case 1  ->   1.0 / 16.0;
            case 2  ->   2.0 / 16.0;
            default ->   0.0;
        };
        double y = switch (vStep) {
            case 0  -> 0.0;
            case 1  -> 0.5;
            default -> 0.0;
        };
        return new Vec3(x, y, 3.0/16);
    }

    @Override
    public Pair<Double, Double> getHeadRotation(AttachmentSlot slot) {
        return Pair.of(0.0, 0.0);
    }

    @Override
    public boolean hasMastCoupler() { return false; }
}
