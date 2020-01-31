package hr.fer.ztel.hmo.ga.selection;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.ga.evauluation.EvaluationFunction;
import hr.fer.ztel.hmo.ga.Population;

import java.util.*;

public class TournamentSelection implements Selection {

    private static final Random random = new Random();

    private int k;

    public TournamentSelection(int k) {
        this.k = k;
    }

    @Override
    public SelectionResult select(Population population, EvaluationFunction evaluationFunction) {
        HashMap<Chromosome, Double> scores = evaluationFunction.evaluate(population);
        List<Chromosome> chromosomes = new ArrayList<>(population.getChromosomes());
        chromosomes.sort(Comparator.<Chromosome>comparingDouble(scores::get).reversed());
        List<Chromosome> picked = Utils.chooseKFromList(chromosomes, scores, this.k);
        int n = picked.size();
        return new SelectionResult(
                Arrays.asList(picked.get(0), picked.get(1)),
                Arrays.asList(picked.get(n - 1), picked.get(n - 2))
        );
    }


}
