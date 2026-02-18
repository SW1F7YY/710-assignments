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
        // build the tree root
        int randomRootIndex = (int)(Math.random() * functionalSet.length);
        Node rootNode = new Node(functionalSet[randomRootIndex].label);
        return null;
    };

    public Node buildFull(int maxDepth) {
        int randomRootIndex = (int)(Math.random() * functionalSet.length);
        Node rootNode = new Node(functionalSet[randomRootIndex].label);
        return null;
    }

    public Node buildRamped(int maxDepth) {
        return null;
    }
}