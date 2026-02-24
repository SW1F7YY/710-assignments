import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static void main(String[] args){
        // Depth will always start from 1 E.x: Max Depth 1 will only be 1 node aka root node
        System.out.println(YELLOW + "Initializing variables" + RESET);
        final Random RNG = new Random(1);
        int maxGenerations = 1;
        int tournamentSize = 3;
        List<String> terminals = List.of("A", "B", "C", "D", "E", "F", "G");
        PopulationGenerator.FunctionSymbol[] functionSymbols = {
                PopulationGenerator.FunctionSymbol.ADD,
                PopulationGenerator.FunctionSymbol.SUB,
                PopulationGenerator.FunctionSymbol.MUL,
                PopulationGenerator.FunctionSymbol.DIV,
//                PopulationGenerator.FunctionSymbol.SQRT,
//                PopulationGenerator.FunctionSymbol.POW,
//                PopulationGenerator.FunctionSymbol.LOG,
//                PopulationGenerator.FunctionSymbol.LN,
//                PopulationGenerator.FunctionSymbol.LOG2
        };
        StringBuilder terminalString = new StringBuilder();
        for (String terminal: terminals){
            terminalString.append(terminal).append(",");
        }
        StringBuilder functionString = new StringBuilder();
        for (PopulationGenerator.FunctionSymbol symbol: functionSymbols) {
            functionString.append(symbol.label).append(",");
        }
        System.out.println(GREEN + "Sets populated, creating population generator" + RESET);

        Path path = Paths.get("C:\\Users\\Family\\Desktop\\Uni\\COS 710\\Assignment 1/training_data.txt");
        List<String> trainingDataLines = null;
        try{
            trainingDataLines = Files.readAllLines(path);}
        catch (IOException e) {

            System.out.println(RED + "Loading training data failed" + RESET);
            return;
        }
        System.out.println(GREEN + "Training Data loaded" + RESET);

        PopulationGenerator createPopulation = new PopulationGenerator(terminals, functionSymbols, RNG);
        System.out.println(GREEN + "Population Generator created with \nterminals:" + terminalString + "\nfunctions:" + functionString + RESET);

        System.out.println(YELLOW + "Starting initial population generation" + RESET);
        List<Node> population = createPopulation.generate(PopulationGenerator.PopulationMethod.GROW, 3, 4);
        System.out.println(GREEN+ "Initial population has been completed. Size:" + population.size() + RESET);

        // printing initial trees
        for (int i = 0; i < population.size(); i++) {
            System.out.println("Tree " + (i + 1) + ":\n");
            Node root = population.get(i);
            root.printTree("");
        }

        System.out.println(YELLOW + "Starting training" + RESET);
        int currentGeneration = 0;
        while(currentGeneration < maxGenerations) {
            currentGeneration++;
            System.out.println(YELLOW + "Current generation:" + currentGeneration + RESET);
            int treeindex = 0;
            System.out.println(YELLOW + "Starting fitness calculation" + RESET);
            for (Node tree : population) { // 1. Pick a student (Tree)
                treeindex++;
                double totalSquaredError = 0;

                for (int i = 1; i < trainingDataLines.size(); i++) { // 2. Give them the whole test (All Rows)
                    String currentLine = trainingDataLines.get(i);
                    double[] rowValues = Arrays.stream(currentLine.split(","))
                            .mapToDouble(Double::parseDouble)
                            .toArray();

                    // Separate inputs from target
                    double target = rowValues[rowValues.length - 1];
                    FitnessFunction ff = new FitnessFunction();
                    double prediction = ff.calculateFitness(tree, rowValues, terminals);

                    // 3. Accumulate Error
                    double error = prediction - target;
                    totalSquaredError += (error * error);
                }

                // 4. Calculate and store MSE
                double mse = totalSquaredError / (trainingDataLines.size() - 1);
                tree.fitness = mse;

                System.out.println("Tree " + treeindex +" MSE: " + mse);
            }
            System.out.println(GREEN + "Fitness generation completed" + RESET);

            System.out.println(YELLOW + "Starting selection" + RESET);
            Selection tournamentSelection = new Selection(population, RNG, tournamentSize);
            List<Node> newPopulation = tournamentSelection.performTournamentSelection();
            System.out.println(GREEN + "Selection finished" + RESET);

            for (int i = 0; i < newPopulation.size(); i++){
                System.out.println("\nNew population tree " + (i + 1) + ":");
                newPopulation.get(i).printTree("");
            }
            System.out.println(YELLOW + "Starting crossover" + RESET);
            
            GenericOperators gp = new GenericOperators("classic", newPopulation);
            newPopulation = gp.classicCrossover();
            System.out.println(GREEN + "Generic operations passed" + RESET);
            for (int i = 0; i < newPopulation.size(); i++){
                System.out.println("\nNew population tree " + (i + 1) + ":");
                newPopulation.get(i).printTree("");
            }

            // TODO: implement genetic operators (crossover currently)


        }

//        System.out.println("Starting fitness function");
//        FitnessFunction fitnessFunction = new FitnessFunction();
//        for( int i = 0; i< population.size(); i++){
//            // calculate the fitness for each member of the population
//            System.out.println("Current fitness for population member " + i + ": " + fitnessFunction.calculateFitness(population.get(i)));
//        }
//        System.out.println("Finishes raw fitness calculations");
    }
}