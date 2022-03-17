package core.query;

import core.BayesianNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Query Info Class.
 * This class is used to instantiate a query given evidence for a bayesian network to infer.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class QueryInfo {
    private final String label;
    private final boolean value;
    private final List<QueryInfo> evidences = new ArrayList<>();

    /**
     * Constructor for query info specifying the random variable label and its value.
     * No evidence is provided here
     *
     * @param label random variable label
     * @param value random variable value
     */
    public QueryInfo(String label, boolean value) {
        this(label, value, new ArrayList<>());
    }

    /**
     * Constructor for query info specifying the random variable label and its value.
     * No evidence is provided here
     *
     * @param label     random variable label
     * @param value     random variable value
     * @param evidences list of evidence
     */
    public QueryInfo(String label, boolean value, List<String[]> evidences) {
        this.label = label;
        this.value = value;
        for (String[] evidence : evidences) {
            this.evidences.add(new QueryInfo(evidence[0], resolveBoolean(evidence[1])));
        }
    }

    /**
     * Gets the random variable label
     *
     * @return random variable label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the random variable value
     *
     * @return random variable value
     */
    public boolean getQueryValue() {
        return value;
    }

    /**
     * Checks if the random variables in the query exist in the bayesian network.
     *
     * @param network bayesian network to examine
     * @return True if query random variables exist in the bayesian network
     */
    public boolean exists(BayesianNetwork network) {
        return network.getNode(label) != null;
    }

    /**
     * Gets the evidences.
     *
     * @return list of evidence for the query
     */
    public List<QueryInfo> getEvidences() {
        return evidences;
    }

    /**
     * Checks if the query has any evidence.
     *
     * @return True if evidence exists
     */
    public boolean hasEvidence() {
        return getEvidences().size() != 0;
    }

    /**
     * Returns the boolean representation of a boolen string
     * @param booleanString boolean string
     * @return boolean value
     */
    public static boolean resolveBoolean(String booleanString) {
        return booleanString.equalsIgnoreCase("T");
    }

}
