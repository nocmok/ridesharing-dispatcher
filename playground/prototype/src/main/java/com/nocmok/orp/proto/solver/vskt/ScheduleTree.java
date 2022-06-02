package com.nocmok.orp.proto.solver.vskt;

import com.nocmok.orp.proto.graph.Graph;
import com.nocmok.orp.proto.pojo.GPS;
import com.nocmok.orp.proto.solver.ScheduleCheckpoint;
import com.nocmok.orp.proto.solver.Vehicle;
import com.nocmok.orp.proto.solver.common.KineticTree;
import com.nocmok.orp.proto.solver.common.ShortestPathSolver;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

// Дерево для построения всех вариантов валидных маршрутов тс
public class ScheduleTree {

    private Vehicle vehicle;
    private VSKTORPInstance instance;
    private ShortestPathSolver shortestPathSolver;
    private KineticTree<ScheduleCheckpoint, ScheduleNode> kineticTree;

    public ScheduleTree(VSKTVehicle vehicle, VSKTORPInstance instance) {
        this.vehicle = vehicle;
        this.instance = instance;
        this.shortestPathSolver = new ShortestPathSolver(instance.getGraph());
        this.kineticTree = new KineticTree<>(ScheduleNode::new,
                new KineticTree.Validator<ScheduleCheckpoint, ScheduleNode>() {
                    @Override public boolean validate(ScheduleNode parent, ScheduleNode child) {
                        if (child.getValue().isDepartureCheckpoint()) {
                            return child.getCapacity() >= child.getValue().getRequest().getLoad() &&
                                    child.getBestTime() <= child.getValue().getRequest().getLatestDepartureTime();
                        } else {
                            return child.getBestTime() <= child.getValue().getRequest().getLatestArrivalTime();
                        }
                    }

                    @Override public boolean validate(ScheduleNode tree) {
                        if (tree.getValue().isDepartureCheckpoint()) {
                            return tree.getCapacity() >= tree.getValue().getRequest().getLoad() &&
                                    tree.getBestTime() <= tree.getValue().getRequest().getLatestDepartureTime();
                        } else {
                            return tree.getBestTime() <= tree.getValue().getRequest().getLatestArrivalTime();
                        }
                    }
                },
                new KineticTree.Aggregator<ScheduleCheckpoint, ScheduleNode>() {
                    @Override public void aggregate(ScheduleNode parent, ScheduleNode child) {
                        if (parent.getValue().isDepartureCheckpoint()) {
                            child.setCapacity(parent.getCapacity() - parent.getValue().getRequest().getLoad());
                        } else {
                            child.setCapacity(parent.getCapacity() + parent.getValue().getRequest().getLoad());
                        }
                        child.setBestTime(parent.getBestTime() +
                                (int) (shortestPathSolver.dijkstra(parent.getValue().getNode(), child.getValue().getNode()).getDistance() /
                                        vehicle.getAverageVelocity() +
                                        0.5));
                    }

                    @Override public void aggregate(ScheduleNode tree) {
                        int startNode = vehicle.getNextNode().orElseGet(() -> closestNode(instance.getGraph(), vehicle.getGps()));
                        int startTime =
                                instance.getTime() + (int) (distance(vehicle.getGps(), instance.getGraph().getGps(startNode)) / vehicle.getAverageVelocity());
                        tree.setBestTime(startTime +
                                (int) (shortestPathSolver.dijkstra(startNode, tree.getValue().getNode()).getDistance() / vehicle.getAverageVelocity() + 0.5));
                        tree.setCapacity(vehicle.getCurrentCapacity());
                    }
                });
    }

    public ScheduleTree(ScheduleTree other) {
        this.vehicle = other.vehicle;
        this.instance = other.instance;
        this.shortestPathSolver = other.shortestPathSolver;
        this.kineticTree = new KineticTree<>(other.kineticTree);
    }

    private double distance(GPS startPoint, GPS endPoint) {
        return Math.hypot(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }

    private int closestNode(Graph graph, GPS point) {
        return IntStream.range(0, graph.nNodes())
                .boxed()
                .min(Comparator.comparingDouble(node -> distance(graph.getGps(node), point)))
                .orElse(0);
    }

    public void passCheckpoint(ScheduleCheckpoint checkpoint) {
        kineticTree.descendRoot(checkpoint);
    }

    public void insert(ScheduleCheckpoint pickup, ScheduleCheckpoint dropoff) {
        if (!pickup.isDepartureCheckpoint() || !dropoff.isArrivalCheckpoint()) {
            throw new RuntimeException("expected (pickup, dropoff) pair");
        }
        kineticTree.insert(pickup, dropoff);
    }

    public void insertDropoffCheckpoint(ScheduleCheckpoint dropoff) {
        if (!dropoff.isArrivalCheckpoint()) {
            throw new RuntimeException("expected dropoff checkpoint");
        }
        kineticTree.insert(dropoff);
    }

    public void forEachSchedule(Consumer<List<ScheduleCheckpoint>> callback) {
        kineticTree.forEachPermutation(callback);
    }

    public void clear() {
        kineticTree.clear();
    }
}
