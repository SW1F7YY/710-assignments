import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Selection {
    // for this we will be using tournament selection
    private final List<Node> population;
    private final Random rng;
    private final int tournamentSize;

    public Selection(List<Node> population, Random rng, int tournamentSize){
        this.rng = rng;
        this.population = population;
        this.tournamentSize = tournamentSize;
    }

    public List<Node> performTournamentSelection() {
        List<Node> newPopulation = new ArrayList<>();
        for(int i = 0; i < population.size(); i++){
            Node minFitnessNode = null;
            for (int j = 0; j < tournamentSize; j++){
                int randomSelectionIndex = rng.nextInt(population.size());
                if (minFitnessNode == null){
                    minFitnessNode = population.get(randomSelectionIndex);
                } else if (minFitnessNode.fitness > population.get(randomSelectionIndex).fitness) {
                    minFitnessNode = population.get(randomSelectionIndex);
                }
            }
            newPopulation.add(minFitnessNode);
        }
        return newPopulation;
    }
}