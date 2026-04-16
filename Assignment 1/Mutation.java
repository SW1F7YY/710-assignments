import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mutation {
    private final int maxDepth;
    private final Random rng;
    private final List<String> terminalSet;
    private final PopulationGenerator.FunctionSymbol[] functionSet;

    public Mutation(int maxDepth, Random rng, List<String> terminalSet, PopulationGenerator.FunctionSymbol[] functionSet){
        this.maxDepth = maxDepth;
        this.rng = rng;
        this.terminalSet = terminalSet;
        this.functionSet = functionSet;
    }

    public Node mutate(Node nodeToMutate){
        PopulationGenerator generator = new PopulationGenerator(terminalSet, functionSet, rng);
        Node newNode = nodeToMutate.copy();
        Node currentNode = newNode;
        int currentDepth = 1;
        // perform mutation
        int randomDepth = rng.nextInt(maxDepth) + 1;

        while (currentDepth < randomDepth && currentNode.children.size() > 0){
            int randomChildIndex = rng.nextInt(currentNode.children.size());
            currentNode = currentNode.children.get(randomChildIndex);
            currentDepth++;
        }
        // replace in place with new node
        int randomFunctionalIndex = rng.nextInt(functionSet.length);
        currentNode.value = functionSet[randomFunctionalIndex].label;
        currentNode.type = "function";
        currentNode.children.clear();
        generator.buildFullFromRoot(currentDepth, currentNode, maxDepth);
            
        return newNode;
    }
}
