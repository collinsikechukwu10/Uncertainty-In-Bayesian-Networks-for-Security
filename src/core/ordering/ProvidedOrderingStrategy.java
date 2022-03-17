package core.ordering;

import core.query.QueryInfo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provided Ordering Class.
 * This class is used to manually get an order for a bayesian network query.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class ProvidedOrderingStrategy extends BasicOrderingStrategy {

    private final Set<String> providedOrder;

    /**
     * Constructor specifying the order.
     *
     * @param order order
     */
    public ProvidedOrderingStrategy(String[] order) {
        this.providedOrder = Arrays.stream(order).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Gets the order based on the ordering algorithm.
     *
     * @param queryInfo query information
     * @return set of labels
     */
    public Set<String> getOrder(QueryInfo queryInfo) {
        return providedOrder;
    }
}
