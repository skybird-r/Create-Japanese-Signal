package com.skybird.create_jp_signal.item;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.skybird.create_jp_signal.item.measurement.TrackDistanceSearch;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrackDistanceMeterItem extends Item {
    private static final String START = "DistanceMeterStart";

    public TrackDistanceMeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (!context.getLevel().isClientSide)
            select(context.getLevel(), player, context.getItemInHand(), context.getClickedPos(), null);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);
        if (!level.isClientSide) reset(stack, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void select(Level level, Player player, ItemStack stack, BlockPos pos,
                              @Nullable BezierTrackPointLocation curve) {
        if (player.isShiftKeyDown()) {
            reset(stack, player);
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        player.getCooldowns().addCooldown(stack.getItem(), 5);

        CompoundTag start = getStart(stack);
        TrackGraphLocation origin = null;
        if (start != null) {
            if (!start.getString("Dimension").equals(level.dimension().location().toString())) {
                message(player, "dimension");
                return;
            }
            BlockPos startPos = NbtUtils.readBlockPos(start.getCompound("Pos"));
            // Load only the anchor's local chunk for validation, never the route's chunks.
            // A remote/unloaded anchor must not be mistaken for a destroyed track.
            level.getChunk(startPos);
            if (!isAnchorPresent(level, startPos, start)) {
                resetData(stack);
                message(player, "lost");
                return;
            }
            origin = resolve(level, startPos, start);
            if (origin == null) {
                message(player, "unavailable");
                return;
            }
        }

        if (!(level.getBlockState(pos).getBlock() instanceof ITrackBlock track)) {
            message(player, "no_track");
            return;
        }
        Vec3 axis = track.getNearestTrackAxis(level, pos, level.getBlockState(pos), player.getLookAngle()).getFirst();
        CompoundTag selection = new CompoundTag();
        selection.putString("Dimension", level.dimension().location().toString());
        selection.put("Pos", NbtUtils.writeBlockPos(pos));
        selection.put("Axis", VecHelper.writeNBT(axis));
        if (curve != null) {
            selection.put("CurveTarget", NbtUtils.writeBlockPos(curve.curveTarget()));
            selection.putInt("Segment", curve.segment());
        }
        TrackGraphLocation destination = resolve(level, pos, selection);
        if (destination == null) {
            message(player, "unavailable");
            return;
        }
        if (start == null) {
            Vec3 marker = markerPosition(level, pos, selection, track);
            selection.put("Marker", VecHelper.writeNBT(marker));
            stack.getOrCreateTag().put(START, selection);
            message(player, "set");
            return;
        }
        TrackDistanceSearch.Result result = TrackDistanceSearch.measure(origin, destination);
        switch (result.status()) {
            case FOUND -> message(player, "distance", String.format(Locale.ROOT, "%.2f", result.distance()));
            case DISCONNECTED -> message(player, "disconnected");
            case OUT_OF_RANGE -> message(player, "out_of_range");
            case TOO_COMPLEX -> message(player, "too_complex");
        }
    }

    private static TrackGraphLocation resolve(Level level, BlockPos pos, CompoundTag selection) {
        // Deliberately bypass TrackTargetingBlockItem.withGraphLocation: its overlap
        // checks reject signals/stations, which must have no effect on measurements.
        if (selection.contains("CurveTarget"))
            return TrackGraphHelper.getBezierGraphLocationAt(level, pos, AxisDirection.NEGATIVE,
                new BezierTrackPointLocation(NbtUtils.readBlockPos(selection.getCompound("CurveTarget")),
                    selection.getInt("Segment")));
        return TrackGraphHelper.getGraphLocationAt(level, pos, AxisDirection.POSITIVE,
            VecHelper.readNBT(selection.getList("Axis", 6)));
    }

    private static boolean isAnchorPresent(Level level, BlockPos pos, CompoundTag start) {
        if (!(level.getBlockState(pos).getBlock() instanceof ITrackBlock track)) return false;
        if (!start.contains("CurveTarget")) {
            Vec3 axis = VecHelper.readNBT(start.getList("Axis", 6));
            return track.getTrackAxes(level, pos, level.getBlockState(pos)).stream()
                .anyMatch(candidate -> candidate.distanceToSqr(axis) < 1e-6
                    || candidate.add(axis).lengthSqr() < 1e-6);
        }
        if (!(level.getBlockEntity(pos) instanceof TrackBlockEntity be)) return false;
        BezierConnection connection = be.getConnections().get(NbtUtils.readBlockPos(start.getCompound("CurveTarget")));
        return connection != null && connection.isPrimary() && start.getInt("Segment") >= 0
            && start.getInt("Segment") < connection.getSegmentCount() - 1
            && curvePoint(connection, start.getInt("Segment"))
                .distanceToSqr(VecHelper.readNBT(start.getList("Marker", 6))) < .01;
    }

    private static Vec3 markerPosition(Level level, BlockPos pos, CompoundTag selection, ITrackBlock track) {
        if (selection.contains("CurveTarget") && level.getBlockEntity(pos) instanceof TrackBlockEntity be) {
            BezierConnection connection = be.getConnections().get(NbtUtils.readBlockPos(selection.getCompound("CurveTarget")));
            if (connection != null)
                return curvePoint(connection, selection.getInt("Segment"));
        }
        return Vec3.atBottomCenterOf(pos).add(0, track.getElevationAtCenter(level, pos, level.getBlockState(pos)), 0);
    }

    /** Match the midpoint of the segment pair used by Create's curve hit testing. */
    public static Vec3 curvePoint(BezierConnection connection, int segment) {
        float[] steps = connection.getStepLUT();
        double count = connection.getSegmentCount();
        double t0 = steps[segment] * segment / count;
        double t2 = steps[segment + 2] * (segment + 2) / count;
        return connection.getPosition(t0).add(connection.getPosition(t2)).scale(.5);
    }

    @Nullable
    public static CompoundTag getStart(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(START, 10)
            ? stack.getTag().getCompound(START) : null;
    }

    private static void resetData(ItemStack stack) {
        if (stack.getTag() != null) stack.getTag().remove(START);
    }

    private static void reset(ItemStack stack, Player player) {
        resetData(stack);
        message(player, "reset");
    }

    private static void message(Player player, String key, Object... args) {
        player.displayClientMessage(Component.translatable("item.create_jp_signal.track_distance_meter." + key, args), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.create_jp_signal.track_distance_meter.help").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.create_jp_signal.track_distance_meter.reset_help").withStyle(ChatFormatting.GRAY));
        CompoundTag start = getStart(stack);
        if (start != null)
            tooltip.add(Component.translatable("item.create_jp_signal.track_distance_meter.selected",
                NbtUtils.readBlockPos(start.getCompound("Pos")).toShortString(), start.getString("Dimension"))
                .withStyle(ChatFormatting.YELLOW));
    }
}
