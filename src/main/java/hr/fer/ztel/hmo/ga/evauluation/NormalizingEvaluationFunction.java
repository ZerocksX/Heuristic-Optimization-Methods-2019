package hr.fer.ztel.hmo.ga.evauluation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Population;
import hr.fer.ztel.hmo.ga.Utils;

import java.util.HashMap;

public class NormalizingEvaluationFunction implements EvaluationFunction {
    private EvaluationFunction evaluationFunction;

    public NormalizingEvaluationFunction(EvaluationFunction evaluationFunction) {
        this.evaluationFunction = evaluationFunction;
    }

    @Override
    public HashMap<Chromosome, Double> evaluate(Population population) {
        HashMap<Chromosome, Double> scores = new HashMap<>(evaluationFunction.evaluate(population));
        Utils.normalizeScores(scores, true);
        return scores;
    }
}
