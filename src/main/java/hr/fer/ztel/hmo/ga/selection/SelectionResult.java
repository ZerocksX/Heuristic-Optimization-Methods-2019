package hr.fer.ztel.hmo.ga.selection;

import hr.fer.ztel.hmo.ga.Chromosome;

import java.util.List;
import java.util.Set;

public class SelectionResult {
    private List<Chromosome> toSelect;
    private List<Chromosome> toDelete;

    public SelectionResult(List<Chromosome> toSelect, List<Chromosome> toDelete) {
        this.toSelect = toSelect;
        this.toDelete = toDelete;
    }

    public List<Chromosome> getToSelect() {
        return toSelect;
    }

    public List<Chromosome> getToDelete() {
        return toDelete;
    }
}
