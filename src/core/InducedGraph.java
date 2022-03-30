package core;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

/**
 * Induced Graph class.
 * This class generates an induced graph of node labels and their neighbours without regarding directions of connectivity.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class InducedGraph {
    private final Set<InducedGraphNode> inducedGraphNodes = new LinkedHashSet<>();
    private final Set<Node> nodes;

    /**
     * Constructor specifying the nodes to use to generate the induced graph.
     *
     * @param nodes bayesian network nodes
     */
    public InducedGraph(Set<Node> nodes) {
        // add all nodes to the graph
        this.nodes = nodes;
        nodes.forEach(n -> inducedGraphNodes.add(new InducedGraphNode(n.getLabel())));
        // apply relationships
        for (Node node : nodes) {
            InducedGraphNode inducedGraphNode = getInducedGraphNode(node.getLabel());
            List<Node> nodeParents = new ArrayList<>(node.getParents());
            // add children to node as neighbour
            node.getChildren().forEach(x -> inducedGraphNode.addNeighbour(getInducedGraphNode(x.getLabel())));
            //connect parents
            nodeParents.forEach(x -> inducedGraphNode.addNeighbour(getInducedGraphNode(x.getLabel())));
            // connect parents to each other
            for (int i = 0; i < nodeParents.size() - 1; i++) {
                for (int j = i + 1; j < nodeParents.size(); j++) {
                    String currentParentLabel = nodeParents.get(i).getLabel();
                    String otherParentLabel = nodeParents.get(j).getLabel();
                    getInducedGraphNode(currentParentLabel).addNeighbour(getInducedGraphNode(otherParentLabel));
                }
            }
        }
    }

    /**
     * Gets a induced graph node using the label.
     *
     * @param label induced graph node label
     * @return induced graph node
     */
    public InducedGraphNode getInducedGraphNode(String label) {
        return inducedGraphNodes.stream().filter(ign -> ign.getLabel().equalsIgnoreCase(label)).findFirst().orElse(new InducedGraphNode(label));
    }

    /**
     * Connects all neighbors of an induced graph node together.
     *
     * @param label induced graph node label
     * @return copy of induced graph with neighbors of the specified graph node connected
     */
    public InducedGraph connectNeighbors(String label) {
        // remove the node and connect its parents and children
        InducedGraph graph = new InducedGraph(nodes);
        InducedGraphNode labelGraphNode = graph.getInducedGraphNode(label);
        List<InducedGraphNode> neighbors = new ArrayList<>(labelGraphNode.getNeighbours());
        for (int i = 0; i < neighbors.size() - 1; i++) {
            for (int j = i + 1; j < neighbors.size(); j++) {
                InducedGraphNode currentNeighbor = neighbors.get(i);
                InducedGraphNode otherNeighbor = neighbors.get(j);
                if (!currentNeighbor.getNeighbours().contains(otherNeighbor)) {
                    currentNeighbor.addNeighbour(otherNeighbor);
                }
            }
        }
        return graph;
    }
}
