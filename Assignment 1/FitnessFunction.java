import java.util.List;

public class FitnessFunction {
    public static int noOfChildren = 0;
    public double calculateFitness(Node rootNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders, double complexityPenalty ) {
        // Handle a single-node tree (just a terminal)
        noOfChildren = 0;
        if (rootNode.type.equals("terminal")) {
            return getTerminalValue(rootNode, trainingDataTerminalValues, terminalPlaceholders);
        }
        // TODO: 1. Add List<Node> sample and double similarityWeight to this method's parameters
        // TODO: 2. Calculate similarity penalty: double similarityPenalty = calculateSimilarities(rootNode, sample) * similarityWeight;
        // TODO: 3. Add similarityPenalty to the return value below
        // TODO: 4. In Main.java, pass a random sample of k trees from the population when calling calculateFitness
        return evaluate(rootNode, trainingDataTerminalValues, terminalPlaceholders) + (complexityPenalty * noOfChildren);
    }

    private double evaluate(Node currentNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders) {
        // Base Case: If it's a terminal, look up its value
        noOfChildren++;
        if (currentNode.type.equals("terminal")) {
            return getTerminalValue(currentNode, trainingDataTerminalValues, terminalPlaceholders);
        }

        // Recursive Step: Evaluate all children first
        double[] childResults = new double[currentNode.children.size()];
        for (int i = 0; i < currentNode.children.size(); i++) {
            childResults[i] = evaluate(currentNode.children.get(i), trainingDataTerminalValues, terminalPlaceholders);
        }

        // Apply the function (e.g., +, -, *, /)
        // Note: You should pass your Function Map or use a switch here to avoid object creation
        return applyFunction(currentNode.value, childResults);
    }

    private double getTerminalValue(Node node, double[] values, List<String> placeholders) {
        // 1. Try to find it in the lag list (L1, L2, etc.)
        int index = placeholders.indexOf(node.value);
        if (index != -1) {
            return values[index];
        }
        // 2. If not found in placeholders, it's a constant number (e.g., "1.5")
        try {
            return Double.parseDouble(node.value);
        } catch (NumberFormatException e) {
            return 0.0; // Fallback
        }
    }

    private double applyFunction(String operator, double[] args) {
        return switch (operator) {
            case "+" -> args[0] + args[1];
            case "-" -> args[0] - args[1];
            case "*" -> args[0] * args[1];

            // Protected Division: Avoids near-zero explosions
            case "/" -> {
                if (args.length < 2) yield 1.0; // Safety fallback
                yield (Math.abs(args[1]) > 1e-6) ? args[0] / args[1] : 1.0;
            }

            // Protected SQRT: Avoids NaN for negative numbers
            case "SQRT" -> Math.sqrt(Math.abs(args[0]));

            // Protected POW: Avoids Infinity and NaN
            case "POW" -> {
                double base = args[0];
                double exponent = args[1];

                // Limit exponent to prevent Infinity (e.g., 100^100 is too big)
                if (exponent > 10) exponent = 10;
                if (exponent < -10) exponent = -10;

                double result = Math.pow(Math.abs(base), exponent);

                // Final safety check
                yield (Double.isInfinite(result) || Double.isNaN(result)) ? 1000.0 : (result > 1000) ? 1000 : result;
            }

            default -> 0.0;
        };
    }

    private double calculateSimilarities(Node baseTree, List<Node> sample) {
        int totalMatchingNodes = 0;
        double totalSimilarity = 0.0;
        for (Node otherTree : sample) {
            totalMatchingNodes += countMatching(baseTree, otherTree);
            double similarity = (double) totalMatchingNodes / Math.max(sizeOf(baseTree), sizeOf(otherTree));
            System.out.println("Similarity with tree: " + similarity);
            totalSimilarity += similarity;
        }
        return totalSimilarity / sample.size(); // Average similarity across the sample
    }

    private int countMatching(Node nodeA, Node nodeB) {
        // start with the root nodes
        int count = 0;
        if (nodeA.value.equals(nodeB.value) && nodeA.type.equals(nodeB.type)) {
            count++;
            
        }
            // Recursively check children
            for (int i = 0; i < Math.min(nodeA.children.size(), nodeB.children.size()); i++) {
                count += countMatching(nodeA.children.get(i), nodeB.children.get(i));
            }
        
        return count;
    }

    private int sizeOf(Node node) {
        int count = 1;
        for (Node child : node.children) {
            count += sizeOf(child);
        }
        return count;
    }
}