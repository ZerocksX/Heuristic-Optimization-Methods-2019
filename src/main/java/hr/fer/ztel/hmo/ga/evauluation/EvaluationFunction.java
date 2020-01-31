package hr.fer.ztel.hmo.ga.evauluation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Population;

import java.util.HashMap;

public interface EvaluationFunction {
    HashMap<Chromosome, Double> evaluate(Population population);
}
