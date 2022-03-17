package core.ordering;

import core.query.QueryInfo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProvidedOrderingStrategy extends BasicOrderingStrategy {

    private final Set<String> providedOrder;

    public ProvidedOrderingStrategy(String[] order){
        this.providedOrder = Arrays.stream(order).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public Set<String> getOrder(QueryInfo queryInfo) {
        return providedOrder;
    }
}
