package com.skybird.create_jp_signal.block.signal;
import net.minecraft.nbt.CompoundTag;

public interface ISignalAppearance {
    void writeNbt(CompoundTag tag);
    
    String getTypeId(); 
    ISignalAppearance copy();
}
