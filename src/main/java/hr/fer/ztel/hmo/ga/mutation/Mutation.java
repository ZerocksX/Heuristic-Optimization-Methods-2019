package hr.fer.ztel.hmo.ga.mutation;

import hr.fer.ztel.hmo.ga.Chromosome;

public interface Mutation {
    Chromosome mutate(Chromosome original);
}
