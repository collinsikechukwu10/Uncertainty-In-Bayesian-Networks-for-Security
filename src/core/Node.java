package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Node Class.
 * This class is used to create a bayesian network node representing a random variable.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class Node {
    private final Set<Node> parents = new HashSet<>();
    private final Set<Node> children = new HashSet<>();
    private final String label;
    private Factor cpt;

    /**
     * Constructor specifying the label
     *
     * @param label random variable label
     */
    public Node(String label) {
        this.label = label;
    }

    /**
     * Gets the label of the node.
     *
     * @return label of the node
     */
    public String getLabel() {
        return label;
    }

    /**
     * Adds a parent node.
     *
     * @param parent parent node
     */
    public void addParent(Node parent) {
        this.parents.add(parent);
    }

    /**
     * Adds a child node.
     *
     * @param child child node
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Adds cpt values to the cpt table.
     *
     * @param vals cpt values
     */
    public void addCPTValues(double... vals) {
        // get parents
        Set<Node> nodes = new HashSet<>(getParents());
        // get current node
        nodes.add(this);
        // create the factor
        cpt = new Factor(nodes);
        cpt.addValues(vals);
    }

    /**
     * Gets the cpt table for a node
     *
     * @return cpt table
     */
    public Factor getCpt() {
        return cpt;
    }

    /**
     * Gets the parents of the node
     *
     * @return node parents
     */
    public Set<Node> getParents() {
        return parents;
    }

    /**
     * Gets all ancestors of a node
     *
     * @param node node to search
     * @return list of ancestors
     */
    public static List<Node> getAllAncestors(Node node) {
        List<Node> nodesList = new ArrayList<>();
        if (node.getParents().size() == 0) {
            return nodesList;
        }
        for (Node parent : node.getParents()) {
            nodesList.add(parent);
            nodesList.addAll(getAllAncestors(parent));
        }
        return nodesList;
    }
}
