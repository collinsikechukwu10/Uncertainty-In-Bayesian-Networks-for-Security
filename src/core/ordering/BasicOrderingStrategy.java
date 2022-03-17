package core.ordering;

import core.query.QueryInfo;

import java.util.Set;

public abstract class BasicOrderingStrategy {
    public abstract Set<String> getOrder(QueryInfo queryInfo);

}
