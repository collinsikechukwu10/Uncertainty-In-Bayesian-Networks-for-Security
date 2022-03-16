package core.ordering;

public class ProvidedOrdering implements EliminationOrdering {

    private String[] order;


    public ProvidedOrdering(){}

    public String[] getOrder() {
        return order;
    }

    public void setOrder(String[] order) {
        this.order = order;
    }
}
