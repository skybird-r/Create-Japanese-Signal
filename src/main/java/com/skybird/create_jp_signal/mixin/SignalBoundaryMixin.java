package com.skybird.create_jp_signal.mixin;


import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBlock.SignalType;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity.SignalState;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.create.mixin_interface.ISignalBoundary;
import com.skybird.create_jp_signal.create.mixin_interface.ISignalEdgeGroup;
import com.skybird.create_jp_signal.create.train.schedule.OperationType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Mixin(value = SignalBoundary.class, remap = false)
public abstract class SignalBoundaryMixin implements ISignalBoundary {

    //@Shadow public Couple<TrackNodeLocation> edgeLocation;
    @Shadow public Couple<Map<BlockPos, Boolean>> blockEntities;
    @Shadow public Couple<UUID> groups;
    @Shadow public Couple<SignalState> cachedStates;
    @Shadow public Couple<SignalType> types;

    @Shadow abstract SignalState resolveSignalChain(TrackGraph graph, boolean side);
    @Shadow abstract boolean isForcedRed(boolean primary);
    //@Shadow abstract double getLocationOn(TrackEdge edge);

    @Unique public Couple<Double> reserverTrainMaxSpeeds; 
    // @Unique public Couple<Integer> nextRedIndexes;
	@Unique public Couple<Pair<SignalBoundary, Boolean>> nextEntrySignals = Couple.create(null, null);
	@Unique public Couple<OperationType> OperationTypes;

	



    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void create_jp_signal_onConstructorEnd(CallbackInfo ci) {
        this.reserverTrainMaxSpeeds = Couple.create(() -> 0.0);
        // this.nextRedIndexes = Couple.create(() -> null);
		this.OperationTypes = Couple.create(() -> OperationType.TRAIN);
		//this.nextEntrySignals = Couple.create(null, null);
    }


	@ModifyVariable(
        method = "tick",
        at = @At(value = "STORE"),
        ordinal = 1
    )
    private boolean create_jp_signal_onPreTrainsLoopStart(boolean front) {
        reserverTrainMaxSpeeds.set(front, 0.0);
		OperationTypes.set(front, OperationType.TRAIN);
        return front;
    }


    @ModifyVariable(
        method = "tickState",
        at = @At(value = "STORE"),
        ordinal = 0
    )
    private boolean create_jp_signal_onLoopStart(boolean current) {
        // nextRedIndexes.set(current, null);
		if (this.types.get(current) == SignalType.ENTRY_SIGNAL) {
			nextEntrySignals.set(current, null); 
		}
        return current;
    }


    @Inject(
        method = "tickState",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/utility/Couple;set(ZLjava/lang/Object;)V",
			ordinal = 2,
			shift = At.Shift.AFTER
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void create_jp_signal_overwriteSignalLogic(
        TrackGraph graph,
        CallbackInfo ci,
        boolean[] var2,
        int var3,
        int var4,
        boolean current,
        Map set,
        boolean forcedRed,
        UUID group,
        Map signalEdgeGroups,
        SignalEdgeGroup signalEdgeGroup
    ) {

        if (this.types.get(current) == SignalType.CROSS_SIGNAL) {
            boolean isNotSelfReservationAtThisBoundary = forcedRed || signalEdgeGroup.reserved != (Object)this;
            cachedStates.set(current, isNotSelfReservationAtThisBoundary ? SignalState.RED : SignalState.GREEN);
        } else {
            boolean occupiedUnlessBySelf = forcedRed || ((ISignalEdgeGroup)signalEdgeGroup).isOccupiedUnless2((SignalBoundary)(Object)this);
            cachedStates.set(current, occupiedUnlessBySelf ? SignalState.RED : resolveSignalChain(graph, current));
        }

    }

	//思い処理を吹っ飛ばす
	@Redirect(
        method = "tickState",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/signal/SignalBoundary;resolveSignalChain(Lcom/simibubi/create/content/trains/graph/TrackGraph;Z)Lcom/simibubi/create/content/trains/signal/SignalBlockEntity$SignalState;"
        )
    )
    private SignalBlockEntity.SignalState redirectResolveChain(
        SignalBoundary self,
        TrackGraph graph,
        boolean current
    ) {
		return SignalState.RED;
		
    }

	public int getNextRedIndex(TrackGraph graph, int currentIndex, int max, boolean first) {
		if (max > 10) {
			max = 10;
		}
		if (currentIndex >= max) {
			return max;
		}
		//if (this.cachedStates.get(first) == SignalState.GREEN) return max;
		if (this.cachedStates.get(first) == SignalState.RED || this.cachedStates.get(first) == SignalState.INVALID) {
			return currentIndex;
		}
		if (nextEntrySignals.get(first) != null) {
			boolean nextFirst = nextEntrySignals.get(first).getSecond();
			SignalBoundary nextSignal = nextEntrySignals.get(first).getFirst();
			if (nextSignal == (SignalBoundary)(Object)this) {
				nextEntrySignals.set(first, null);
				return currentIndex;
			}
			if (this.types.get(first) == SignalType.CROSS_SIGNAL) {
				if (nextSignal.types.get(nextFirst) == SignalType.CROSS_SIGNAL){
					return currentIndex + 1;
				} else {
					return ((ISignalBoundary)nextSignal).getNextRedIndex(graph, currentIndex, max, nextFirst);
				}
			} else {
				return ((ISignalBoundary)nextSignal).getNextRedIndex(graph, currentIndex + 1, max, nextFirst);
			}
		}
		// nextSignalが未探索、entrysignalなら、
		if (this.types.get(first) == SignalType.ENTRY_SIGNAL) {
			Couple<TrackNode> nodes = ((TrackEdgePoint)(Object)this).edgeLocation.map(graph::locateNode);
			if (nodes.getFirst() == null || nodes.getSecond() == null) {
				return currentIndex; // null
			}
			TrackNode startNode = first ? nodes.getSecond() : nodes.getFirst();
			TrackNode currentNode = startNode;
			TrackNode previousNode = first ? nodes.getFirst() : nodes.getSecond();
			
			TrackEdge currentEdge = graph.getConnection(Couple.create(previousNode, currentNode));
			if (currentEdge == null) return currentIndex; // null

			double distanceTraveled = 0;
			
			while (distanceTraveled < 4096) {
				// エッジ
				double positionOnEdge = 0;
				// 出発時、edge内の手前の信号をスキップ
				if (currentNode.equals(startNode)) {
					positionOnEdge = ((TrackEdgePoint)(Object)this).getLocationOn(currentEdge);
				}
		
				while (true) {
					SignalBoundary nextSignal = currentEdge.getEdgeData().next(EdgePointType.SIGNAL, positionOnEdge);
					if (nextSignal == null) break; // 信号なし
		
					if (!nextSignal.canNavigateVia(currentNode)) {
						return currentIndex + 1;
					}
					
					boolean nextSignalSide = nextSignal.isPrimary(currentNode);
					//SignalState nextState = nextSignal.cachedStates.get(nextSignalSide);
					//SignalType nextType = nextSignal.types.get(nextSignalSide);
					nextEntrySignals.set(first, Pair.of(nextSignal, nextSignalSide));
					return ((ISignalBoundary)nextSignal).getNextRedIndex(graph, currentIndex + 1, max, nextSignalSide);
					
					//positionOnEdge = nextSignal.getLocationOn(currentEdge);
				}
		
				// 次のエッジ
				Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(currentNode);
				TrackEdge nextEdge = null;
				int traversableOptions = 0;
				
				List<TrackEdge> validEdges = new ArrayList<>();
				for (TrackEdge edge : connections.values()) {
					if (edge.node2.equals(previousNode)) continue;
					if (currentEdge != null && !currentEdge.canTravelTo(edge)) continue;
					traversableOptions++;
					validEdges.add(edge);
				}
				
				// 分岐点・終点の判定、経路選択
				if (traversableOptions == 0) {
					// 端点
					return currentIndex + 1;
				} else if (traversableOptions > 1) {
					// 直線選択
					TrackEdge bestChoice = null;
					double bestDotProduct = -2.0;
	
					Vec3 incomingDirection = currentEdge.getDirection(false);
	
					for (TrackEdge candidateEdge : validEdges) {
						Vec3 outgoingDirection = candidateEdge.getDirection(true);
						double dot = incomingDirection.dot(outgoingDirection);
	
						if (dot > bestDotProduct) {
							bestDotProduct = dot;
							bestChoice = candidateEdge;
						}
					}
					nextEdge = bestChoice;
				} else {
					// 進行可能経路1つ
					nextEdge = validEdges.get(0);
				}
				if (nextEdge == null) return currentIndex;//null
				//次の区画へ移動
				distanceTraveled += currentEdge.getLength();
				previousNode = currentNode;
				currentNode = nextEdge.node2;
				currentEdge = nextEdge;
			}
		}
		// nextSignalがnull、crosssignalなら
		return currentIndex + 1;
	}

    
	public int getNextRedIndex(TrackGraph graph, BlockPos blockEntity, int max) {
		// --- 実行条件のチェック ---
		boolean first;
		if (blockEntities.getFirst().containsKey(blockEntity)) {
			first = true;
		} else if (blockEntities.getSecond().containsKey(blockEntity)) {
			first = false;
		} else {
			return 0;
		}
		if (graph != null){
			return this.getNextRedIndex(graph, 0, max, first);
		} else {
			return 0;
		}

		// if (nextRedIndexes.get(first) != null) {
		// 	return nextRedIndexes.get(first);
		// }

		// Couple<TrackNode> nodes = ((TrackEdgePoint)(Object)this).edgeLocation.map(graph::locateNode);
		// if (nodes.getFirst() == null || nodes.getSecond() == null) {
		// 	return null;
		// }
		// TrackNode startNode = first ? nodes.getSecond() : nodes.getFirst();

		// //Create.LOGGER.info("same: {}", first == side);
		
		// if (this.cachedStates.get(first) == SignalState.RED) {
		// 	nextRedIndexes.set(first, 0);
		// 	return nextRedIndexes.get(first);
		// }

		// if (this.types.get(first) != SignalType.ENTRY_SIGNAL) {
		// 	nextRedIndexes.set(first, Integer.MAX_VALUE);
		// 	return nextRedIndexes.get(first);
		// }
	
		// // --- 探索の準備 ---
		// int signalIndex = 0;
		// double distanceTraveled = 0;
		
		// Couple<TrackNode> initialNodes = ((TrackEdgePoint)(Object)this).edgeLocation.map(graph::locateNode);
		// if (initialNodes.getFirst() == null || initialNodes.getSecond() == null) return null;
	
		// TrackNode currentNode = startNode;
		// TrackNode previousNode;
		// if (currentNode.equals(initialNodes.getFirst())) {
		// 	previousNode = initialNodes.getSecond();
		// } else {
		// 	previousNode = initialNodes.getFirst();
		// }
		
		// TrackEdge currentEdge = graph.getConnection(Couple.create(previousNode, currentNode));
		// if (currentEdge == null) return null;
	
		// // --- 探索ループ ---
		// while (distanceTraveled < 1000) {
			
		// 	// --- フェーズ1: 現在いるエッジ上の信号を全て探索 ---
		// 	double positionOnEdge = 0;
		// 	// 出発点となる信号自身はスキップするため、斥候の現在地と比較
		// 	if (currentNode.equals(startNode)) {
		// 		positionOnEdge = ((TrackEdgePoint)(Object)this).getLocationOn(currentEdge);
		// 	}
	
		// 	while (true) {
		// 		SignalBoundary nextSignal = currentEdge.getEdgeData().next(EdgePointType.SIGNAL, positionOnEdge);
		// 		if (nextSignal == null) break; // このエッジにもう信号はない
	
		// 		if (!nextSignal.canNavigateVia(currentNode)) {
		// 			nextRedIndexes.set(first, signalIndex + 1);
		// 			return nextRedIndexes.get(first);
		// 		}
				
		// 		boolean nextSignalSide = nextSignal.isPrimary(currentNode);
		// 		SignalState nextState = nextSignal.cachedStates.get(nextSignalSide);
		// 		SignalType nextType = nextSignal.types.get(nextSignalSide);
				
		// 		signalIndex++;
	
		// 		if (nextState == SignalState.RED) {
		// 			nextRedIndexes.set(first, signalIndex);
		// 			return nextRedIndexes.get(first);
		// 		}
		// 		if (nextType == SignalType.CROSS_SIGNAL) {
		// 			nextRedIndexes.set(first, Integer.MAX_VALUE);
		// 			return nextRedIndexes.get(first);
		// 		}
		// 		positionOnEdge = nextSignal.getLocationOn(currentEdge);
		// 	}
	
		// 	// --- フェーズ2: 次のエッジへ移動 ---
		// 	Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(currentNode);
		// 	TrackEdge nextEdge = null;
		// 	int traversableOptions = 0;
	
		// 	for (TrackEdge edge : connections.values()) {
		// 		if (edge.node2.equals(previousNode)) continue;
		// 		if (currentEdge != null && !currentEdge.canTravelTo(edge)) continue;
		// 		traversableOptions++;
		// 		nextEdge = edge;
		// 	}
			
		// 	if (traversableOptions != 1 || (nextEdge != null && nextEdge.getEdgeData().hasIntersections())) {
		// 		nextRedIndexes.set(first, signalIndex + 1);
		// 		return nextRedIndexes.get(first); // 分岐点・終点は次の障害物
		// 	}
	
		// 	distanceTraveled += nextEdge.getLength();
		// 	previousNode = currentNode;
		// 	currentNode = nextEdge.node2;
		// 	currentEdge = nextEdge;
		// }
		// nextRedIndexes.set(first, Integer.MAX_VALUE);
		// return nextRedIndexes.get(first);
	}

	@Override
	public Couple<Double> getReserverMaxSpeeds() {
		return reserverTrainMaxSpeeds;
	}

	@Override
	public boolean isRed(BlockPos blockEntity) {
		if (blockEntities.getFirst().containsKey(blockEntity)) {
			return cachedStates.getFirst() == SignalState.RED || cachedStates.getFirst() == SignalState.INVALID;
		} else if (blockEntities.getSecond().containsKey(blockEntity)) {
			return cachedStates.getSecond() == SignalState.RED || cachedStates.getSecond() == SignalState.INVALID;
		}
		return true;
	}

	@Override
    public double getReserverMaxSpeed(BlockPos blockEntity) {
        for (boolean first : Iterate.trueAndFalse) {
			Map<BlockPos, Boolean> set = blockEntities.get(first);
			if (set.containsKey(blockEntity))
				return reserverTrainMaxSpeeds.get(first);
		}
		return 0.0;
    }

	@Override
	public Couple<Pair<SignalBoundary, Boolean>> getNextEntrySignals() {
		return nextEntrySignals;
	}

	@Override
	public OperationType getReserverOperationType(BlockPos blockEntity) {
		if (blockEntities.getFirst().containsKey(blockEntity)) {
			return OperationTypes.getFirst();
		} else if (blockEntities.getSecond().containsKey(blockEntity)) {
			return OperationTypes.getSecond();
		}
		return OperationType.TRAIN;
	}

	@Override
	public Couple<OperationType> getReserverOperationTypes() {
		return this.OperationTypes;
	}


}