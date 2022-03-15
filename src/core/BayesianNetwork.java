package core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final Set<Node> nodes = new HashSet<>();

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
        a.addParent(b);
        b.addChild(a);
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
        if (queryInfo.exists(this)) {
            if (queryInfo.hasEvidence()) {
                return queryWithEvidence(queryInfo, order);
            } else {
                return queryWithoutEvidence(queryInfo, order);
            }
        }
        return null;
    }

    private QueryResult queryWithoutEvidence(QueryInfo queryInfo, String[] order) {
        //P2
        // get query node
        Node queryNode = getNode(queryInfo.getLabel());

        // prune order first
        Set<String> pruneLabels = pruneOrder(Arrays.stream(order).collect(Collectors.toSet()), queryInfo.getLabel());

        // get factors for the pruned list
        List<Factor> factors = pruneLabels.stream().map(label -> getNode(label).getCpt().copy()).collect(Collectors.toList());

        // TODO implement sumout, join, and marginalization
        //  --------------------------------
        //  --------------------------------

        // keep doing some processing till the factors list gets done to just one item in it
        Factor queryVariableFactor = factors.get(0);

        return new QueryResult(queryVariableFactor.get(queryNode, queryInfo.getQueryValue()), order);
    }

    private QueryResult queryWithEvidence(QueryInfo queryInfo, String[] order) {
        //P3
        return new QueryResult(0.0, order);
    }

    /**
     * Remove nodes that are not ancestors of the target node
     *
     * @param order  set of node labels
     * @param target target node label
     * @return set of target node ancestors as labels
     */
    private Set<String> pruneOrder(Set<String> order, String target) {
        // remove labels that are not ancestors of the  node in question
        Node targetNode = getNode(target);
        // get all children
        List<Node> ancestors = Node.getAllAncestors(targetNode);
        order.retainAll(ancestors.stream().map(Node::getLabel).collect(Collectors.toSet()));
        return order;
    }


    /**
     * Renders the bayesian network, printing out the random variables in ORDER, and their respective
     * conditional probability tables
     */
    public void renderNetwork() {
        for (Node node : nodes) {
            // print variable name
            System.out.println("______________________________");
            System.out.println("*** Random Variable: " + node.getLabel() + "***");
            // print cpt values
            System.out.println("CPT Values:..");
            node.getCpt().logCPTValues();
            System.out.println("______________________________");
            System.out.println();
            System.out.println();
        }
    }

}
