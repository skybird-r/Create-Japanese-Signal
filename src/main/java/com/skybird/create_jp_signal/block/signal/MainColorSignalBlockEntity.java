package com.skybird.create_jp_signal.block.signal;

import javax.annotation.Nonnull;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.Signal3LBlockEntity.SignalAspect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/* 
public class MainColorSignalBlockEntity extends BlockEntity {

    public MainColorSignalBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_3L_ENTITY.get(), pPos, pState);
    }




    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // サーバーからのパケットを受け取ったクライアント側で、このBlockEntityのデータを更新
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    // チャンクがロード/アンロードされるときに、データを保存/読み込みするためのNBT処理
    @Override
    public void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        // Enumの状態を数値(ordinal)として保存
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        // 数値からEnumの状態を復元
    }


    
}
*/