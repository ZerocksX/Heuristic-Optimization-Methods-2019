package hr.fer.ztel.hmo.ga;

import hr.fer.ztel.hmo.ga.crossover.Crossover;
import hr.fer.ztel.hmo.ga.crossover.IdentityCrossover;
import hr.fer.ztel.hmo.ga.evauluation.*;
import hr.fer.ztel.hmo.ga.initialization.RandomInitializer;
import hr.fer.ztel.hmo.ga.mutation.Mutation;
import hr.fer.ztel.hmo.ga.mutation.RuinAndRecreateMutation;
import hr.fer.ztel.hmo.ga.selection.Selection;
import hr.fer.ztel.hmo.ga.selection.SelectionResult;
import hr.fer.ztel.hmo.ga.selection.TournamentSelection;
import hr.fer.ztel.hmo.parser.Instance;
import hr.fer.ztel.hmo.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithm {
    private Population population;
    private List<Crossover> crossovers;
    private List<Mutation> mutations;
    private Selection selection;
    private Instance instance;
    private EvaluationFunction evaluationFunction;
    private static final RouteEvaluator routeEvaluator = new RouteEvaluator();
    private int round = 0;
    private double bestScoreRepeated = 0;
    private static final int bestScoreRepeatedLimit = 2000;
    private Chromosome best;
    private double bestScore;

    public GeneticAlgorithm(Instance instance) {
        this(
                new Population(10, new RandomInitializer(0, 0, instance, routeEvaluator)),
                Arrays.asList(new IdentityCrossover()),
                Arrays.asList(
//                        new RuinAndRecreateMutation(instance, true, true),
//                        new RuinAndRecreateMutation(instance, true, false),
//                        new RuinAndRecreateMutation(instance, false, true),
//                        new RuinAndRecreateMutation(instance, false, false),
                        new RuinAndRecreateMutation(instance, true)
                ),
                new TournamentSelection(5),
                instance,
                new NormalizingEvaluationFunction(new CachingEvaluationFunction(new EvaluateBySizeThenDistance(routeEvaluator, instance)))
        );
    }

    public GeneticAlgorithm(Population population, List<Crossover> crossovers, List<Mutation> mutations, Selection selection, Instance instance, EvaluationFunction evaluationFunction) {
        this.population = population;
        this.crossovers = crossovers;
        this.mutations = mutations;
        this.selection = selection;
        this.instance = instance;
        this.evaluationFunction = evaluationFunction;
    }

    public void iterate(EvaluationFunction scoreFunction) {
        round++;
        HashMap<Chromosome, Double> evaluation = scoreFunction.evaluate(this.getPopulation());
        Chromosome possibleBest = evaluation.keySet().stream().min(Comparator.comparingDouble(evaluation::get)).get();
        if (best == null || evaluation.get(possibleBest) < bestScore) {
            best = possibleBest;
            bestScore = evaluation.get(possibleBest);
        }

        if (bestScoreRepeated >= bestScoreRepeatedLimit) {
            this.population.reinitializeChromosomes(0.4, null);
            bestScoreRepeated = 0;
            System.out.println("Reset half chromosomes");
        }


        HashMap<Chromosome, Double> evaluationBefore = scoreFunction.evaluate(this.getPopulation());
        Chromosome bestBefore = evaluationBefore.keySet().stream().min(Comparator.comparingDouble(evaluationBefore::get)).get();

        SelectionResult selectionResult = this.selection.select(this.population, this.evaluationFunction);
        Chromosome par1 = selectionResult.getToSelect().get(0);
        Chromosome par2 = selectionResult.getToSelect().get(1);
        Chromosome toDel1 = selectionResult.getToDelete().get(selectionResult.getToDelete().size()-1);
        Chromosome toDel2 = selectionResult.getToDelete().get(selectionResult.getToDelete().size()-2);
        Pair<Chromosome, Chromosome> children = this.crossovers.get(ThreadLocalRandom.current().nextInt(this.crossovers.size())).crossover(par1, par2);


        Chromosome child1 = this.mutations.get(ThreadLocalRandom.current().nextInt(this.mutations.size())).mutate(children.getFirst());
        Chromosome child2 = this.mutations.get(ThreadLocalRandom.current().nextInt(this.mutations.size())).mutate(children.getSecond());
        this.population.getChromosomes().remove(toDel1);
        this.population.getChromosomes().remove(toDel2);
        this.population.getChromosomes().add(child1);
        this.population.getChromosomes().add(child2);

        HashMap<Chromosome, Double> evaluationAfter = scoreFunction.evaluate(this.getPopulation());
        Chromosome bestAfter = evaluationAfter.keySet().stream().min(Comparator.comparingDouble(evaluationAfter::get)).get();
        if (evaluationAfter.get(bestAfter) >= evaluationBefore.get(bestBefore)) {
            bestScoreRepeated++;
        } else {
            bestScoreRepeated = 0;
        }

    }

    public Population getPopulation() {
        return population;
    }

    public List<Crossover> getCrossovers() {
        return crossovers;
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public Selection getSelection() {
        return selection;
    }

    public Instance getInstance() {
        return instance;
    }

    public EvaluationFunction getEvaluationFunction() {
        return evaluationFunction;
    }

    public static RouteEvaluator getRouteEvaluator() {
        return routeEvaluator;
    }

    public Chromosome getBest() {
        return best;
    }

    public double getBestScore() {
        return bestScore - best.getVehicleNumber() * EvaluateBySizeThenDistance.VEHICLE_SIZE_OFFSET;
    }
}
