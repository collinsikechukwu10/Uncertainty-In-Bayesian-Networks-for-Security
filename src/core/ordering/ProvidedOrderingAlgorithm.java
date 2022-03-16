package core.ordering;

public class ProvidedOrderingAlgorithm implements OrderingAlgorithm {

    private String[] order;


    public ProvidedOrderingAlgorithm(){}

    public String[] getOrder() {
        return order;
    }

    public void setOrder(String[] order) {
        this.order = order;
    }
}
