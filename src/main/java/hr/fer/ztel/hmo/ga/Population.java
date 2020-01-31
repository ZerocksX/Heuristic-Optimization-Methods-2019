package hr.fer.ztel.hmo.ga;

import hr.fer.ztel.hmo.ga.initialization.ChromosomeInitializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Population {
    private List<Chromosome> chromosomes;
    private ChromosomeInitializer chromosomeInitializer;

    public Population(int size, ChromosomeInitializer chromosomeInitializer) {
        this.chromosomes = new ArrayList<>(size);
        this.chromosomeInitializer = chromosomeInitializer;
        for (int i = 0; i < size; i++) {
            chromosomes.add(this.chromosomeInitializer.initialize());
        }
    }


    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public int getSize() {
        return chromosomes.size();
    }

    public ChromosomeInitializer getChromosomeInitializer() {
        return chromosomeInitializer;
    }

    public void reinitializeChromosomes(double percentage, Chromosome best) {
        for (int i = 0; i < getSize(); i++) {
            if(this.chromosomes.get(i).equals(best)){
                continue;
            }
            if (ThreadLocalRandom.current().nextDouble() < percentage) {
                this.chromosomes.set(i, getChromosomeInitializer().initialize());
            }
        }
    }
}
