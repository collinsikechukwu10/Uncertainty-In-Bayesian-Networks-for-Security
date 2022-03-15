import core.*;

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

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        InputScannerUtils scannerUtils = new InputScannerUtils(sc);
        // generate bayesian network based on network ID
        BayesianNetwork network = NetworkGenerator.buildNetwork(args[1]);

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
                String variable = query[0];
                String value = query[1];
                QueryInfo queryInfo = new QueryInfo(variable, Boolean.parseBoolean(value));
                QueryResult result = network.query(queryInfo, order);
                printResult(result.getProbability());
            }
            break;

            case "P3": {
                // execute query of p(variable=value|evidence) with given order of elimination
                String[] query = scannerUtils.getQueriedNode();
                String[] order = scannerUtils.getOrder();
                ArrayList<String[]> evidence = scannerUtils.getEvidence();
                String variable = query[0];
                String value = query[1];
                QueryInfo queryInfo = new QueryInfo(variable, Boolean.parseBoolean(value), evidence);
                QueryResult result = network.query(queryInfo, order);
                printResult(result.getProbability());
            }
            break;

            case "P4": {
                String[] query = scannerUtils.getQueriedNode();
                ArrayList<String[]> evidence = scannerUtils.getEvidence();
                String variable = query[0];
                String value = query[1];
                // execute query of p(variable=value|evidence) with given order of elimination
                QueryInfo queryInfo = new QueryInfo(variable, Boolean.parseBoolean(value), evidence);
                QueryResult result = network.query(queryInfo);
                System.out.println(Arrays.toString(result.getOrder())); // order = "A,B";
                printResult(result.getProbability());
            }
            break;
            case "P5": {

            }
            break;
        }
        sc.close();
    }

    //method to format and print the result
    private static void printResult(double result) {
        System.out.println(dd.format(result));
    }

}
