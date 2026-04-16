import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericOperators {
    private final Random rng;
    private final int maxDepth;

    GenericOperators(Random rng, int maxDepth){
        this.rng = rng;
        this.maxDepth = maxDepth;
    }

    public List<Node> classicCrossover(Node A, Node B){
      
        List<Node> newNodes = new ArrayList<>();
                Node childA = A.copy();
                Node childB = B.copy();

                    List<NodePoint> pointsA = getAllPoints(childA, null, -1);
                    List<NodePoint> pointsB = getAllPoints(childB, null, -1);

                    NodePoint cpA = pointsA.get(rng.nextInt(pointsA.size()));
                    NodePoint cpB = pointsB.get(rng.nextInt(pointsB.size()));

                    
                    Node branchFromA = cpA.node.copy();
                    Node branchFromB = cpB.node.copy();

                    // Insert Branch B into Tree A
                    if (cpA.parent == null) childA = branchFromB;
                    else cpA.parent.children.set(cpA.indexInParent, branchFromB);

                    
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

                newNodes.add(childA);
                newNodes.add(childB);
            
        return newNodes;
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
