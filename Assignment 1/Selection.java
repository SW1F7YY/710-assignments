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

    public List<Node> performTournamentSelection(int countToSelect) {
        List<Node> winners = new ArrayList<>();
        for(int i = 0; i < countToSelect; i++) {
            Node bestInTournament = null;
            for (int j = 0; j < tournamentSize; j++) {
                Node candidate = population.get(rng.nextInt(population.size()));
                if (bestInTournament == null || Double.compare(candidate.fitness, bestInTournament.fitness) < 0) {
                    bestInTournament = candidate;
                }
            }
            winners.add(bestInTournament.copy());
        }
        return winners;
    }
}