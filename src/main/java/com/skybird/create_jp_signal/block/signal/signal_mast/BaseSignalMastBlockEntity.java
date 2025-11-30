package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSignalMastBlockEntity extends BaseSignalBlockEntity {

    protected int rotation = 0;
    protected int xPos = 8;
    protected int zPos = 8;

    public BaseSignalMastBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ISignalType signal) {
        super(type, pos, state, signal);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        // BlockState blockState = this.getBlockState();
        // int rotation = blockState.getValue(BaseSignalMastBlock.ROTATION);
        // int xPos = blockState.getValue(BaseSignalMastBlock.X_POS);
        // int zPos = blockState.getValue(BaseSignalMastBlock.Z_POS);
        // pTag.putInt("Rotation", rotation);
        // pTag.putInt("XPos", xPos);
        // pTag.putInt("ZPos", zPos);
        pTag.putInt("Rotation", this.rotation);
        pTag.putInt("XPos", this.xPos);
        pTag.putInt("ZPos", this.zPos);
        super.saveAdditional(pTag);
    }

    // 3. NBTからデータを読み込む処理を追加
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.rotation = pTag.getInt("Rotation");
        this.xPos = pTag.getInt("XPos");
        this.zPos = pTag.getInt("ZPos");
    }

    // 4. Blockクラスからデータを書き込むためのセッターを追加
    public void setPlacementData(int rotation, int xPos, int zPos) {
        this.rotation = rotation;
        this.xPos = xPos;
        this.zPos = zPos;
        setChanged(); // 変更をワールドに通知
    }
    
    // 5. 当たり判定やレンダリングで使うためのゲッターを追加
    public int getXPos() { return xPos; }
    public int getZPos() { return zPos; }
    public int getRotation() { return rotation; }
    
    public boolean hasMastCoupler() { return true; }
}