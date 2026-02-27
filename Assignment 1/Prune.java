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

    private Node pruneHelper(int maxDepth, Node currentNode, int currentDepth){
        // check if it's at the max depth
        if (currentNode.type.equals("terminal")){
            return currentNode;
        }
        if (currentDepth < maxDepth){
            // if it's not at the max depth, and it's a function, go deeper, otherwise just return the terminal
            for (int i = 0; i < currentNode.children.size(); i++){
                currentNode.children.set(i, pruneHelper(maxDepth, currentNode.children.get(i), currentDepth + 1));
            }
        } else {
            // check if one any of the kids are a terminal
            List<Node> terminalKids = new ArrayList<>();
            for (Node child: currentNode.children){
                if (child.type.equals("terminal")){
                    terminalKids.add(child);
                }
            }
            if (terminalKids.isEmpty()){
                // just get a random one from the terminal set
                currentNode = new Node(terminalSet.get(rng.nextInt(terminalSet.size())), "terminal");
            } else {
                // not empty, get a random one
                currentNode = terminalKids.get(rng.nextInt(terminalKids.size()));
            }
        }
        return currentNode;
    }
}
