package hr.fer.ztel.hmo.ga.crossover;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.util.Pair;

public class IdentityCrossover implements Crossover {
    @Override
    public Pair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        return new Pair<>(parent1, parent2);
    }
}
