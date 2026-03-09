import java.util.List;

public class FitnessFunction {

    public double calculateFitness(Node rootNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders) {
        // Handle a single-node tree (just a terminal)
        if (rootNode.type.equals("terminal")) {
            return getTerminalValue(rootNode, trainingDataTerminalValues, terminalPlaceholders);
        }
        return evaluate(rootNode, trainingDataTerminalValues, terminalPlaceholders);
    }

    private double evaluate(Node currentNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders) {
        // Base Case: If it's a terminal, look up its value
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
}