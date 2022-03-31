package core.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Query Result Class.
 * This class is used to compile results of bayesian network query.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class QueryResult {
    private final double probability;
    private final String[] order;
    private final int noOfJoins;
    private final Map<String, String> pruningHistory;

    /**
     * Constructor specifying the probability and the query order
     *
     * @param probability query r.v probability
     * @param order       query order
     */
    public QueryResult(double probability, String[] order) {
        this(probability, order, -1);

    }

    /**
     * Constructor specifying the probability and the query order and number of joins.
     *
     * @param probability query r.v probability
     * @param order       query order
     * @param noOfJoins   number of joins
     */
    public QueryResult(double probability, String[] order, int noOfJoins) {
        this(probability, order, noOfJoins, new HashMap<>());

    }

    /**
     * Constructor specifying the probability and the query order and number of joins.
     *
     * @param probability    query r.v probability
     * @param order          query order
     * @param noOfJoins      number of joins
     * @param pruningHistory pruning history
     */
    public QueryResult(double probability, String[] order, int noOfJoins, Map<String, String> pruningHistory) {
        this.probability = probability;
        this.order = order;
        this.noOfJoins = noOfJoins;
        this.pruningHistory = pruningHistory;

    }

    /**
     * Gets the pruning history for that query
     *
     * @return pruning history
     */
    public Map<String, String> getPruningHistory() {
        return pruningHistory;
    }

    /**
     * Gets the query probability
     *
     * @return query probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Gets the query order
     *
     * @return query order
     */
    public String[] getOrder() {
        return order;
    }

    /**
     * Gets the number of joins
     *
     * @return number of joins
     */
    public int getNoOfJoins() {
        return noOfJoins;
    }
}
