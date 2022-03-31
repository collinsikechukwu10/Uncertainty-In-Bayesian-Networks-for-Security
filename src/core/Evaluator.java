package core;

import core.ordering.*;
import core.query.QueryInfo;
import core.query.QueryResult;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluator Class.
 * This class is used to run queries of the cnx network.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class Evaluator {
    final BayesianNetwork cnxNetwork;
    private static final DecimalFormat dd = new DecimalFormat("#0.00000");
    private final int SEED = 123;

    /**
     * Empty evaluator constructor.
     */
    public Evaluator() {
        cnxNetwork = NetworkGenerator.buildNetwork("CNX");
        cnxNetwork.setVerbose(true);
    }

    /**
     * Runs all queries for CNX Network.
     */
    public void run() {
        printSeparator();
        System.out.println("[P1]: Print Network");
        cnxNetwork.renderNetwork();
        System.out.println();
        printSeparator();
        System.out.println("[P2]: Run a query without evidence");
        performQueryForPredictingAttackWithoutEvidence();
        System.out.println();
        printSeparator();
        System.out.println("[P3]: Run a diagnostic and predictive query with evidence");
        performQueryForPredictingAttackWithEvidence();
        printSeparator();
        System.out.println("[P4 and P5]: Show how order affects the computation of queries.. We use *no of joins*");
        getJoinsPerOrdering();


    }

    private void printSeparator(){
        System.out.println("#".repeat(50));
    }

    /**
     * Performs non-evidence queries for the CNX Network.
     */
    public void performQueryForPredictingAttackWithoutEvidence() {
        Node securityAttack = cnxNetwork.getNode("Security Attack");
        Node firewallDeactivation = cnxNetwork.getNode("Firewall Deactivation");
        List<QueryInfo> queries = new ArrayList<>();
        queries.add(new QueryInfo(securityAttack.getLabel(), true));
        queries.add(new QueryInfo(firewallDeactivation.getLabel(), true));
        Random random = new Random(SEED);
        queries.forEach(queryInfo -> {
            List<String> orderList = createOrderList(queryInfo.getLabel());
            // play around with different orders
            List<String[]> orders = new ArrayList<>();

            // create 2 random shuffles of the order
            Collections.shuffle(orderList, random);
            orders.add(orderList.toArray(String[]::new));
            Collections.shuffle(orderList, random);
            orders.add(orderList.toArray(String[]::new));
            orders.forEach(order -> {
                // apply ordering
                System.out.println("Using order: " + Arrays.toString(order));
                cnxNetwork.setOrdering(new ProvidedOrderingStrategy(order));
                QueryResult result = cnxNetwork.query(queryInfo);
                System.out.println("[Query P("+ queryInfo.getLabel() +"=" + queryInfo.getQueryValue() + ")] " + dd.format(result.getProbability() * 100) + "%");
            });
        });
    }

    /**
     * Performs evidence queries for the CNX Network.
     */
    public void performQueryForPredictingAttackWithEvidence() {
        cnxNetwork.setVerbose(false);
        Node holiday = cnxNetwork.getNode("Holiday");

        // DIAGNOSTIC QUERY
        // we do attack in reference to logging system. i.e we see if a log generated was actually from an attack
        Node attack = cnxNetwork.getNode("Security Attack");
        Node maintenancePlanned = cnxNetwork.getNode("Maintenance Planned");
        List<String> diagnosticOrder = createOrderList(maintenancePlanned.getLabel());
        cnxNetwork.setOrdering(new ProvidedOrderingStrategy(diagnosticOrder.toArray(String[]::new)));
        List<String[]> diagnosticEvidence = new ArrayList<>();
        diagnosticEvidence.add(new String[]{attack.getLabel(), QueryInfo.resolveToBooleanString(true)});
        diagnosticEvidence.add(new String[]{holiday.getLabel(), QueryInfo.resolveToBooleanString(true)});
        QueryInfo diagnosticQuery = new QueryInfo(maintenancePlanned.getLabel(), true, diagnosticEvidence);
        QueryInfo diagnosticQueryWithoutEvidence = new QueryInfo(maintenancePlanned.getLabel(), true);

        QueryResult diagnosticResult = cnxNetwork.query(diagnosticQuery);
        QueryResult diagnosticResultWithoutEvidence = cnxNetwork.query(diagnosticQueryWithoutEvidence);

        System.out.println("[Diagnostic query with evidence P(Maintenance Planned=True| Attack = True, Holiday= True)] "
                + dd.format(diagnosticResult.getProbability() * 100) + "%");
        System.out.println("[Diagnostic query without evidence P(Maintenance Planned=True)] "
                + dd.format(diagnosticResultWithoutEvidence.getProbability() * 100) + "%");


        // PREDICTIVE QUERY
        Node outdatedMaintenanceInfo = cnxNetwork.getNode("Outdated Maintenance Info");
        Node alertTriggered = cnxNetwork.getNode("Alert Triggered");
        List<String> predictiveOrder = createOrderList(alertTriggered.getLabel());
        cnxNetwork.setOrdering(new ProvidedOrderingStrategy(predictiveOrder.toArray(String[]::new)));
        List<String[]> predictiveEvidence = new ArrayList<>();
        predictiveEvidence.add(new String[]{outdatedMaintenanceInfo.getLabel(), QueryInfo.resolveToBooleanString(true)});
        predictiveEvidence.add(new String[]{holiday.getLabel(), QueryInfo.resolveToBooleanString(false)});
        QueryInfo predictiveQuery = new QueryInfo(alertTriggered.getLabel(), true, predictiveEvidence);
        QueryInfo predictiveQueryWithoutEvidence = new QueryInfo(alertTriggered.getLabel(), true);

        QueryResult predictiveResult = cnxNetwork.query(predictiveQuery);
        QueryResult predictiveResultWithoutEvidence = cnxNetwork.query(predictiveQueryWithoutEvidence);

        System.out.println("[Predictive query with evidence P(Alert Triggered=True| Holiday=false, Outdated Maintenance Info = True)] " +
                dd.format(predictiveResult.getProbability() * 100) + "%");
        System.out.println("[Predictive query without evidence P(Alert Triggered=True)] " +
                dd.format(predictiveResultWithoutEvidence.getProbability() * 100) + "%");


    }

    /**
     * Counts joins based on the implemented ordering strategies.
     */
    public void getJoinsPerOrdering() {
        cnxNetwork.setVerbose(false);

        // perform join analysis for maximum cardinality
        System.out.println("Max Cardinality Search Ordering");
        getJoinsPerOrdering(new MaxCardinalitySearchOrderingStrategy());
        printSeparator();
        System.out.println("Greedy Search Ordering");
        // perform join analysis for greedy search [ADVANCED FUNCTIONALITIES]
        getJoinsPerOrdering(new GreedyOrderingStrategy());

    }

    /**
     * Counts joins based on the ordering strategies provided.
     */
    public void getJoinsPerOrdering(IntermediateOrderingStrategy intermediateOrderingStrategy) {
        // since we are using max cardinality, do this multiple times since it is initialized at random
        int repeats = 100;
        Node attack = cnxNetwork.getNode("Security Attack");
        Map<String, QueryResult> orderToJoinMapping = new HashMap<>();
        for (int i = 0; i < repeats; i++) {
            cnxNetwork.setOrdering(intermediateOrderingStrategy);
            QueryInfo predictiveQuery = new QueryInfo(attack.getLabel(), true);
            QueryResult queryResult = cnxNetwork.query(predictiveQuery);
            String key = Arrays.toString(queryResult.getOrder());
            orderToJoinMapping.putIfAbsent(key, queryResult);
        }

        orderToJoinMapping.forEach((order, qr) -> {
            System.out.println("Order:" + order);
            System.out.println("-----------------------------------------");
            qr.getPruningHistory().forEach((pruneLabel, pruneFactors)->{
                System.out.println("After pruning [" + pruneLabel + "]-->factors:[" + pruneFactors + "]");
            });
            System.out.println("Complexity: "+ qr.getComplexity());
            System.out.println("-----------------------------------------");

        });
    }

    /**
     * Get the CNX network random variables used in the network.
     * @return network random variables
     */
    private List<String> cnXNetworkRandomVariableLabels() {
        return cnxNetwork.getNodes().stream().map(Node::getLabel).collect(Collectors.toList());
    }

    /**
     * Returns the order of the CNX network nodes based its insertion into the network.
     * The target label is removed from this list.
     * @param targetLabel target label
     * @return order
     */
    private List<String> createOrderList(String targetLabel) {
        List<String> order = cnXNetworkRandomVariableLabels();
        order.remove(targetLabel);

        return order;
    }
}
