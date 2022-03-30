import core.*;
import core.ordering.GreedyOrderingStrategy;
import core.ordering.MaxCardinalitySearchOrderingStrategy;
import core.ordering.ProvidedOrderingStrategy;
import core.query.QueryInfo;
import core.query.QueryResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class contains some examples on how to handle the required inputs and outputs
 *
 * @author at258
 * <p>
 * run with
 * java A3main <Pn> <NID>
 * <p>
 * Feel free to change and delete parts of the code as you prefer
 */
public class A3main {
    private static final DecimalFormat dd = new DecimalFormat("#0.00000");

    /**
     * Main command line entrypoint.
     *
     * @param args command line string arguments
     */
    public static void main(String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("evalCNX")){
            Evaluator evaluator = new Evaluator();
            evaluator.run();
            return;
        }

        Scanner sc = new Scanner(System.in);
        InputScannerUtils scannerUtils = new InputScannerUtils(sc);
        // generate bayesian network based on network ID
        boolean verbose = args.length==3 && args[2].equalsIgnoreCase("verbose");
        BayesianNetwork network = NetworkGenerator.buildNetwork(args[1]);
        network.setVerbose(verbose);
        // handle additional parameters

        switch (args[0]) {
            case "P1": {
                System.out.println("Network " + args[1]);
                network.renderNetwork();
            }
            break;

            case "P2": {
                // execute query of p(variable=value) with given order of elimination
                String[] query = scannerUtils.getQueriedNode();
                String[] order = scannerUtils.getOrder();
                QueryInfo queryInfo = new QueryInfo(query[0], QueryInfo.resolveBoolean(query[1]));
                network.setOrdering(new ProvidedOrderingStrategy(order));
                QueryResult result = network.query(queryInfo);
                printResult(result.getProbability());
            }
            break;

            case "P3": {
                // execute query of p(variable=value|evidence) with given order of elimination
                String[] query = scannerUtils.getQueriedNode();
                String[] order = scannerUtils.getOrder();
                ArrayList<String[]> evidence = scannerUtils.getEvidence();
                QueryInfo queryInfo = new QueryInfo(query[0], QueryInfo.resolveBoolean(query[1]), evidence);
                network.setOrdering(new ProvidedOrderingStrategy(order));
                QueryResult result = network.query(queryInfo);
                printResult(result.getProbability());
            }
            break;

            case "P4": {
                // execute query of p(variable=value|evidence) using max cardinality search ordering
                String[] query = scannerUtils.getQueriedNode();
                ArrayList<String[]> evidence = scannerUtils.getEvidence();
                QueryInfo queryInfo = new QueryInfo(query[0], QueryInfo.resolveBoolean(query[1]), evidence);
                // using automatic ordering algorithm
                network.setOrdering(new MaxCardinalitySearchOrderingStrategy());
                QueryResult result = network.query(queryInfo);
                System.out.println(Arrays.toString(result.getOrder())); // order = "A,B";
                printResult(result.getProbability());
            }
            break;
            case "P5": {
                // execute query of p(variable=value|evidence) using greedy search ordering
                String[] query = scannerUtils.getQueriedNode();
                ArrayList<String[]> evidence = scannerUtils.getEvidence();
                QueryInfo queryInfo = new QueryInfo(query[0], QueryInfo.resolveBoolean(query[1]), evidence);
                network.setOrdering(new GreedyOrderingStrategy());
                QueryResult result = network.query(queryInfo);
                System.out.println(Arrays.toString(result.getOrder()));
                printResult(result.getProbability());
            }
            break;
        }
        sc.close();
    }

    /**
     * Method to format and print the result.
     * @param result numeric result
     */
    private static void printResult(double result) {
        System.out.println(dd.format(result));
    }

}
