import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericOperators {
    private final List<Node> population;
    private final Random rng;
    private final int maxDepth;

    GenericOperators(List<Node> population, Random rng, int maxDepth){
        this.population = population;
        this.rng = rng;
        this.maxDepth = maxDepth;
    }

    public List<Node> classicCrossover() {
        List<Node> newPopulation = new ArrayList<>();

        for (int i = 0; i < population.size(); i += 2) {
            if (i + 1 <population.size()) {
                Node childA = population.get(i).copy();
                Node childB = population.get(i + 1).copy();

                if (rng.nextDouble() < 0.85) {

                    Node parentNodeA = childA;
                    Node targetA = childA;
                    int lastIndexA = 0;
                    int depthA = rng.nextInt(1, maxDepth);

                    for (int j = 0; j < depthA; j++) {
                        if (targetA.children.isEmpty()) break;


                        lastIndexA = rng.nextInt(targetA.children.size());
                        parentNodeA = targetA;
                        targetA = targetA.children.get(lastIndexA);
                    }

                    Node parentNodeB = childB;
                    Node targetB = childB;
                    int lastIndexB = 0;
                    int depthB = rng.nextInt(1, maxDepth);

                    for (int j = 0; j < depthB; j++) {
                        if (targetB.children.isEmpty()) break;
                        lastIndexB = rng.nextInt(targetB.children.size());
                        parentNodeB = targetB;
                        targetB = targetB.children.get(lastIndexB);
                    }


                    Node graftForB = targetA.copy();
                    Node graftForA = targetB.copy();

                    parentNodeA.children.set(lastIndexA, graftForA);
                    parentNodeB.children.set(lastIndexB, graftForB);
                }


                newPopulation.add(childA);
                newPopulation.add(childB);
            } else {
                newPopulation.add(population.get(i).copy());
            }
        }
        return newPopulation;
    }

    public List<Node> internalCrossover() {
        return null;
    }
}
