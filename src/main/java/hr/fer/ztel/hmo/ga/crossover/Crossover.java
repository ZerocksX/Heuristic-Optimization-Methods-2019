package hr.fer.ztel.hmo.ga.crossover;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.util.Pair;

public interface Crossover {
    Pair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2);
}
