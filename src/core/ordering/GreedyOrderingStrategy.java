package core.ordering;

import core.InducedGraph;
import core.Node;
import core.query.QueryInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Greedy Search Ordering Class.
 * This class is used to automatically generate an order for a bayesian network query using the greedy minimum edges search algorithm
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class GreedyOrderingStrategy extends IntermediateOrderingStrategy {
    /**
     * Gets the order based on the ordering algorithm.
     *
     * @param queryInfo query information
     * @return set of labels
     */
    @Override
    public Set<String> getOrder(QueryInfo queryInfo) {
        InducedGraph graph = getInducedGraph();
        Node queryNode = getNodesList().stream().filter(n -> n.getLabel().equalsIgnoreCase(queryInfo.getLabel())).findFirst().orElse(null);
        if (queryNode != null) {
            Set<String> unmarkedList = getNodesList().stream().map(Node::getLabel).collect(Collectors.toSet());
            Set<String> markedList = new HashSet<>();
            List<String> order = new ArrayList<>();

            for (int i = 0; i < getNodesList().size(); i++) {
                String label = getLabelWithMinimumNumberOfMarkedNeighbours(graph, markedList, unmarkedList);
                order.add(label);
                unmarkedList.remove(label);
                markedList.add(label);
                graph = graph.connectNeighbors(label);
            }
            order.remove(queryNode.getLabel());
            return new LinkedHashSet<>(order);
        }
        return Set.of();
    }

    /**
     * Gets the label with the lowest number of marked neighbours.
     *
     * @param graph      induced graph with connected labels
     * @param markedList marked labels
     * @param labels     searchable labels
     * @return label with min number of marked neighbours
     */
    private String getLabelWithMinimumNumberOfMarkedNeighbours(InducedGraph graph, Set<String> markedList, Set<String> labels) {
        String y = null;
        int maxNoOfNeighbours = Integer.MAX_VALUE;
        List<String> tmpLabels = new ArrayList<>(labels);
        if (markedList.isEmpty()) {
            Collections.shuffle(tmpLabels);
        }
        for (String label : tmpLabels) {
            int unmarkedLabelMarkedNeighboursSize = getNumberOfMarkedNeighbours(graph, markedList, label);
            if (unmarkedLabelMarkedNeighboursSize < maxNoOfNeighbours) {
                maxNoOfNeighbours = unmarkedLabelMarkedNeighboursSize;
                y = label;
            }
        }
        return y;
    }
}
