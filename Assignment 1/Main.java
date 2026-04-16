import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    public static final int maxDepth = 6; // best is 6: Tree 0 | MSE: 4.855578254196218E-4
    public static final double complexityPenalty = 0.00001;
    public static final int popSize = 200; // best is 200
    public static final int elitismCount = 10; // best is 10
    public static void main(String[] args){
        long startTime = System.nanoTime();


        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Generation,BestMSE,AverageMSE\n");
        // Depth will always start from 1 E.x: Max Depth 1 will only be 1 node aka root node
        System.out.println(YELLOW + "Initializing variables" + RESET);
        long seed = (args.length > 0) ? Long.parseLong(args[0]) : 10L;
        System.out.println(YELLOW + "Using seed: " + seed + RESET);
        final Random RNG = new Random(seed);
        int maxGenerations = 12;
        int tournamentSize = 4;
        List<String> terminals = List.of("L1","L2","L3","L4");
        int windowSize = terminals.size();
        PopulationGenerator.FunctionSymbol[] functionSymbols = {
                PopulationGenerator.FunctionSymbol.ADD,
                PopulationGenerator.FunctionSymbol.SUB,
                PopulationGenerator.FunctionSymbol.MUL,
                PopulationGenerator.FunctionSymbol.DIV,
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

        // Load from classpath — works in IDE and when packaged as a JAR
        List<String> trainingDataLines;
        try (InputStream is = Main.class.getResourceAsStream("/Data_set.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            trainingDataLines = reader.lines().collect(Collectors.toList());
        } catch (IOException | NullPointerException e) {
            System.out.println(RED + "Loading training data failed — Data_set.csv not found on classpath" + RESET);
            return;
        }
        System.out.println(GREEN + "Training Data loaded" + RESET);
        int loadColumnIndex = 1;
        double[] allLoads = trainingDataLines.stream()
                .skip(1) // Skip the CSV header
                .map(line -> line.split(","))
                .mapToDouble(parts -> Double.parseDouble(parts[loadColumnIndex].trim()))
                .toArray();

        int percentageOfData = (int) (allLoads.length * 0.8);
        double[] trainingSet = new double[percentageOfData];
        System.arraycopy(allLoads, 0, trainingSet, 0, percentageOfData);


        // ! Creating Population
        PopulationGenerator createPopulation = new PopulationGenerator(terminals, functionSymbols, RNG);
        System.out.println(GREEN + "Population Generator created with \nterminals:" + terminalString + "\nfunctions:" + functionString + RESET);

        System.out.println(YELLOW + "Starting initial population generation" + RESET);
        List<Node> population = createPopulation.generate(PopulationGenerator.PopulationMethod.GROW, popSize, maxDepth);
        System.out.println(GREEN+ "Initial population has been completed. Size:" + population.size() + RESET);

        // printing initial trees
//        for (int i = 0; i < population.size(); i++) {
//            System.out.println("Tree " + (i + 1) + ":\n");
//            Node root = population.get(i);
//            root.printTree("");
//        }
        // ! Starting training
        System.out.println(YELLOW + "Starting training" + RESET);
        int currentGeneration = 0;
        int treeindex;
        double bestMse;
        double totalMse;
        while(currentGeneration < maxGenerations) {
            treeindex = 0;
            currentGeneration++;
            System.out.println(YELLOW + "Current generation:" + currentGeneration + RESET);
            treeindex++;
            double[] rowValues = new double[windowSize];

            // ! Fitness function
            System.out.println(YELLOW + "Starting fitness calculation" + RESET);
            FitnessFunction ff = new FitnessFunction(); // Move OUTSIDE the loops
            bestMse = Double.MAX_VALUE;
            totalMse = 0;
            for (Node tree : population) {
                treeindex++;
                double totalSquaredError = 0;
                int evalCount = 0;

                for (int i = windowSize; i < trainingSet.length; i++) {
                    double target = trainingSet[i];

                    // Populate rowValues with the exact number of lags the GP expects
                    for (int j = 0; j < windowSize; j++) {
                        rowValues[j] = trainingSet[i - (j + 1)];
                    }

                    // Pass the single row of data to the tree evaluator
                    double prediction = ff.calculateFitness(tree, rowValues, terminals, complexityPenalty);

                    double error = prediction - target;
                    totalSquaredError += (error * error);
                    evalCount++;
                }
//                totalMse += totalSquaredError / evalCount;
//                if ((totalSquaredError / evalCount) < bestMse){
//                    bestMse = totalSquaredError / evalCount;
//                }

                System.out.println("Tree " + treeindex + " | MSE: " + totalSquaredError/evalCount);
                tree.fitness = totalSquaredError / evalCount;
            }
            System.out.println(GREEN + "Fitness generation completed" + RESET);
            // ! Export data to csv
            for (Node tree : population) {
                if (tree.fitness < bestMse) {
                    bestMse = tree.fitness;
                }
                totalMse += tree.fitness;
            }
            double averageMse = totalMse / percentageOfData;
            csvContent.append(currentGeneration)
                    .append(",")
                    .append(bestMse)
                    .append(",")
                    .append(averageMse)
                    .append("\n");

            // ! Selection. Elitism first, then tournament

            System.out.println(YELLOW + "Starting selection" + RESET);
            // 1. Sort and pick elites
            population.sort(Comparator.comparingDouble(n -> n.fitness));
            List<Node> newPopulation = new ArrayList<>();
            for (int i = 0; i < elitismCount; i++) {
                newPopulation.add(population.get(i).copy());
            }

            // 2. Fill the REST of the population with tournament winners
            Selection tournamentSelection = new Selection(population, RNG, tournamentSize);
            // Pass the count of how many more you need
            List<Node> winners = tournamentSelection.performTournamentSelection(population.size() - elitismCount);
            newPopulation.addAll(winners);

            population = newPopulation;
            System.out.println(GREEN + "Selection finished" + RESET);

//            for (int i = 0; i < newPopulation.size(); i++){
//                System.out.println("\nNew population tree " + (i + 1) + ":");
//                newPopulation.get(i).printTree("");
//            }

               // * continue from where elitisim left off in new population and apply crossover/mutation to the rest of the population
            newPopulation = new ArrayList<>(population.subList(0, elitismCount)); // Start with elites
            for (int i = elitismCount; i < population.size(); i += 2) {
                double randomValue = RNG.nextDouble(1);
                if (randomValue < 0.7 && i + 1 < population.size()) {
                    // ! Crossover
                    System.out.println(YELLOW + "Starting crossover" + RESET);
                    GenericOperators gp = new GenericOperators(RNG, maxDepth);
                    newPopulation.addAll(gp.classicCrossover(population.get(i), population.get(i + 1)));
                    System.out.println(GREEN + "Generic operations passed" + RESET);
                } else if ( randomValue < 0.85) {
                    // mutate 
                    System.out.println(YELLOW + "Starting mutation" + RESET);
                    Mutation mutation = new Mutation(maxDepth, RNG, terminals, functionSymbols);
                    newPopulation.add(mutation.mutate(population.get(i)));
                } else {
                    // just copy the rest to maintain diversity and avoid overfitting to crossover/mutation
                    newPopulation.add(population.get(i).copy());
                }
            }


            // should crossover or mutation be applied?
    //         if (RNG.nextDouble(1) < 0.7) {
    //         // ! Crossover
    //             System.out.println(YELLOW + "Starting crossover" + RESET);
    //             GenericOperators gp = new GenericOperators(population, RNG, maxDepth);
    //             newPopulation = gp.classicCrossover();
    //             System.out.println(GREEN + "Generic operations passed" + RESET);
    // //            for (int i = 0; i < newPopulation.size(); i++){
    // //                System.out.println("\nNew population tree " + (i + 1) + ":");
    // //                newPopulation.get(i).printTree("");
    // //            }
    //             population = newPopulation;
    //         } else {
    //         // ! Mutation
    //             System.out.println(YELLOW + "Starting mutation" + RESET);
    //             Mutation mutation = new Mutation(population, maxDepth, RNG, terminals, functionSymbols);
    //             newPopulation = mutation.mutate();
    //             System.out.println(GREEN + "Mutation completed" + RESET);
    // //            for (int i = 0; i < newPopulation.size(); i++){
    // //                System.out.println("\nNew population tree " + (i + 1) + ":");
    // //                newPopulation.get(i).printTree("");
    // //            }
    
    //             population = newPopulation;
    //         }
            // ! prune to keep max depth
            System.out.println(YELLOW + "Pruning the population" + RESET);
            Prune pruner = new Prune(population, maxDepth, terminals, RNG);
            if (RNG.nextDouble(1) < 0.4) newPopulation = pruner.prune();
            System.out.println(GREEN + "Pruning complete" + RESET);
//          for (int i = 0; i < newPopulation.size(); i++){
//                System.out.println("\nNew population tree " + (i + 1) + ":");
//                newPopulation.get(i).printTree("");
//            }
            population = newPopulation;



        }
        // Re-evaluate fitness on the final population so values are not 0.0
//        System.out.println(YELLOW + "Re-evaluating fitness on final population" + RESET);
//        FitnessFunction ffFinal = new FitnessFunction();
//        double[] rowValuesFinal = new double[windowSize];
//        for (Node tree : population) {
//            double totalSquaredErrorFinal = 0;
//            int evalCountFinal = 0;
//            for (int i = windowSize; i < trainingSet.length; i++) {
//                double target = trainingSet[i];
//                for (int j = 0; j < windowSize; j++) {
//                    rowValuesFinal[j] = trainingSet[i - (j + 1)];
//                }
//                double prediction = ffFinal.calculateFitness(tree, rowValuesFinal, terminals);
//                double error = prediction - target;
//                totalSquaredErrorFinal += (error * error);
//                evalCountFinal++;
//            }
//            tree.fitness = totalSquaredErrorFinal / evalCountFinal;
//        }

        System.out.println("\n=== TOP 3 TREES IN FINAL POPULATION ===");
        population.stream()
                .sorted(Comparator.comparingDouble(t -> t.fitness))
                .limit(3)
                .forEach(t -> {
                    System.out.println("MSE: " + t.fitness);
                    t.printTree("");
                });


        System.out.println(YELLOW + "Testing population on latest set" + RESET);
        treeindex = 0;
        double[] rowValues = new double[windowSize];
        FitnessFunction ff = new FitnessFunction(); // Move OUTSIDE the loops
        Node bestTree = null;
        for (Node tree: population) {
            if (bestTree == null){
                bestTree = tree;
            }
            else if (tree.fitness < bestTree.fitness) {
                bestTree = tree;
            }
        }
        assert bestTree != null;
        double totalSquaredError = 0;
        int evalCount = 0;

        for (int i = percentageOfData - 4; i < allLoads.length; i++) {
            double target = allLoads[i];

            // Populate rowValues with the exact number of lags the GP expects
            for (int j = 0; j < windowSize; j++) {
                rowValues[j] = allLoads[i - (j + 1)];
            }

            // Pass the single row of data to the tree evaluator

            double prediction = ff.calculateFitness(bestTree, rowValues, terminals, complexityPenalty);

            double error = prediction - target;
            totalSquaredError += (error * error);
            evalCount++;
        }
        System.out.println("Tree " + treeindex + " | MSE: " + totalSquaredError/evalCount);
        bestTree.fitness = totalSquaredError / evalCount;


//        System.out.println("Starting fitness function");
//        FitnessFunction fitnessFunction = new FitnessFunction();
//        for( int i = 0; i< population.size(); i++){
//            // calculate the fitness for each member of the population
//            System.out.println("Current fitness for population member " + i + ": " + fitnessFunction.calculateFitness(population.get(i)));
//        }
//        System.out.println("Finishes raw fitness calculations");
        long endTime = System.nanoTime();
        long durationInNanoseconds = (endTime - startTime);

// Convert to seconds for your report
        double durationInSeconds = (double) durationInNanoseconds / 1_000_000_000;
        System.out.println("Total Execution Time: " + durationInSeconds + " seconds");

        try {
            Files.writeString(Path.of("evolution_results.csv"), csvContent.toString());
            System.out.println(GREEN + "CSV file saved successfully!" + RESET);
        } catch (IOException e) {
            System.out.println(RED + "Failed to save CSV." + RESET);
        }
    }

}