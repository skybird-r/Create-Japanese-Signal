package com.skybird.create_jp_signal.item.measurement;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;

/** Searches directed edges so a junction cannot be crossed through an impossible turn. */
public final class TrackDistanceSearch {
    public static final double MAX_DISTANCE = 10_000;
    private static final int MAX_VISITED = 20_000;

    public enum Status { FOUND, DISCONNECTED, OUT_OF_RANGE, TOO_COMPLEX }
    public record Result(Status status, double distance) {}
    private record Step(TrackEdge edge, double distance) {}

    private TrackDistanceSearch() {}

    public static Result measure(TrackGraphLocation start, TrackGraphLocation end) {
        if (start.graph != end.graph) return new Result(Status.DISCONNECTED, 0);
        TrackGraph graph = start.graph;
        TrackNode s1 = graph.locateNode(start.edge.getFirst());
        TrackNode s2 = graph.locateNode(start.edge.getSecond());
        TrackNode t1 = graph.locateNode(end.edge.getFirst());
        TrackNode t2 = graph.locateNode(end.edge.getSecond());
        if (s1 == null || s2 == null || t1 == null || t2 == null)
            return new Result(Status.DISCONNECTED, 0);
        TrackEdge forward = graph.getConnectionsFrom(s1).get(s2);
        TrackEdge backward = graph.getConnectionsFrom(s2).get(s1);
        TrackEdge target = graph.getConnectionsFrom(t1).get(t2);
        TrackEdge reverseTarget = graph.getConnectionsFrom(t2).get(t1);
        if (forward == null || backward == null || target == null || reverseTarget == null)
            return new Result(Status.DISCONNECTED, 0);

        double best = Double.POSITIVE_INFINITY;
        if (forward == target) best = Math.abs(start.position - end.position);
        if (forward == reverseTarget) best = Math.abs(start.position - (target.getLength() - end.position));

        PriorityQueue<Step> queue = new PriorityQueue<>(Comparator.comparingDouble(Step::distance));
        Map<TrackEdge, Double> distances = new HashMap<>();
        offer(queue, distances, forward, forward.getLength() - start.position);
        offer(queue, distances, backward, start.position);
        boolean truncated = best > MAX_DISTANCE && Double.isFinite(best);
        int visited = 0;
        while (!queue.isEmpty()) {
            Step step = queue.remove();
            if (step.distance != distances.get(step.edge)) continue;
            if (step.distance >= best) break;
            if (step.distance > MAX_DISTANCE) {
                truncated = true;
                break;
            }
            if (++visited > MAX_VISITED) return new Result(Status.TOO_COMPLEX, 0);
            for (TrackEdge next : graph.getConnectionsFrom(step.edge.node2).values()) {
                // Portal edges have zero length in Create; exclude them from metre measurements.
                if (next.isInterDimensional() || !step.edge.canTravelTo(next)) continue;
                if (next == target) best = Math.min(best, step.distance + end.position);
                if (next == reverseTarget)
                    best = Math.min(best, step.distance + target.getLength() - end.position);
                double distance = step.distance + next.getLength();
                if (distance > MAX_DISTANCE) truncated = true;
                else if (distance < best) offer(queue, distances, next, distance);
            }
        }
        if (best <= MAX_DISTANCE) return new Result(Status.FOUND, Math.max(0, best));
        return new Result(truncated ? Status.OUT_OF_RANGE : Status.DISCONNECTED, 0);
    }

    private static void offer(PriorityQueue<Step> queue, Map<TrackEdge, Double> distances,
                              TrackEdge edge, double distance) {
        if (distance >= distances.getOrDefault(edge, Double.POSITIVE_INFINITY)) return;
        distances.put(edge, distance);
        queue.add(new Step(edge, distance));
    }
}
