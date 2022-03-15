package core;

import java.util.Map;
import java.util.Set;

/**
 * Factor Class.
 * This class denotes a probability distribution table.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class Factor {
    private final Set<Node> randomVariableNodes;

    public Factor(Set<Node> randomVariableNodes) {
        this.randomVariableNodes = randomVariableNodes;
    }


    public void addValues(double... vals) {

    }

    public Factor join(Factor other) {

    }

    public double get(Node node, boolean value) {
        // make sure the nodes are in the list of random variables in this factor

    }

    public double get(Map<Node, Boolean> nodeValueMap) {
        // make sure the nodes are in the list of random variables in this factor

    }


    public Factor sum(Node randomVariableNode) {

    }

    /**
     * Gets the size of the probability table
     *
     * @return probability table size
     */
    public long getRequiredNumberOfProbabilityTableValues() {
        return (long) Math.pow(2, randomVariableNodes.size());
    }

    public Factor copy() {
        try {
            return (Factor) this.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }


    public void logCPTValues() {

    }
}
