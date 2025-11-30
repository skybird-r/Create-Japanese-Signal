package com.skybird.create_jp_signal.block.signal;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

public interface ISignalAppearance {
    void writeNbt(CompoundTag tag);
    
    String getTypeId(); 
    ISignalAppearance copy();
    SignalAccessory getAccessory();
    boolean hasSameStaticParts(ISignalAppearance appearance);
    
}
