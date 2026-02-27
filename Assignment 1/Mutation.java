import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mutation {
    private final List<Node> population;
    private final int maxDepth;
    private final Random rng;
    private final List<String> terminalSet;
    private final PopulationGenerator.FunctionSymbol[] functionSet;

    public Mutation(List<Node> population, int maxDepth, Random rng, List<String> terminalSet, PopulationGenerator.FunctionSymbol[] functionSet){
        this.population = population;
        this.maxDepth = maxDepth;
        this.rng = rng;
        this.terminalSet = terminalSet;
        this.functionSet = functionSet;
    }

    public List<Node> mutate(){
        List<Node> newPopulation = new ArrayList<>();
        for (Node root: population){
            Node newRoot = root.copy();
            if (rng.nextDouble() < 0.1){
                Node currentNode = newRoot;
                // perform mutation
                int randomDepth = rng.nextInt(maxDepth);
                for (int i = 0; i < randomDepth; i++){
                    // go down the tree until children is either empty or the correct node has been reached
                    if (currentNode.children.isEmpty()) {
                        // children array is empty, this means it's a terminal. simply replace terminal with terminal
                        int randomTerminal = rng.nextInt(terminalSet.size());
                        currentNode.value = terminalSet.get(randomTerminal);
                        currentNode.children.clear();
                        currentNode.type = "terminal";
                        break;
                    } else {
                        // current node not found so we have to go deeper
                        currentNode = currentNode.children.get(rng.nextInt(currentNode.children.size()));
                    }
                }
                // max depth has been reached
                if (currentNode.type.equals("terminal")) {
                    // simply replace with a terminal
                    int randomTerminal = rng.nextInt(terminalSet.size());
                    currentNode.value = terminalSet.get(randomTerminal);
                    currentNode.children.clear();
                    currentNode.type = "terminal";
                } else {
                    // current node is a function
                    if(rng.nextInt(2) == 1){
                        // select from terminal
                        int randomTerminal = rng.nextInt(terminalSet.size());
                        currentNode.value = terminalSet.get(randomTerminal);
                        currentNode.children.clear();
                        currentNode.type = "terminal";
                    } else {
                        // select from function
                        currentNode.value = functionSet[rng.nextInt(functionSet.length)].label;
                    }
                }
            }
            newPopulation.add(newRoot);
        }

        return newPopulation;
    }
}
