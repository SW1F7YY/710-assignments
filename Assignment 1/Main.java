import java.util.List;

public class Main {
    public static void main(String[] args){
        // Depth will always start from 1 E.x: Max Depth 1 will only be 1 node aka root node

        String[] terminals = {"1","2"};
        PopulationGenerator.FunctionSymbol[] functionSymbols = {PopulationGenerator.FunctionSymbol.ADD, PopulationGenerator.FunctionSymbol.SUB};
        StringBuilder terminalString = new StringBuilder();
        for (String terminal: terminals){
            terminalString.append(terminal).append(",");
        }
        StringBuilder functionString = new StringBuilder();
        for (PopulationGenerator.FunctionSymbol symbol: functionSymbols) {
            functionString.append(symbol.label).append(",");
        }
        System.out.println("Sets populated, creating population generator");

        PopulationGenerator createPopulation = new PopulationGenerator(terminals, functionSymbols);
        System.out.println("Population Generator created with \nterminals:" + terminalString + "\nfunctions:" + functionString);

        List<Node> population = createPopulation.generate(PopulationGenerator.PopulationMethod.GROW, 4, 4);
        System.out.println("Initial population has been completed. Size:" + population.size());

        // printing initial trees
        for (int i = 0; i < population.size(); i++) {
            System.out.println("Tree " + (i + 1) + ":\n");
            Node root = population.get(i);
            root.printTree("");
        }
    }
}