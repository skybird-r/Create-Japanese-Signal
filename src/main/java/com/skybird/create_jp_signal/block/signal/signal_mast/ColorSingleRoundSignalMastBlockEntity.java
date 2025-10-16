package com.skybird.create_jp_signal.block.signal.signal_mast;

import java.util.UUID;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ColorSingleRoundSignalMastBlockEntity extends BaseSignalMastBlockEntity {

    public ColorSingleRoundSignalMastBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.COLOR_SINGLE_ROUND_SIGNAL_MAST_ENTITY.get(), pPos, pState, AllSignalTypes.COLOR_LIGHT_SIGNAL);

        ISignalAppearance appearance = new ColorLightSignalAppearance(ColorLightSignalAppearance.HeadType.THREE_LAMP, ColorLightSignalAppearance.BackplateType.ROUND, false);
        
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
        double x = 0, y = 0;
        switch (hStep) {
            case -2:
                x = -10.0 / 16.0;
                break;
            case -1:
                x = -6.0 / 16.0;
                break;
            case 0:
                x = 0.0 / 16.0;
                break;
            case 1:
                x = 6.0 / 16.0;
                break;
            case 2:
                x = 10.0 / 16.0;
                break;
            default:
                x = 0.0;
                break;
        }
        if (vStep == 1) {
            y = 0.5;
        }
        return new Vec3(x, y, 6.0/16);
    }

    @Override
    public Pair<Double, Double> getHeadRotation(AttachmentSlot slot) {
        return Pair.of(0.0, 0.0);
    }
}
