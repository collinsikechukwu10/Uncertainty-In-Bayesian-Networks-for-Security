package core.ordering;

import core.InducedGraph;
import core.InducedGraphNode;
import core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class IntermediateOrderingStrategy extends BasicOrderingStrategy {

    private InducedGraph inducedGraph;
    private List<Node> nodesList;

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

    public InducedGraph getInducedGraph() {
        return inducedGraph;
    }

    public void setInducedGraph(InducedGraph inducedGraph) {
        this.inducedGraph = inducedGraph;
    }

    public List<Node> getNodesList() {
        return nodesList;
    }

    public void setNodesList(List<Node> nodesList) {
        this.nodesList = nodesList;
        this.inducedGraph = new InducedGraph(new HashSet<>(nodesList));
    }
}
