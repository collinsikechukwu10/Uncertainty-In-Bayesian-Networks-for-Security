package core;

import core.ordering.MaxCardinalitySearchOrderingStrategy;
import core.ordering.ProvidedOrderingStrategy;
import core.query.QueryInfo;
import core.query.QueryResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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


    public Evaluator() {
        cnxNetwork = NetworkGenerator.buildNetwork("CNX");
        cnxNetwork.setVerbose(true);
    }

    public void run() {
        System.out.println("[P1]: Print Network");
        cnxNetwork.renderNetwork();
        System.out.println();
        System.out.println("[P2]: Run a query without evidence");
        performQueryForPredictingAttackWithoutEvidence();
        System.out.println();
        System.out.println("[P3]: Run a diagnostic and predictive query with evidence");
        performQueryForPredictingAttackWithEvidence();
        System.out.println("[P4]: Show how order affects the computation of queries.. We use *no of joins*");
        getJoinsPerOrdering();



    }

    public void performQueryForPredictingAttackWithoutEvidence() {
        Node attack = cnxNetwork.getNode("Attack");
        QueryInfo queryInfo = new QueryInfo(attack.getLabel(), true);

        // play around with different orders
        List<String[]> orders = new ArrayList<>();
        orders.add(new String[]{});
        orders.add(new String[]{});

        orders.forEach(order -> {
            // apply ordering
            System.out.println("Using order: " + Arrays.toString(order));
            cnxNetwork.setOrdering(new ProvidedOrderingStrategy(order));
            QueryResult result = cnxNetwork.query(queryInfo);
            System.out.println("Potential of attack: " + dd.format(result.getProbability() * 100) + "%");
        });
    }

    public void performQueryForPredictingAttackWithEvidence() {
        Node attack = cnxNetwork.getNode("Attack");
        // set ordering
        String[] order = new String[]{};
        cnxNetwork.setOrdering(new ProvidedOrderingStrategy(order));


        // DIAGNOSTIC QUERY
        // we do attack in reference to logging system. i.e we see if a log generated was actually from an attack

        Node logging = cnxNetwork.getNode("Logging");
        List<String[]> diagnosticEvidence = new ArrayList<>();
        diagnosticEvidence.add(new String[]{logging.getLabel(), QueryInfo.resolveToBooleanString(true)});
        QueryInfo diagnosticQuery = new QueryInfo(attack.getLabel(), true, diagnosticEvidence);
        QueryResult diagnosticResult = cnxNetwork.query(diagnosticQuery);
        System.out.println("Diagnostic query, looking at the probability that " +
                "a log to the system was caused by an attack is " + dd.format(diagnosticResult.getProbability() * 100) + "%");


        // PREDICTIVE QUERY
        Node maintenanceInfo = cnxNetwork.getNode("Maintenance Info");
        Node maliciousWebsite = cnxNetwork.getNode("Malicious Website");
        List<String[]> predictiveEvidence = new ArrayList<>();
        predictiveEvidence.add(new String[]{maintenanceInfo.getLabel(), QueryInfo.resolveToBooleanString(false)});
        predictiveEvidence.add(new String[]{maliciousWebsite.getLabel(), QueryInfo.resolveToBooleanString(true)});
        QueryInfo predictiveQuery = new QueryInfo(attack.getLabel(), true, predictiveEvidence);
        QueryResult predictiveResult = cnxNetwork.query(predictiveQuery);
        System.out.println("Predictive query, looking at the probability that " +
                "there is an attack when there is a malicious website and when there was no maintenance scheduled: " + dd.format(predictiveResult.getProbability() * 100) + "%");


    }

    public void getJoinsPerOrdering(){
        // since we are using max cardinality, do this multiple times since it is initialized at random
        int repeats = 100;
        Node attack = cnxNetwork.getNode("Attack");
        HashMap<String,Integer> orderToJoinMapping = new HashMap<>();
        for (int i = 0; i < repeats; i++) {
            cnxNetwork.setOrdering(new MaxCardinalitySearchOrderingStrategy());
            QueryInfo predictiveQuery = new QueryInfo(attack.getLabel(), true);
            QueryResult queryResult = cnxNetwork.query(predictiveQuery);
            String key = Arrays.toString(queryResult.getOrder());
            orderToJoinMapping.putIfAbsent(key, queryResult.getNoOfJoins());
        }
        orderToJoinMapping.forEach((order,noOfJoins)->{
            System.out.println("Order: ["+ order + "] performed "+ noOfJoins + "joins");
        });
    }
}
