package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Input Scanner Utilities.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
public class InputScannerUtils {
    private final Scanner scanner;

    /**
     * Constructor specifying the scanner
     *
     * @param scanner scanner connected to stdin
     */
    public InputScannerUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Method to obtain the evidence from the user
     *
     * @return list of evidence
     */
    public ArrayList<String[]> getEvidence() {
        System.out.println("Evidence:");
        ArrayList<String[]> evidence = new ArrayList<>();
        String[] line = scanner.nextLine().split(" ");
        Arrays.stream(line).forEach(l -> evidence.add(l.split(":")));
//        for (String st : line) {String[] ev = st.split(":");evidence.add(ev);}
        return evidence;
    }

    /**
     * Method to obtain the order from the user
     *
     * @return order
     */
    public String[] getOrder() {
        System.out.println("Order:");
        return scanner.nextLine().split(",");
    }

    /**
     * Method to obtain the queried node from the user
     *
     * @return query variable and its value
     */
    public String[] getQueriedNode() {
        System.out.println("Query:");
        return scanner.nextLine().split(":");

    }
}
