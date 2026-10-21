package com.simibubi.create.content.trains.graph;

import java.util.HashMap;

import com.skybird.create_jp_signal.item.measurement.TrackDistanceSearch;
import com.skybird.create_jp_signal.item.measurement.TrackDistanceSearch.Status;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/** Executable regression checks using Create's actual graph and turn compatibility. */
public final class TrackDistanceSearchTest {
    private static int checks;

    public static void main(String[] args) {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        Fixture line = new Fixture();
        TrackEdge first = line.edge(0, 1, 10);
        found(line.at(first, 2), line.at(first, 8), 6);
        found(line.at(first, 8), line.at(first, 2), 6);
        found(line.at(first, 2), line.at(first, 2), 0);
        found(line.at(first, 2), line.at(line.reverse(first), 2), 6);
        first.getEdgeData().getPoints().add(new com.simibubi.create.content.trains.signal.SignalBoundary());
        found(line.at(first, 2), line.at(first, 8), 6);
        TrackEdge middle = line.edge(1, 2, 20);
        TrackEdge last = line.edge(2, 3, 30);
        found(line.at(first, 2), line.at(last, 10), 38);
        found(line.at(last, 10), line.at(first, 2), 38);
        found(line.at(first, 10), line.at(middle, 0), 0);

        Fixture another = new Fixture();
        TrackEdge other = another.edge(0, 1, 5);
        status(line.at(first, 2), another.at(other, 2), Status.DISCONNECTED);
        TrackEdge island = line.edge(5, 6, 5);
        status(line.at(first, 2), line.at(island, 2), Status.DISCONNECTED);

        Fixture branches = new Fixture();
        TrackEdge entry = branches.edge(0, 1, 10);
        branches.edge(1, 2, 100);
        branches.edge(2, 4, 100);
        branches.edge(1, 3, 20);
        branches.edge(3, 4, 30);
        TrackEdge exit = branches.edge(4, 5, 10);
        found(branches.at(entry, 5), branches.at(exit, 5), 60);
        // A loop must neither cause infinite exploration nor hide the shorter route.
        branches.edge(3, 6, 1);
        branches.edge(6, 1, 1);
        found(branches.at(entry, 5), branches.at(exit, 5), 60);

        Fixture junction = new Fixture();
        TrackEdge approach = junction.edge(0, 1, 5);
        TrackEdge perpendicular = junction.edge(1, 2, 5, new Vec3(0, 0, 1));
        status(junction.at(approach, 2), junction.at(perpendicular, 2), Status.DISCONNECTED);

        Fixture range = new Fixture();
        TrackEdge longEdge = range.edge(0, 1, 25_000);
        found(range.at(longEdge, 5_000), range.at(longEdge, 15_000), 10_000);
        status(range.at(longEdge, 5_000), range.at(longEdge, 15_000.01), Status.OUT_OF_RANGE);
        found(range.at(longEdge, 12_000), range.at(longEdge, 12_001), 1);
        TrackEdge tail = range.edge(1, 2, 25_000);
        found(range.at(longEdge, 24_999), range.at(tail, 1), 2);
        found(range.at(longEdge, 24_999), range.at(tail, 9_999), 10_000);
        status(range.at(longEdge, 24_999), range.at(tail, 9_999.01), Status.OUT_OF_RANGE);

        Fixture portal = new Fixture();
        TrackEdge beforePortal = portal.edge(0, 1, 5);
        for (int id : new int[] { 2, 3 }) {
            TrackNode netherNode = portal.node(id);
            portal.graph.nodes.remove(netherNode.getLocation());
            netherNode.getLocation().dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_nether"));
            portal.graph.nodes.put(netherNode.getLocation(), netherNode);
        }
        portal.edge(1, 2, 0);
        portal.edge(2, 3, 5);
        portal.edge(3, 4, 0);
        TrackEdge afterPortal = portal.edge(4, 5, 5);
        status(portal.at(beforePortal, 2), portal.at(afterPortal, 2), Status.DISCONNECTED);

        Fixture complex = new Fixture();
        TrackEdge begin = complex.edge(0, 1, .01);
        TrackEdge end = begin;
        for (int i = 1; i < 20_010; i++) end = complex.edge(i, i + 1, .01);
        status(complex.at(begin, .005), complex.at(end, .005), Status.TOO_COMPLEX);
        System.out.println("Track distance search: " + checks + " checks passed");
    }

    private static void found(TrackGraphLocation start, TrackGraphLocation end, double expected) {
        var actual = TrackDistanceSearch.measure(start, end);
        if (actual.status() != Status.FOUND || Math.abs(actual.distance() - expected) > 1e-6)
            throw new AssertionError("Expected " + expected + " m, got " + actual);
        checks++;
    }

    private static void status(TrackGraphLocation start, TrackGraphLocation end, Status expected) {
        var actual = TrackDistanceSearch.measure(start, end);
        if (actual.status() != expected) throw new AssertionError("Expected " + expected + ", got " + actual);
        checks++;
    }

    private static final class Fixture {
        final TrackGraph graph = new TrackGraph();
        final java.util.Map<Integer, TrackNode> nodes = new HashMap<>();

        TrackNode node(int id) {
            return nodes.computeIfAbsent(id, i -> {
                TrackNode node = new TrackNode(new TrackNodeLocation(i, 0, 0).in(
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation("overworld"))), i, new Vec3(0, 1, 0));
                graph.addNode(node);
                return node;
            });
        }

        TrackEdge edge(int from, int to, double length) {
            return edge(from, to, length, new Vec3(1, 0, 0));
        }

        TrackEdge edge(int from, int to, double length, Vec3 tangent) {
            TrackNode a = node(from), b = node(to);
            TrackEdge edge = new TestEdge(a, b, length, tangent);
            graph.connectionsByNode.computeIfAbsent(a, n -> new HashMap<>()).put(b, edge);
            graph.connectionsByNode.computeIfAbsent(b, n -> new HashMap<>()).put(a,
                new TestEdge(b, a, length, tangent.scale(-1)));
            return edge;
        }

        TrackEdge reverse(TrackEdge edge) {
            return graph.getConnectionsFrom(edge.node2).get(edge.node1);
        }

        TrackGraphLocation at(TrackEdge edge, double position) {
            TrackGraphLocation location = new TrackGraphLocation();
            location.graph = graph;
            location.edge = Couple.create(edge.node1.getLocation(), edge.node2.getLocation());
            location.position = position;
            return location;
        }
    }

    // Explicit lengths and endpoint tangents let fixtures model curves and loops
    // without requiring a running Minecraft world. canTravelTo remains Create's code.
    private static final class TestEdge extends TrackEdge {
        private final double length;
        private final Vec3 tangent;

        TestEdge(TrackNode from, TrackNode to, double length, Vec3 tangent) {
            super(from, to, null, null);
            this.length = length;
            this.tangent = tangent;
        }

        @Override public double getLength() { return length; }
        @Override public Vec3 getDirection(boolean fromFirst) { return tangent; }
    }
}
