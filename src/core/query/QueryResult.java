package core.query;

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
        this.probability = probability;
        this.order = order;
        this.noOfJoins = noOfJoins;

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
