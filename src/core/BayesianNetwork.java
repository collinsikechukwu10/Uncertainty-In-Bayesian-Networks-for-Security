package core;

import core.ordering.BasicOrderingStrategy;
import core.ordering.IntermediateOrderingStrategy;
import core.query.QueryInfo;
import core.query.QueryResult;

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
    private BasicOrderingStrategy ordering;

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

    /**
     * Sets ordering for the
     *
     * @param ordering ordering strategy
     */
    public void setOrdering(BasicOrderingStrategy ordering) {
        this.ordering = ordering;

        if(ordering instanceof IntermediateOrderingStrategy){
            ((IntermediateOrderingStrategy) ordering).setNodesList(new ArrayList<>(this.nodes));
        }
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
     * Query a variable using an order. THis is done using variable elimination
     *
     * @param queryInfo query info object containing the query variable, its value and a list of evidence
     * @return query result with the probability and the order
     */
    public QueryResult query(QueryInfo queryInfo) {
        if (queryInfo.exists(this)) {
            if (queryInfo.hasEvidence()) {
                return queryWithEvidence(queryInfo);
            } else {
                return queryWithoutEvidence(queryInfo);
            }
        }
        return null;
    }

    private QueryResult queryWithoutEvidence(QueryInfo queryInfo) {
        //P2
        // get query node
        Node queryNode = getNode(queryInfo.getLabel());
        // prune order first
        Set<String> order = ordering.getOrder(queryInfo);
        Set<String> prunedOrder = pruneOrder(order, queryInfo);
        Set<String> factorLabels = new HashSet<>(prunedOrder);
        factorLabels.add(queryNode.getLabel());
        // get factors for the pruned list
        List<Factor> factors = getFactors(factorLabels);

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
        Factor queryFactor = factors.get(0);
        // get probability based on the queried random variable and its value
        Map<String, Boolean> queryMap = queryFactor.generateQueryMap(new boolean[]{queryInfo.getQueryValue()});
        double probability = queryFactor.get(queryMap);

        return new QueryResult(probability, prunedOrder.toArray(String[]::new));
    }


    private List<Factor> getFactors(Set<String> nodeLabels) {
        return nodeLabels.stream().map(x -> getNode(x).getCpt().copy()).collect(Collectors.toList());
    }


    private QueryResult queryWithEvidence(QueryInfo queryInfo) {
        // prune order first
        // do target first
        Node queryNode = getNode(queryInfo.getLabel());
        Set<String> order = ordering.getOrder(queryInfo);
        Set<String> prunedOrder = pruneOrder(order, queryInfo);

        // add evidence to prunable order because previous function does not include it
        Set<String> factorLabels = new HashSet<>(prunedOrder);
        factorLabels.add(queryNode.getLabel());

        // get factors for the pruned list, evidences and query node
        List<Factor> factors = getFactors(factorLabels);

        // set evidence in factor to zero for each factor that the r.v. exists in where its value is the same as the evidence value
        for (QueryInfo evidence : queryInfo.getEvidences()) {
            factors.forEach(factor -> {
                Node evidenceNode = getNode(evidence.getLabel());
                if (factor.includes(evidenceNode)) {
                    factor.projectToZero(evidenceNode, evidence.getQueryValue());
                }
            });
        }
        // prune label
        for (String pruneLabel : prunedOrder) {

            // find all the factors that contains the label
            List<Factor> toSumOut = factors.stream().filter(x -> x.includes(getNode(pruneLabel))).collect(Collectors.toList());

            // perform join marginalize algorithm
            Factor f = toSumOut.get(0);
            if (toSumOut.size() > 1) {
                for (int i = 1; i < toSumOut.size(); i++) {
                    Factor tmp = f.join(toSumOut.get(i));
                    f = tmp.sumOut(getNode(pruneLabel));
                }
            } else {
                f = f.sumOut(getNode(pruneLabel));
            }

            factors.removeAll(toSumOut);
            factors.add(f);
        }

        // join factors if factors are more than one
        if (factors.size() > 1) {
            Factor f = factors.get(0);
            for (int i = 1; i < factors.size(); i++) {
                f = f.join(factors.get(i));
            }
            factors = new ArrayList<>(List.of(f));
        }
        Factor queryFactor = factors.get(0);
        // normalize
        queryFactor.normalize();
        // get probability based on the queried random variable and its value
        Map<String, Boolean> queryMap = queryFactor.generateQueryMap(new boolean[]{queryInfo.getQueryValue()});
        double probability = queryFactor.get(queryMap);

        return new QueryResult(probability, prunedOrder.toArray(String[]::new));
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
        Set<String> pruneOrder = new LinkedHashSet<>(order);
        if (queryInfo.hasEvidence()) {
            //prune order using the target and the evidences, then merge as a union
            // do target first
            List<Node> ancestors = Node.getAllAncestors(targetNode);
            Set<String> nodesToKeep = ancestors.stream().map(Node::getLabel).collect(Collectors.toSet());
            // add pruned nodes for each evidence
            for (QueryInfo evidence : queryInfo.getEvidences()) {
                Set<String> prunedByEvidence = pruneOrder(new LinkedHashSet<>(order), evidence);
                nodesToKeep.addAll(prunedByEvidence);
                // include evidence label
                nodesToKeep.add(evidence.getLabel());
            }
            // retain only the nodes in the original order that have been pruned by both the target and the evidence
            pruneOrder.retainAll(nodesToKeep);
            // just incase the target node is included in the order, (if target node is an ancestor of an evidence nose)
            // remove it, so you dont prune it from when doing join marginalization
            pruneOrder.remove(targetNode.getLabel());

        } else {
            // remove labels that are not ancestors of the  node in question
            List<Node> ancestors = Node.getAllAncestors(targetNode);
            pruneOrder.retainAll(ancestors.stream().map(Node::getLabel).collect(Collectors.toSet()));
        }
        return pruneOrder;
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
