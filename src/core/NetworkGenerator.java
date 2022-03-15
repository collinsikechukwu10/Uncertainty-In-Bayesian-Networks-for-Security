package core;

/**
 * Network Generator Class.
 * This class generates all the bayesian networks required for the practical
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class NetworkGenerator {

    /**
     * Generates the CNX bayesian network
     *
     * @return CNX Network
     */
    private static BayesianNetwork createCNXNetwork() {
        BayesianNetwork network = new BayesianNetwork();
        Node maintenance = network.addNode("Maintenance");
        Node firewall = network.addNode("Firewall");
        Node maliciousWebsite = network.addNode("Malicious Website");
        Node holiday = network.addNode("Holiday");
        Node attack = network.addNode("Attack");
        Node logging = network.addNode("Logging ");
        Node alarm = network.addNode("Alarm");
        holiday.addCPTValues(0.125, 0.875);
        network.addEdge(holiday, attack);
        network.addEdge(maintenance, firewall);

        return network;
    }

    /**
     * Generates the BNA bayesian network
     *
     * @return BNA Network
     */
    private static BayesianNetwork createBNANetwork() {
        BayesianNetwork network = new BayesianNetwork();
        Node a = network.addNode("A");
        Node b = network.addNode("B");
        Node c = network.addNode("C");
        Node d = network.addNode("D");
        network.addEdge(a, b);
        network.addEdge(b, c);
        network.addEdge(c, d);
        a.addCPTValues(0.05, 0.95);
        b.addCPTValues(0.05, 0.95, 0.8, 0.2);
        c.addCPTValues(0.1, 0.9, 0.3, 0.7);
        d.addCPTValues(0.4, 0.6, 0.6, 0.4);
        return network;
    }

    /**
     * Generates the BNB bayesian network
     *
     * @return BNB Network
     */
    private static BayesianNetwork createBNBNetwork() {
        BayesianNetwork network = new BayesianNetwork();
        Node j = network.addNode("J");
        Node k = network.addNode("K");
        Node l = network.addNode("L");
        Node m = network.addNode("M");
        Node n = network.addNode("N");
        Node o = network.addNode("O");
        network.addEdge(j, k);
        network.addEdge(k, m);
        network.addEdge(l, m);
        network.addEdge(m, n);
        network.addEdge(m, o);
        j.addCPTValues(0.05, 0.95);
        k.addCPTValues(0.9, 0.1, 0.7, 0.3);
        l.addCPTValues(0.7, 0.3);
        m.addCPTValues(0.6, 0.4, 0.7, 0.3, 0.2, 0.8, 0.1, 0.9);
        n.addCPTValues(0.6, 0.4, 0.2, 0.8);
        o.addCPTValues(0.05, 0.95, 0.8, 0.2);
        return network;
    }

    /**
     * Generates the BNC bayesian network
     *
     * @return BNC Network
     */
    private static BayesianNetwork createBNCNetwork() {
        BayesianNetwork network = new BayesianNetwork();
        Node p = network.addNode("P");
        Node q = network.addNode("Q");
        Node r = network.addNode("R");
        Node s = network.addNode("S");
        Node u = network.addNode("U");
        Node v = network.addNode("V");
        Node z = network.addNode("Z");
        network.addEdge(p, q);
        network.addEdge(q, v);
        network.addEdge(q, s);
        network.addEdge(r, v);
        network.addEdge(r, s);
        network.addEdge(v, z);
        network.addEdge(s, z);
        network.addEdge(s, u);
        p.addCPTValues(0.05, 0.95);
        q.addCPTValues(0.9, 0.1, 0.7, 0.3);
        r.addCPTValues(0.7, 0.3);
        s.addCPTValues(0.6, 0.4, 0.7, 0.3, 0.2, 0.8, 0.1, 0.9);
        u.addCPTValues(0.05, 0.95, 0.8, 0.2);
        v.addCPTValues(0.7, 0.3, 0.55, 0.45, 0.15, 0.85, 0.1, 0.9);
        z.addCPTValues(0.65, 0.35, 0.7, 0.3, 0.4, 0.6, 0.2, 0.8);
        return network;
    }

    /**
     * Resolves the bayesian network based on the network type
     *
     * @param networkType network type
     * @return bayesian network of the network type
     */
    public static BayesianNetwork buildNetwork(String networkType) {
        BayesianNetwork network = null;
        switch (networkType.toUpperCase()) {
            case "BNA":
                network = createBNANetwork();
                break;
            case "BNB":
                network = createBNBNetwork();
                break;
            case "BNC":
                network = createBNCNetwork();
                break;
            case "CNX":
                network = createCNXNetwork();
                break;
        }
        return network;
    }


}
