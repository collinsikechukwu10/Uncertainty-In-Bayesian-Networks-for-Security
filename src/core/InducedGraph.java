package core;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;


public class InducedGraph {
    private final Set<InducedGraphNode> inducedGraphNodes = new LinkedHashSet<>();

    public InducedGraph(Set<Node> nodes) {
        // add all nodes to the graph
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

    public InducedGraphNode getInducedGraphNode(String label) {
        return inducedGraphNodes.stream().filter(ign -> ign.getLabel().equalsIgnoreCase(label)).findFirst().orElse(new InducedGraphNode(label));
    }
}
