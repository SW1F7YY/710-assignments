import java.util.ArrayList;
import java.util.List;

public class PopulationGenerator {

    public PopulationGenerator(String[] terminalSet, FunctionSymbol[] functionalSet){
        this.terminalSet = terminalSet;
        this.functionalSet = functionalSet;
    }

    public enum FunctionSymbol {
        ADD("+", 2),
        SUB("-", 2),
        MUL("*", 2),
        DIV("/", 2),
        SQRT("sqrt", 1);

        public final String label;
        public final int arity;

        FunctionSymbol(String label, int arity) {
            this.label = label;
            this.arity = arity;
        }
    }

    public String[] terminalSet;
    public FunctionSymbol[] functionalSet;
    public enum PopulationMethod { GROW, FULL, RAMPED }

    public List<Node> generate(PopulationMethod method, int popSize, int maxDepth) {
        List<Node> population = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            if (method == PopulationMethod.FULL) {
                population.add(buildFull(maxDepth));
            } else if (method == PopulationMethod.GROW) {
                population.add(buildGrow(maxDepth));
            } else {
                population.add(buildRamped(maxDepth));
            }
        }
        return population;
    }

    public Node buildGrow(int maxDepth) {
        int randomRootIndex = (int)(Math.random() * functionalSet.length);
        Node rootNode = new Node(functionalSet[randomRootIndex].label, "function");
        rootNode = buildGrowFromRoot(1, rootNode, maxDepth);
        return rootNode;
    };

    public Node buildFull(int maxDepth) {
        int randomRootIndex = (int)(Math.random() * functionalSet.length);
        Node rootNode = new Node(functionalSet[randomRootIndex].label, "function");

        rootNode = buildFullFromRoot(1, rootNode, maxDepth);
        return rootNode;
    }

    public Node buildRamped(int maxDepth) {
        return null;
    }

    public Node buildFullFromRoot (int currentDepth, Node currentRootNode, int maxDepth) {
        // will always be functional
        FunctionSymbol currentSymbol = null;
        for (FunctionSymbol functionSymbol : functionalSet) {
            if (currentRootNode.value.equals(functionSymbol.label)) {
                currentSymbol = functionSymbol;
                break;
            }
        }
        if (currentSymbol == null) return null; // no symbol has been found

        // loop through and populate the different nodes
        for ( int i = 0; i < currentSymbol.arity; i++) {
            Node child;
            if (currentDepth == maxDepth - 1) {
                int randomTerminalIndex = (int) (Math.random() * terminalSet.length);
                child = new Node(terminalSet[randomTerminalIndex], "terminal");
            } else {
                int randomFunctionalIndex = (int) (Math.random() * functionalSet.length);
                child = new Node(functionalSet[randomFunctionalIndex].label, "function");
                if (currentDepth + 1 < maxDepth) buildFullFromRoot(currentDepth + 1, child, maxDepth);
            }
            // recursive step
            child.parent = currentRootNode; // every node will have a parent so we can add it last
            currentRootNode.children.add(child);
        }

        return currentRootNode;
    }

    public Node buildGrowFromRoot(int currentDepth, Node currentNode, int maxDepth) {
        FunctionSymbol currentSymbol = null;
        for (FunctionSymbol functionSymbol : functionalSet) {
            if (currentNode.value.equals(functionSymbol.label)) {
                currentSymbol = functionSymbol;
                break;
            }
        }
        if (currentSymbol == null) return null;
        for (int i = 0; i < currentSymbol.arity; i++){
            Node child;
            if (currentDepth == maxDepth - 1) {
                int randomTerminalIndex = (int) (Math.random() * terminalSet.length);
                child = new Node(terminalSet[randomTerminalIndex], "terminal");
            } else {
                int functionalOrTerminalProbability = (int) (Math.random() * 2);
                if (functionalOrTerminalProbability == 1) {
                    // it's a terminal selection
                    int randomTerminalIndex = (int) (Math.random() * terminalSet.length);
                    child = new Node(terminalSet[randomTerminalIndex], "terminal");
                } else {
                    // it's a function
                    int randomFunctionalIndex = (int) (Math.random() * functionalSet.length);
                    child = new Node(functionalSet[randomFunctionalIndex].label, "function");
                    buildFullFromRoot(currentDepth + 1, child, maxDepth);
                }
            }
            child.parent = currentNode;
            currentNode.children.add(child);
        }
        return currentNode;
    }

}
