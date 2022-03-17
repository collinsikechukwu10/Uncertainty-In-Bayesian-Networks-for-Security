package core;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class InducedGraphNode {
    private final Set<InducedGraphNode> neighbours = new LinkedHashSet<>();
    private final String label;

    InducedGraphNode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    void addNeighbour(InducedGraphNode inducedGraphNode) {
        this.neighbours.add(inducedGraphNode);
        inducedGraphNode.neighbours.add(this);
    }

    public Set<InducedGraphNode> getNeighbours() {
        return neighbours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InducedGraphNode that = (InducedGraphNode) o;
        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}


