package com.skybird.create_jp_signal.block.signal;

import java.util.UUID;
import javax.annotation.Nullable;

import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class SignalHead {

    private final UUID uniqueId;
    private ISignalAppearance appearance;
    private SignalAspect.State currentAspect;
    private SignalAccessory.Route currentRoute;
    private BlockPos controllerPos;

    // --- コンストラクタを修正 ---
    public SignalHead(UUID id, ISignalAppearance appearance, @Nullable BlockPos controllerPos) {
        this.uniqueId = id;
        this.appearance = appearance;
        this.controllerPos = controllerPos;

        // SignalTypeから適切なデフォルト現示を取得するロジックが望ましい
        this.currentAspect = SignalAspect.State.OFF_3;
        this.currentRoute = SignalAccessory.Route.NONE;
    }

    public UUID getUniqueId() { return uniqueId; }
    public ISignalAppearance getAppearance() { return appearance; }
    public SignalAspect.State getCurrentAspect() { return currentAspect; }
    public SignalAccessory.Route getCurrentRoute() { return currentRoute; }
    public void setCurrentAspect(SignalAspect.State newAspect) { this.currentAspect = newAspect; }
    public void setCurrentRoute(SignalAccessory.Route currentRoute) { this.currentRoute = currentRoute; }

    public void setAppearance(ISignalAppearance appearance) {
        this.appearance = appearance;
    }
    
    @Nullable
    public BlockPos getControllerPos() { return controllerPos; }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public void writeNbt(CompoundTag tag) {
        tag.putUUID("Id", this.uniqueId);

        if (this.appearance != null) {
            tag.putString("AppearanceType", this.appearance.getTypeId());
            CompoundTag appearanceTag = new CompoundTag();
            this.appearance.writeNbt(appearanceTag);
            tag.put("AppearanceData", appearanceTag);
        }

        if (this.controllerPos != null) {
            tag.put("ControllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }

        tag.putString("Aspect", this.currentAspect.name());
        tag.putString("Route", this.currentRoute.name());
    }

    public static SignalHead fromNbt(CompoundTag tag) {
        UUID id = tag.getUUID("Id");
        
        String typeId = tag.getString("AppearanceType");
        ISignalAppearance appearance = AllSignalTypes.createAppearanceFromId(typeId, tag.getCompound("AppearanceData"));
        
        BlockPos controllerPos = null;
        if (tag.contains("ControllerPos")) {
            controllerPos = NbtUtils.readBlockPos(tag.getCompound("ControllerPos"));
        }

        SignalHead newHead = new SignalHead(id, appearance, controllerPos);
        
        try {
            SignalAspect.State aspect = SignalAspect.State.valueOf(tag.getString("Aspect"));
            newHead.setCurrentAspect(aspect);
        } catch (IllegalArgumentException e) { /* 不正なデータはデフォルト値のまま */ }

        try {
            SignalAccessory.Route route = SignalAccessory.Route.valueOf(tag.getString("Route"));
            newHead.setCurrentRoute(route);
        } catch (IllegalArgumentException e) { /* 不正なデータはデフォルト値のまま */ }
        
        return newHead;
    }
}