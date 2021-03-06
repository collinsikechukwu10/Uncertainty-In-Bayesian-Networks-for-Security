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
        Node maintenancePlanned = network.addNode("Maintenance Planned");
        Node outdatedMaintenanceInfo = network.addNode("Outdated Maintenance Info");
        Node firewallDeactivation = network.addNode("Firewall Deactivation");
        Node allowedMaliciousWebsite = network.addNode("Allowed Malicious Website");
        Node holiday = network.addNode("Holiday");
        Node networkAttack = network.addNode("Security Attack");
        Node loggingSystemClassification = network.addNode("Logging System Classification");
        Node cnxNetworkNoise = network.addNode("CNX Network Node");
        Node alertTriggered = network.addNode("Alert Triggered");

        network.addEdge(maintenancePlanned, outdatedMaintenanceInfo);
        network.addEdge(maintenancePlanned, firewallDeactivation);
        network.addEdge(outdatedMaintenanceInfo, firewallDeactivation);
        network.addEdge(holiday, networkAttack);
        network.addEdge(firewallDeactivation, networkAttack);
        network.addEdge(allowedMaliciousWebsite, networkAttack);
        network.addEdge(networkAttack, loggingSystemClassification);
        network.addEdge(cnxNetworkNoise, alertTriggered);
        network.addEdge(networkAttack, alertTriggered);
        maintenancePlanned.addCPTValues((double) 29 / 30, (double) 1 / 30);
        outdatedMaintenanceInfo.addCPTValues(1, 0, 0.98, 0.02);
        firewallDeactivation.addCPTValues(1, 0, 1, 0, 0.97, 0.03, 0.95, 0.05);
        holiday.addCPTValues(0.875, 0.125);
        allowedMaliciousWebsite.addCPTValues(0.85, 0.15);
        loggingSystemClassification.addCPTValues(0.7, 0.3, 0.3, 0.7);
        networkAttack.addCPTValues(0.99,0.01,0.6,0.4,0.66,0.34,0.48,0.52,0.98,0.02,0.45,0.55,0.51,0.49,0.33,0.67);
        cnxNetworkNoise.addCPTValues(0.915,0.085);
        alertTriggered.addCPTValues(1,0,0.95,0.05,1,0,0.05,0.95);
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
        a.addCPTValues(0.95, 0.05);
        b.addCPTValues(0.2, 0.8, 0.95, 0.05);
        c.addCPTValues(0.7, 0.3, 0.9, 0.1);
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
        j.addCPTValues(0.95, 0.05);
        k.addCPTValues(0.3, 0.7, 0.1, 0.9);
        l.addCPTValues(0.3, 0.7);
        m.addCPTValues(0.9, 0.1, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6);
        n.addCPTValues(0.8, 0.2, 0.4, 0.6);
        o.addCPTValues(0.2, 0.8, 0.95, 0.05);
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
        network.addEdge(s, z);
        network.addEdge(v, z);
        network.addEdge(s, u);
        p.addCPTValues(0.95, 0.05);
        q.addCPTValues(0.3, 0.7, 0.1, 0.9);
        r.addCPTValues(0.3, 0.7);
        s.addCPTValues(0.9, 0.1, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6);
        u.addCPTValues(0.2, 0.8, 0.95, 0.05);
        v.addCPTValues(0.9, 0.1, 0.85, 0.15, 0.45, 0.55, 0.3, 0.7);
        z.addCPTValues(0.8, 0.2, 0.3, 0.7, 0.6, 0.4, 0.35, 0.65);
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
