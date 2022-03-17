package core.ordering;

import core.BayesianNetwork;
import core.InducedGraph;
import core.NetworkGenerator;
import core.Node;
import core.query.QueryInfo;
import core.query.QueryResult;

import java.util.*;
import java.util.stream.Collectors;

public class MaxCardinalitySearchOrderingStrategy extends IntermediateOrderingStrategy {

    private String getLabelWithMaxNumberOfMarkedNeighbours(InducedGraph graph, Set<String> markedList, Set<String> labels) {
        String y = null;
        int maxNoOfNeighbours = -1;
        for (String label : labels) {
            int unmarkedLabelMarkedNeighboursSize = getNumberOfMarkedNeighbours(graph, markedList, label);
            if (unmarkedLabelMarkedNeighboursSize > maxNoOfNeighbours) {
                maxNoOfNeighbours = unmarkedLabelMarkedNeighboursSize;
                y = label;
            }
        }
        return y;
    }

    @Override
    public Set<String> getOrder(QueryInfo queryInfo) {
        Node queryNode = getNodesList().stream().filter(n -> n.getLabel().equalsIgnoreCase(queryInfo.getLabel())).findFirst().orElse(null);
        if (queryNode != null) {
            Set<String> unmarkedList = getNodesList().stream().map(Node::getLabel).collect(Collectors.toSet());
            Set<String> markedList = new HashSet<>();
            List<String> order = new ArrayList<>();

            for (int i = 0; i < getNodesList().size(); i++) {
                String label = getLabelWithMaxNumberOfMarkedNeighbours(getInducedGraph(), markedList, unmarkedList);
                order.add(label);
                unmarkedList.remove(label);
                markedList.add(label);
            }
            // reverse order and then remove query node
            Collections.reverse(order);
            order.remove(queryNode.getLabel());
            return new LinkedHashSet<>(order);
        }
        return Set.of();
    }

//    public static void main(String[] args) {
//        BayesianNetwork n = NetworkGenerator.buildNetwork("BNB");
//        n.setOrdering(new MaxCardinalitySearchOrderingStrategy());
//        QueryResult f = n.query(new QueryInfo("N",true));
//        System.out.println(Arrays.toString(f.getOrder()));
//    }
}
