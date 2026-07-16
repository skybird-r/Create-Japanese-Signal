package com.skybird.create_jp_signal.block.signal.signal_mast;

import com.skybird.create_jp_signal.AllBlockEntities;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;

import io.netty.util.Signal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignalMastBlockEntity extends BlockEntity { 


    protected int rotation = 0;
    protected int xPos = 8;
    protected int zPos = 8;

    public SignalMastBlockEntity(BlockPos pPos, BlockState pState) {
        super(AllBlockEntities.SIGNAL_MAST_ENTITY.get(), pPos, pState);
    }


    // --- NBT & 同期 ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        // BlockState blockState = this.getBlockState();
        // int rotation = blockState.getValue(SignalMastBlock.ROTATION);
        // int xPos = blockState.getValue(SignalMastBlock.X_POS);
        // int zPos = blockState.getValue(SignalMastBlock.Z_POS);
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

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag() { return this.saveWithoutMetadata(); }
}