package hr.fer.ztel.hmo.ga.mutation;

import hr.fer.ztel.hmo.ga.Chromosome;

public class IdentityMutation implements Mutation  {
    @Override
    public Chromosome mutate(Chromosome original) {
        return original;
    }
}
