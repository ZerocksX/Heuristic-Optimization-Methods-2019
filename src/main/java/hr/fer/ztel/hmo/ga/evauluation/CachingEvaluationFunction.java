package hr.fer.ztel.hmo.ga.evauluation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.MaxSizeHashMap;
import hr.fer.ztel.hmo.ga.Population;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CachingEvaluationFunction implements EvaluationFunction {
    private MaxSizeHashMap<Chromosome, Double> results;
    private int callCount;
    private EvaluationFunction evaluationFunction;

    public CachingEvaluationFunction(EvaluationFunction evaluationFunction) {
        this.evaluationFunction = evaluationFunction;
        results = new MaxSizeHashMap<Chromosome, Double>(10_000);
        callCount = 0;
    }

    @Override
    public HashMap<Chromosome, Double> evaluate(Population population) {
        List<Chromosome> chromosomeList = new LinkedList<>();
        for (Chromosome c : population.getChromosomes()) {
            if (!results.containsKey(c)) {
                chromosomeList.add(c);
                callCount++;
            }
        }
        Population pop = new Population(chromosomeList);
        HashMap<Chromosome, Double> evals = evaluationFunction.evaluate(pop);
        results.putAll(evals);
        HashMap<Chromosome, Double> scores = new HashMap<>();
        for (Chromosome chromosome : population.getChromosomes()) {
            scores.put(chromosome, results.get(chromosome));
        }
        return scores;
    }


    public int getCalls() {
        return callCount;
    }


}
