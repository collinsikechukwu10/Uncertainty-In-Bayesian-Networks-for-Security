package core.ordering;

import core.InducedGraph;
import core.InducedGraphNode;
import core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Intermediate Ordering Class.
 * This class is the base class for all automatic order generation for bayesian network queries.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public abstract class IntermediateOrderingStrategy extends BasicOrderingStrategy {

    private InducedGraph inducedGraph;
    private List<Node> nodesList;
    public Random random = new Random(123);

    /**
     * Gets the number of marked neighbours for an induced graph node.
     * @param graph induced graph node
     * @param markedList list of marked induced graph nodes as their labels
     * @param label label to examine
     * @return number of marked neighbours for the induced graph node
     */
    public int getNumberOfMarkedNeighbours(InducedGraph graph, Set<String> markedList, String label) {
        // get induced node and count how many neighbours have their label in the marked list
        int count = 0;
        Set<InducedGraphNode> neighbours = graph.getInducedGraphNode(label).getNeighbours();
        for (InducedGraphNode neighbour : neighbours) {
            if (markedList.contains(neighbour.getLabel())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the induced graph
     *
     * @return induced graph
     */
    public InducedGraph getInducedGraph() {
        return inducedGraph;
    }

    /**
     * Gets nodes used to generate the induced graph.
     *
     * @return list of nodes
     */
    public List<Node> getNodesList() {
        return nodesList;
    }

    /**
     * Sets the list of nodes used to generate an induced graph.
     *
     * @param nodesList list of nodes
     */
    public void setNodesList(List<Node> nodesList) {
        this.nodesList = nodesList;
        this.inducedGraph = new InducedGraph(new HashSet<>(nodesList));
    }

    public Random getRandom() {
        return random;
    }
}
