package hr.fer.ztel.hmo.ga.initialization;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.List;

public interface ChromosomeInitializer {
    Chromosome initialize();
}
