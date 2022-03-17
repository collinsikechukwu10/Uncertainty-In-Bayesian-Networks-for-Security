package core;

import java.util.*;

/**
 * Node Class.
 * This class is used to create a bayesian network node representing a random variable.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class Node {
    private final Set<Node> parents = new LinkedHashSet<>();
    private final Set<Node> children = new LinkedHashSet<>();
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
        // create the factor
        cpt = new Factor(this);
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
     * Gets the children of the node
     *
     * @return node children
     */
    public Set<Node> getChildren() {
        return children;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return label.equals(node.label) && Objects.equals(cpt, node.cpt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, cpt);
    }
}
