import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericOperators {
    private final List<Node> population;
    private final Random rng;
    private final int maxDepth;

    GenericOperators(List<Node> population, Random rng, int maxDepth){
        this.population = population;
        this.rng = rng;
        this.maxDepth = maxDepth;
    }

    public List<Node> classicCrossover() {
        List<Node> newPopulation = new ArrayList<>();

        for (int i = 0; i < population.size(); i += 2) {
            if (i + 1 < population.size()) {
                Node childA = population.get(i).copy();
                Node childB = population.get(i + 1).copy();

                if (rng.nextDouble() < 0.7) {
                    List<NodePoint> pointsA = getAllPoints(childA, null, -1);
                    List<NodePoint> pointsB = getAllPoints(childB, null, -1);

                    NodePoint cpA = pointsA.get(rng.nextInt(pointsA.size()));
                    NodePoint cpB = pointsB.get(rng.nextInt(pointsB.size()));

                    // --- THE CRITICAL FIX ---
                    // Extract the "Genetic Material" before performing the surgery
                    Node branchFromA = cpA.node.copy();
                    Node branchFromB = cpB.node.copy();

                    // Insert Branch B into Tree A
                    if (cpA.parent == null) childA = branchFromB;
                    else cpA.parent.children.set(cpA.indexInParent, branchFromB);

                    // Insert Branch A into Tree B
                    // Insert Branch A into Tree B
                    if (cpB.parent == null) {
                        childB = branchFromA;
                    } else {
                        // SAFETY CHECK: Ensure the index still exists
                        if (cpB.indexInParent < cpB.parent.children.size()) {
                            cpB.parent.children.set(cpB.indexInParent, branchFromA);
                        } else {
                            // If the index is gone (due to a previous swap/mutation),
                            // just add it as a new child or skip
                            cpB.parent.children.add(branchFromA);
                        }
                    }
                }

                newPopulation.add(childA);
                newPopulation.add(childB);
            } else {
                newPopulation.add(population.get(i).copy());
            }
        }
        return newPopulation;
    }

    // Helper class to track a node and its parent
    private static class NodePoint {
        Node node;
        Node parent;
        int indexInParent;

        NodePoint(Node n, Node p, int i) {
            this.node = n; this.parent = p; this.indexInParent = i;
        }
    }

    // Recursive helper to find every node in the tree
    private List<NodePoint> getAllPoints(Node current, Node parent, int index) {
        List<NodePoint> points = new ArrayList<>();
        points.add(new NodePoint(current, parent, index));
        for (int i = 0; i < current.children.size(); i++) {
            points.addAll(getAllPoints(current.children.get(i), current, i));
        }
        return points;
    }
}
