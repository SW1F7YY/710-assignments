import java.io.IOError;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FitnessFunction {
    public double calculateFitness(Node rootNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders){
        if (rootNode.type.equals("terminal")) return Double.parseDouble(rootNode.value);
        return calculateFitnessHelper(rootNode,trainingDataTerminalValues, terminalPlaceholders);
    };

    private double calculateFitnessHelper(Node currentNode, double[] trainingDataTerminalValues, List<String> terminalPlaceholders) {
        // check if it's a terminal
        if (currentNode.type.equals("terminal")){
            return trainingDataTerminalValues[terminalPlaceholders.indexOf(currentNode.value)];
        };

        // create mock data
        List<String> emptyString = List.of();
        PopulationGenerator.FunctionSymbol[] emptyArray = {};
        PopulationGenerator arityFinder = new PopulationGenerator(emptyString, emptyArray,new Random());
        // find the arity
        PopulationGenerator.FunctionSymbol currentSymbol = arityFinder.getFunctionSymbol(currentNode.value);
        int currentSymbolArity = arityFinder.getFunctionSymbol(currentNode.value).arity;
        if (currentSymbolArity != currentNode.children.size()) throw new IOError(new Throwable("Children size and arity doesn't match"));

        // get the values for the children
        double[] childResults = new double[currentNode.children.size()];
        for ( int i = 0; i < currentNode.children.size(); i++){
            childResults[i] = calculateFitnessHelper(currentNode.children.get(i), trainingDataTerminalValues, terminalPlaceholders);
        }
        return currentSymbol.apply(childResults);
    }
}
