import jdk.dynalink.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PopulationGenerator {
    private final Random rng;
    private final List<String> terminalSet;
    private final FunctionSymbol[] functionalSet;
    public enum PopulationMethod { GROW, FULL, RAMPED }

    public PopulationGenerator(List<String> terminalSet, FunctionSymbol[] functionalSet, Random rng){
        this.terminalSet = terminalSet;
        this.functionalSet = functionalSet;
        this.rng = rng;
    }

    //to enable searching for functional symbol

    public enum FunctionSymbol {
        ADD("+", 2, (args) -> args[0] + args[1]),
        SUB("-", 2, (args) -> args[0] - args[1]),
        MUL("*", 2, (args) -> args[0] * args[1]),
        DIV("/", 2, (args) -> args[0] / args[1]),
        SQRT("sqrt", 1, (args) -> Math.sqrt(args[0])),
        POW("^", 2, (args) -> Math.pow(args[0], args[1])),
        LOG("log10", 1, args -> Math.log10(args[0])),
        LN("ln", 1, args -> Math.log(args[0])), // Math.log is natural log in Java
        LOG2("log2", 1, args -> Math.log(args[0]) / Math.log(2));

        public final String label;
        public final int arity;
        public final Operation functionLogic;

        FunctionSymbol(String label, int arity, Operation functionLogic) {
            this.label = label;
            this.arity = arity;
            this.functionLogic = functionLogic;
        }

        public double apply(double... args) {
            if (args.length != arity) {
                throw new IllegalArgumentException(label + " requires " + arity + " arguments.");
            }

            // Execute the logic (ADD, MUL, etc.)
            double result = functionLogic.execute(args);

            // SAFETY CHECK: If the result is broken (Infinity or NaN),
            // we return a neutral value or a small constant to keep the GP alive.
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                if (Double.isNaN(result)) System.out.println("NaN: " + Arrays.toString(args));
                return 1.0;
            }

            return result;
        }

        @FunctionalInterface
        public interface Operation {
            double execute(double[] args);
        }
    }

    public FunctionSymbol getFunctionSymbol(String value) {
        for (FunctionSymbol symbol : FunctionSymbol.values()) {
            if (symbol.label.equals(value)) {
                return symbol;
            }
        }
        return null;
    }



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
        int randomRootIndex = rng.nextInt(functionalSet.length);
        Node rootNode = new Node(functionalSet[randomRootIndex].label, "function");
        rootNode = buildGrowFromRoot(1, rootNode, maxDepth);
        return rootNode;
    };

    public Node buildFull(int maxDepth) {
        int randomRootIndex = rng.nextInt(functionalSet.length);
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
                int randomTerminalIndex = rng.nextInt(terminalSet.size());
                child = new Node(terminalSet.get(randomTerminalIndex), "terminal");
            } else {
                int randomFunctionalIndex = rng.nextInt(functionalSet.length);
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
                int randomTerminalIndex = rng.nextInt(terminalSet.size());
                child = new Node(terminalSet.get(randomTerminalIndex), "terminal");
            } else {
                int functionalOrTerminalProbability = rng.nextInt(2);
                if (functionalOrTerminalProbability == 1) {
                    // it's a terminal selection
                    int randomTerminalIndex = rng.nextInt(terminalSet.size());
                    child = new Node(terminalSet.get(randomTerminalIndex), "terminal");
                } else {
                    // it's a function
                    int randomFunctionalIndex = rng.nextInt(functionalSet.length);
                    child = new Node(functionalSet[randomFunctionalIndex].label, "function");
                    buildFullFromRoot(currentDepth + 1, child, maxDepth);
                }
            }
            child.parent = currentNode;
            currentNode.children.add(child);
        }
        return currentNode;
    }
    public List<Node> cleanUpTrees (List<Node> population) {
        return null;
    }
}
