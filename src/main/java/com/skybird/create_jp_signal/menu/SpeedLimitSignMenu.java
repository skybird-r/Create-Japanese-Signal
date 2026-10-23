package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockEntity;
import com.skybird.create_jp_signal.block.sign.RailwaySignSettings.Variant;
import com.skybird.create_jp_signal.block.sign.RailwaySignType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SpeedLimitSignMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final int speed;
    public final Variant variant;
    public final boolean useLetterO;
    private final RailwaySignBlockEntity sign;
    public SpeedLimitSignMenu(int id, Inventory inventory, RailwaySignBlockEntity sign) {
        super(AllMenuTypes.SPEED_LIMIT_SIGN_MENU.get(), id);
        this.sign = sign; pos = sign.getBlockPos();
        speed = sign.settings().speed(); variant = sign.settings().variant(); useLetterO = sign.settings().useLetterO();
    }
    public SpeedLimitSignMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(AllMenuTypes.SPEED_LIMIT_SIGN_MENU.get(), id);
        sign = null; pos = buffer.readBlockPos(); speed = buffer.readVarInt();
        variant = buffer.readEnum(Variant.class); useLetterO = buffer.readBoolean();
    }
    @Override public boolean stillValid(Player player) {
        return sign != null && !sign.isRemoved() && sign.signType() == RailwaySignType.SPEED_LIMIT
            && player.level() == sign.getLevel() && player.level().getBlockEntity(pos) == sign
            && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
