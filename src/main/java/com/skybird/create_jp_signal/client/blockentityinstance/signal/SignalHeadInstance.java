package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.BasicData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.block.signal.SignalHead;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SignalHeadInstance {

    protected final MaterialManager materialManager;
    protected SignalHead signalHead;

    protected SignalAccessoryInstance accessory;
    
    // ★ここに追加：自分の座標を覚えておく
    protected BlockPos pos; 

    protected final List<BasicData> allModels = new ArrayList<>();

    public SignalHeadInstance(MaterialManager materialManager, SignalHead signalHead) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
    }

    // 初期化
    public void init(SignalHead signalHead, PoseStack ms, BlockPos pos, Vec3 offset, Pair<Double, Double> rotation) {
        this.pos = pos;
    }

    public abstract void updateTransform(BlockPos pos, Vec3 offset, Pair<Double, Double> rotation);

    public abstract void beginFrame(BlockPos instancePos);

    public abstract void updateLight(Level level, BlockPos pos);

    public void remove() {
        allModels.forEach(BasicData::delete);
        allModels.clear();
    }
    
    // public boolean shouldShow(SignalHead newSignalHead) {
    //     return this.signalHead.getAppearance().equals(newSignalHead.getAppearance());
    // }

    // public void setSignalHead(SignalHead signalHead) {
    //     this.signalHead = signalHead;
    // }

    public List<Vec3> getMastCouplerPosition() {
        return List.of();
    }
}