package com.skybird.create_jp_signal.menu;

import com.skybird.create_jp_signal.AllMenuTypes;
import com.skybird.create_jp_signal.block.sign.GradientPostBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class GradientPostMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    public final boolean useLetterO;
    public final boolean tunnelMode;
    public final int frontGradient;
    public final int backGradient;
    public final boolean showDecimals;
    private final GradientPostBlockEntity post;

    public GradientPostMenu(int id, Inventory inventory, GradientPostBlockEntity post) {
        super(AllMenuTypes.GRADIENT_POST_MENU.get(), id);
        this.post = post;
        pos = post.getBlockPos();
        frontGradient = post.getFrontGradient();
        backGradient = post.getBackGradient();
        showDecimals = post.showDecimals();
        useLetterO = post.useLetterO();
        tunnelMode = post.tunnelMode();
    }

    public GradientPostMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(AllMenuTypes.GRADIENT_POST_MENU.get(), id);
        post = null;
        pos = buffer.readBlockPos();
        frontGradient = buffer.readVarInt();
        backGradient = buffer.readVarInt();
        showDecimals = buffer.readBoolean();
        useLetterO = buffer.readBoolean();
        tunnelMode = buffer.readBoolean();
    }

    @Override public boolean stillValid(Player player) {
        return post != null && !post.isRemoved() && player.level() == post.getLevel()
            && player.level().getBlockEntity(pos) == post
            && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
}
