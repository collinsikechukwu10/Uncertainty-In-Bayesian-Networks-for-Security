package core;

import core.ordering.OrderingAlgorithm;
import core.ordering.OrderingAlgorithm1;
import core.ordering.OrderingAlgorithm2;
import core.ordering.ProvidedOrderingAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bayesian Network Class.
 * This class is used to create a bayesian network of nodes connected by edges.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class BayesianNetwork {
    // maintain insertion order
    private final Set<Node> nodes = new LinkedHashSet<>();

    /**
     * Adds a node to the bayesian network
     *
     * @param label node label
     * @return new node with label
     */
    public Node addNode(String label) {
        Node node = new Node(label);
        this.nodes.add(node);
        return node;
    }

    public Node getNode(String label) {
        return nodes.stream().filter(x -> x.getLabel().equalsIgnoreCase(label)).findFirst().orElse(null);
    }

    /**
     * This adds an adge between two nodes.
     * This edge is directed hence make sure that a is the parent of b.
     *
     * @param a parent node
     * @param b child node
     */
    public void addEdge(Node a, Node b) {
        // make a parent of b
        a.addChild(b);
        b.addParent(a);
    }

    /**
     * Query a variable using an order. This is done using variable elimination
     *
     * @param queryInfo query info object containing the query variable, its value and a list of evidence
     * @return probability of that query
     */
    public QueryResult query(QueryInfo queryInfo) {
        if (queryInfo.exists(this)) {
            return new QueryResult(0.0, new String[]{});
        }
        return null;
    }

    /**
     * Query a variable using an order. THis is done using variable elimination
     *
     * @param queryInfo query info object containing the query variable, its value and a list of evidence
     * @param order     order performing join marginalisation.
     * @return query result with the probability and the order
     */
    public QueryResult query(QueryInfo queryInfo, String[] order) {
        OrderingAlgorithm orderingAlgorithm = resolveOrderingAlgorithm("");
        orderingAlgorithm.setOrder(order);
        if (queryInfo.exists(this)) {
            if (queryInfo.hasEvidence()) {
                return queryWithEvidence(queryInfo, orderingAlgorithm);
            } else {
                return queryWithoutEvidence(queryInfo, orderingAlgorithm);
            }
        }
        return null;
    }

    public QueryResult query(QueryInfo queryInfo, String orderingType) {
        OrderingAlgorithm orderingAlgorithm = resolveOrderingAlgorithm(orderingType);
        if (queryInfo.exists(this)) {
            if (queryInfo.hasEvidence()) {
                return queryWithEvidence(queryInfo, orderingAlgorithm);
            } else {
                return queryWithoutEvidence(queryInfo, orderingAlgorithm);
            }
        }
        return null;
    }

    private QueryResult queryWithoutEvidence(QueryInfo queryInfo, OrderingAlgorithm orderingAlgorithm) {
        //P2
        // get query node
        List<Factor> factors;
        if (orderingAlgorithm instanceof ProvidedOrderingAlgorithm) {
            Node queryNode = getNode(queryInfo.getLabel());
            Set<String> order = Arrays.stream( orderingAlgorithm.getOrder()).collect(Collectors.toSet());
            // prune order first
            Set<String> prunedOrder = pruneOrder(order, queryInfo);
            // get factors for the pruned list
            factors = getFactors(prunedOrder);
            //add query node factor as well
            factors.add(queryNode.getCpt().copy());

            // prune label
            for (String pruneLabel : prunedOrder) {

                // find all the factors that contains the label
                List<Factor> toSumOut = factors.stream().filter(x -> x.includes(getNode(pruneLabel))).collect(Collectors.toList());

                // perform join marginalize algorithm
                Factor f = toSumOut.get(0);
                for (int i = 1; i < toSumOut.size(); i++) {
                    Factor tmp = f.join(toSumOut.get(i));
                    f = tmp.sumOut(getNode(pruneLabel));
                }
                factors.removeAll(toSumOut);
                factors.add(f);
            }
            // only one should remain, if not, then you are doing something wrong
            Factor queryVariableFactor = factors.get(0);
            // get probability based on the queried random variable and its value
            Map<String, Boolean> queryMap = queryVariableFactor.generateQueryMap(new boolean[]{queryInfo.getQueryValue()});
            double probability = queryVariableFactor.get(queryMap);

            return new QueryResult(probability, prunedOrder.toArray(String[]::new));
        }
        return new QueryResult(0.0, null);
    }


    private List<Factor> getFactors(Set<String> nodeLabels) {
        return nodeLabels.stream().map(x -> getNode(x).getCpt().copy()).collect(Collectors.toList());
    }

    private QueryResult queryWithEvidence(QueryInfo queryInfo, OrderingAlgorithm orderingAlgorithm) {
        //P3

        // prune order first
        // do target first
        if (orderingAlgorithm instanceof ProvidedOrderingAlgorithm) {
            Node queryNode = getNode(queryInfo.getLabel());
            Set<String> order = Arrays.stream(orderingAlgorithm.getOrder()).collect(Collectors.toSet());
            Set<String> prunedOrder = pruneOrder(order, queryInfo);
            // get factors for the pruned list
            List<Factor> factors = getFactors(prunedOrder);
            //add query node factor as well
            factors.add(queryNode.getCpt().copy());


        }
        return new QueryResult(0.0, null);
    }


    /**
     * Remove nodes that are not ancestors of the target node or evidence nodes (if included)
     * It does this recursively to prune order when performing evidence based query.
     *
     * @param order     set of node labels
     * @param queryInfo query information about query value and evidence
     * @return set of target node ancestors as labels
     */
    private Set<String> pruneOrder(Set<String> order, QueryInfo queryInfo) {
        Node targetNode = getNode(queryInfo.getLabel());
        if (queryInfo.hasEvidence()) {
            //prune order using the target and the evidences, then merge as a union
            // do target first
            List<Node> ancestors = Node.getAllAncestors(targetNode);
            Set<String> nodesToKeep = ancestors.stream().map(Node::getLabel).collect(Collectors.toSet());
            // add pruned nodes for each evidence
            for (QueryInfo evidence : queryInfo.getEvidences()) {
                Set<String> prunedByEvidence = pruneOrder(new LinkedHashSet<>(order), evidence);
                nodesToKeep.addAll(prunedByEvidence);
            }
            // retain only the nodes in the original order that have been pruned by both the target and the evidence
            order.retainAll(nodesToKeep);
        } else {
            // remove labels that are not ancestors of the  node in question
            List<Node> ancestors = Node.getAllAncestors(targetNode);
            order.retainAll(ancestors.stream().map(Node::getLabel).collect(Collectors.toSet()));
        }
        return order;
    }

    private OrderingAlgorithm resolveOrderingAlgorithm(String orderingAlgorithmType) {
        OrderingAlgorithm algorithm;
        switch (orderingAlgorithmType) {
            case "orderingAlgorithm1": {
                algorithm = new OrderingAlgorithm1();
            }
            break;
            case "orderingAlgorithm2": {
                algorithm = new OrderingAlgorithm2();
            }
            break;
            case "":
            default: {
                // try doing provided ordering from terminal
                algorithm = new ProvidedOrderingAlgorithm();
            }
        }
        return algorithm;
    }


    /**
     * Renders the bayesian network, printing out the random variables in ORDER, and their respective
     * conditional probability tables
     */
    public void renderNetwork() {
        System.out.println();
        System.out.println();
        System.out.println();
        for (Node node : nodes) {
            // print variable name
            System.out.println("______________________________");
            System.out.println("Random Variable: " + node.getLabel());
            // print cpt values
            System.out.println();
            node.getCpt().logCPTValues();
            System.out.println("______________________________");
            System.out.println();
            System.out.println();
        }
    }

}
