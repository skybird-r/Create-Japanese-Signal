package com.skybird.create_jp_signal.block.signal;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.skybird.create_jp_signal.block.signal.signal_mast.BaseSignalMastBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSignalBlock extends BaseEntityBlock implements IWrenchable {

    public BaseSignalBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof BaseSignalBlockEntity be) {
            be.cycleLayout();
        }
        return InteractionResult.SUCCESS;
    }

    private static final net.minecraft.tags.TagKey<net.minecraft.world.item.Item> NAME_EDIT_BYPASS =
        net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
            new net.minecraft.resources.ResourceLocation("create_jp_signal", "signal_name_edit_bypass"));

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        var held = player.getItemInHand(hand);
        if (held.is(NAME_EDIT_BYPASS) || com.simibubi.create.AllTags.AllItemTags.WRENCH.matches(held)
                || player.isShiftKeyDown() || !player.mayBuild() || player.isSpectator()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof BaseSignalBlockEntity signal) || signal.getSignalHeads().isEmpty())
            return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            var provider = new net.minecraft.world.SimpleMenuProvider(
                (id, inventory, unused) -> new com.skybird.create_jp_signal.menu.SignalNameMenu(id, inventory, signal),
                net.minecraft.network.chat.Component.translatable("create_jp_signal.gui.signal_name.title"));
            net.minecraftforge.network.NetworkHooks.openScreen(serverPlayer, provider,
                buffer -> com.skybird.create_jp_signal.menu.SignalNameMenu.writeOpeningData(buffer, signal));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // getTickerは個別で実装
}
