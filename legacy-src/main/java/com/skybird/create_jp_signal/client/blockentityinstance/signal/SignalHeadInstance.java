package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.BasicData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleTunnelSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleTunnelSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.RepeaterSingleTunnelSignalMastBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class SignalHeadInstance {

    protected final MaterialManager materialManager;
    protected SignalHead signalHead;
    protected final BlockEntity blockEntity;

    private final List<SignalMastCouplerInstance> mastCouplerInstances = new ArrayList<>();

    protected SignalAccessoryInstance accessory;

    protected final List<Vec3> mastCouplerPositions = new ArrayList<>();
    
    // ★ここに追加：自分の座標を覚えておく
    protected BlockPos pos; 

    protected final List<BasicData> allModels = new ArrayList<>();

    private Vec3 currentOffset = Vec3.ZERO;
    private Pair<Double, Double> currentRotation = Pair.of(0.0, 0.0);

    public SignalHeadInstance(MaterialManager materialManager, SignalHead signalHead, BlockEntity be) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
        this.blockEntity = be;
        accessory = new SignalAccessoryInstance(materialManager, signalHead);
    }

    // 初期化
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        this.pos = pos;
        if (this.signalHead.getAppearance().hasSameStaticParts(signalHead.getAppearance()) 
                && this.currentOffset.equals(offset) 
                && this.currentRotation.equals(rotation) 
                && !allModels.isEmpty()) {
            this.signalHead = signalHead;
            return;
        }
        this.currentOffset = offset;
        this.currentRotation = rotation;
        this.signalHead = signalHead;
        initInternal(signalHead, ms, pos, offset, rotation);
        initAccessory(signalHead, ms, pos, offset, rotation);
        initMastCoupler(ms, offset, rotation);
    }

    public abstract void initInternal(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation);

    public void initAccessory(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        accessory.init(signalHead, ms, pos, offset, rotation);
        
    }

    public void initMastCoupler(PoseStack ms, Vec3 offset, Pair<Double, Double> rotation) {
        if (blockEntity instanceof ColorSingleTunnelSignalMastBlockEntity || blockEntity instanceof RepeaterSingleTunnelSignalMastBlockEntity)
            return;
        for (Vec3 position : mastCouplerPositions) {
            SignalMastCouplerInstance instance = new SignalMastCouplerInstance(materialManager);
            instance.init(position, ms, offset, rotation);
            mastCouplerInstances.add(instance);
        }
        for (Vec3 position : accessory.getMastCouplerPositions()) {
            SignalMastCouplerInstance instance = new SignalMastCouplerInstance(materialManager);
            instance.init(position, ms, offset, rotation);
            mastCouplerInstances.add(instance);
        }
    }

    public void beginFrame(BlockPos instancePos) {

    }

    public void updateLight(Level level, BlockPos pos) {
        accessory.updateLight(level, pos);
        mastCouplerInstances.forEach(coupler -> coupler.updateLight(level, pos));
    }

    public void remove() {
        allModels.forEach(BasicData::delete);
        allModels.clear();
        accessory.delete();
        mastCouplerPositions.clear();
        mastCouplerInstances.forEach(SignalMastCouplerInstance::delete);
        mastCouplerInstances.clear();
    }

    public List<Vec3> getMastCouplerPositions() {
        return mastCouplerPositions;
    }
}