package com.skybird.create_jp_signal.block;

import com.skybird.create_jp_signal.AllBlockEntities;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Signal3LBlockEntity extends BlockEntity { //old

    // 信号の状態を定義するEnum
    public enum SignalAspect {
        OFF,
        RED,
        YELLOW,
        GREEN,
    }

    // booleanの代わりにEnumで状態を保持
    public SignalAspect aspect = SignalAspect.OFF;

    public Signal3LBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_3L_ENTITY.get(), pPos, pState);
    }

    // レッドストーン強度に応じて状態を更新するメソッド
    public void updateSignalState(int power) {
        SignalAspect newAspect;
        if (power == 0) {
            newAspect = SignalAspect.OFF;
        } else if (power <= 5) {
            newAspect = SignalAspect.RED;
        } else if (power <= 10) {
            newAspect = SignalAspect.YELLOW;
        } else {
            newAspect = SignalAspect.GREEN;
        }

        // 状態が変化した場合のみ更新と同期を行う
        if (this.aspect != newAspect) {
            this.aspect = newAspect;
            this.setChanged(); // 変更があったことをマーク
            // クライアントに更新を通知
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ---- ここから下は、サーバー・クライアント間の同期に必要な定型コード ----

    // 状態が変更されたときにクライアントに送るパケットを作成
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
        tag.putInt("Aspect", this.aspect.ordinal());
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        // 数値からEnumの状態を復元
        int aspectOrdinal = tag.getInt("Aspect");
        this.aspect = SignalAspect.values()[aspectOrdinal];
    }
}