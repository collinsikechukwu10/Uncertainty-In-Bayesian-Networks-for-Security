package core.ordering;

import core.Node;

import java.util.*;
import java.util.stream.Collectors;

public class MaxCardinalitySearchOrdering implements EliminationOrdering {

    @Override
    public void setOrder(String[] order) {

    }

    private Set<String> generateOrder(List<Node> nodes, Node query) {
        List<Node> unmarked = new ArrayList<>();
        List<Node> marked = new ArrayList<>();


        Collections.reverse(marked);
        marked.remove(query);
        return marked.stream().map(Node::getLabel).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String[] getOrder() {
        return new String[0];
    }
}
