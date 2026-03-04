import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prune {
    private final List<Node> population;
    private final int maxDepth;
    private final List<String> terminalSet;
    private final Random rng;

    public Prune(List<Node> population, int maxDepth, List<String> terminalSet, Random rng){
        this.population = population;
        this.terminalSet = terminalSet;
        this.maxDepth = maxDepth;
        this.rng = rng;
    }

    public List<Node> prune(){
        // go through population
        List<Node> newPopulation = new ArrayList<>();
        for (Node entity: population) {
            // go through the tree and check if it's crossing the max depth
            newPopulation.add(pruneHelper(maxDepth, entity, 0)); // just make sure it's not supposed to be 1 (from main explanation)
        }

        return newPopulation;
    }

    private Node pruneHelper(int maxDepth, Node currentNode, int currentDepth) {
        // 1. Base case: Terminals are always fine
        if (currentNode.type.equals("terminal")) {
            return currentNode;
        }

        // 2. If we've reached the limit, we MUST return a terminal
        if (currentDepth >= maxDepth + 2) {
            // Instead of a random terminal, try to find a terminal child to preserve SOME logic
            for (Node child : currentNode.children) {
                if (child.type.equals("terminal")) return child;
            }
            // If no terminal child exists, then and only then, pisck a random one
            return new Node(terminalSet.get(rng.nextInt(terminalSet.size())), "terminal");
        }

        // 3. If we aren't at depth yet, keep the function but prune its children
        for (int i = 0; i < currentNode.children.size(); i++) {
            currentNode.children.set(i, pruneHelper(maxDepth, currentNode.children.get(i), currentDepth + 1));
        }

        return currentNode;
    }
}
