package core.ordering;

import core.query.QueryInfo;

import java.util.Set;

/**
 * Basic Ordering Class.
 * This class is the base class for all order creation for bayesian network queries.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public abstract class BasicOrderingStrategy {
    /**
     * Gets the order based on the ordering algorithm.
     *
     * @param queryInfo query information
     * @return set of labels
     */
    public abstract Set<String> getOrder(QueryInfo queryInfo);

}
