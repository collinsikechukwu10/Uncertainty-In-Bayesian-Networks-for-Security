package core;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Induced Graph Node class.
 * This class generates an induced graph node which is a label and a list of neighbours.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class InducedGraphNode {
    private final Set<InducedGraphNode> neighbours = new LinkedHashSet<>();
    private final String label;

    /**
     * Constructor specifying the label
     *
     * @param label label for the node
     */
    public InducedGraphNode(String label) {
        this.label = label;
    }

    /**
     * Gets the label of the induced graph node
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Adds a neighbor to the induced graph node.
     *
     * @param inducedGraphNode neighbour to add
     */
    public void addNeighbour(InducedGraphNode inducedGraphNode) {
        this.neighbours.add(inducedGraphNode);
        inducedGraphNode.neighbours.add(this);
    }

    /**
     * Gets the neighbors of the induced graph node.
     *
     * @return neighbors
     */
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


