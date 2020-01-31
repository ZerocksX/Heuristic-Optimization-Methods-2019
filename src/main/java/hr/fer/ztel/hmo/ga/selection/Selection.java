package hr.fer.ztel.hmo.ga.selection;

import hr.fer.ztel.hmo.ga.evauluation.EvaluationFunction;
import hr.fer.ztel.hmo.ga.Population;

public interface Selection {
    SelectionResult select(Population population, EvaluationFunction evaluationFunction);
}
