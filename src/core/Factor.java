package core;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factor Class.
 * This class stores the probability distribution table of a set of nodes.
 *
 * @author 210032207
 * @version 1.0.0
 * @since 15/03/2022
 */
@SuppressWarnings("StringConcatenationInLoop")
public class Factor {
    // using a tree map to maintain ordering based on intsertion into the map
    private final Map<String, Double> cpt = new TreeMap<>();
    private final Set<Node> randomVariables = new LinkedHashSet<>();

    /**
     * Constructor specifying the node.
     *
     * @param node node
     */
    public Factor(Node node) {
        randomVariables.addAll(node.getParents());
        randomVariables.add(node);
    }

    /**
     * Constructor specifying the list of random variables to use in the probability table.
     *
     * @param randomVariables set of random variable nodes to use in the factor
     */
    public Factor(Set<Node> randomVariables) {
        this.randomVariables.addAll(randomVariables);
    }

    /**
     * Gets the probability table.
     *
     * @return probability table
     */
    public Map<String, Double> getCpt() {
        return cpt;
    }

    /**
     * Gets the random variables used in the probability table.
     *
     * @return random variable nodes
     */
    public Set<Node> getOrderedVariables() {
        return randomVariables;
    }

    /**
     * Fills the probability table with probability values.
     *
     * @param values probabilities
     */
    public void addValues(double... values) {
        int noOfVariables = getOrderedVariables().size();
        int tableSize = (int) Math.pow(2, noOfVariables);
        // assert that table size is equal to the number of values provided
        if (values.length == tableSize) {
            for (int i = 0; i < tableSize; i++) {
                String key = expandBinary(Integer.toBinaryString(i), noOfVariables);
                cpt.put(key, values[i]);
            }
        }
    }

    /**
     * Generates a list of truth table combinations for the set of random variables
     * used to generate the probability table.
     *
     * @return truth table combinations
     */
    public List<boolean[]> truthTableCombinations() {
        int noOfVariables = getOrderedVariables().size();
        int tableSize = (int) Math.pow(2, noOfVariables);
        List<boolean[]> combinations = new ArrayList<>();
        for (int i = 0; i < tableSize; i++) {
            String key = expandBinary(Integer.toBinaryString(i), noOfVariables);
            char[] keyCharacters = key.toCharArray();
            boolean[] combination = new boolean[noOfVariables];
            for (int i1 = 0; i1 < keyCharacters.length; i1++) {
                combination[i1] = keyCharacters[i1] == '1';
            }
            combinations.add(combination);
        }
        return combinations;
    }

    /**
     * Expand a binary string to a certain length.
     *
     * @param binaryString binary string to expand
     * @param size         required size of the string
     * @return expanded binary string
     */
    private String expandBinary(String binaryString, int size) {
        if (size > binaryString.length()) {
            return "0".repeat(size - binaryString.length()) + binaryString;
        }
        return binaryString;
    }

    /**
     * Gets the probability table key representation of the list of values for the set of random variables used.
     *
     * @param nodeValues values for the random variables used
     * @return probability table key
     */
    public String getKey(boolean[] nodeValues) {
        String key = "";
        if (nodeValues.length == getOrderedVariables().size()) {
            for (int i = 0; i < nodeValues.length; i++) {
                key += (nodeValues[i]) ? "1" : "0";
            }
        }
        return key;
    }

    /**
     * Represents a probability table key as an array of values for the set of random variables used.
     *
     * @param key probability table key
     * @return array of values for the set of random variables used
     */
    public boolean[] keyToBoolean(String key) {
        boolean[] booleanKey = new boolean[key.length()];
        char[] keyArray = key.toCharArray();
        for (int i = 0; i < keyArray.length; i++) {
            booleanKey[i] = keyArray[i] == '1';
        }
        return booleanKey;
    }

    /**
     * Gets the occurrence probability of a set of values of the random variable used.
     *
     * @param nodeLabelValueMap map of random variable labels and their values
     * @return probability of the set of provided values of the random variables occurring
     */
    public double get(Map<String, Boolean> nodeLabelValueMap) {
        String key = "";
        for (Node orderedVariable : getOrderedVariables()) {
            key += (nodeLabelValueMap.get(orderedVariable.getLabel())) ? "1" : "0";
        }
        return cpt.get(key);
    }

    /**
     * Generates a copy of a factor.
     *
     * @return coopy of a factor
     */
    public Factor copy() {
        Factor factor = new Factor(getOrderedVariables());
        getCpt().forEach((key, prob) -> factor.assignProbability(keyToBoolean(key), prob));
        return factor;

    }

    /**
     * Normalizes the values in the probability table.
     */
    public void normalize() {
        // we only normalize a prior distribution, where only one r.v. exists
        if (randomVariables.size() == 1) {
            double total = cpt.values().stream().reduce(0.0, Double::sum);
            cpt.forEach((key, probability) -> assignProbability(keyToBoolean(key), probability / total));
        }
    }

    /**
     * Sets a probability for an event occurring.
     *
     * @param values array of values for the random variables used
     * @param prob   probability of the event occurring
     */
    public void assignProbability(boolean[] values, double prob) {
        if (values.length == getOrderedVariables().size()) {
            cpt.put(getKey(values), prob);
        }
    }

    /**
     * Checks of a random variable node is included in the factor
     *
     * @param otherNode random variable node
     * @return True if node exists in the factor
     */
    public boolean includes(Node otherNode) {
        return getOrderedVariables().contains(otherNode);
    }

    /**
     * Joins another factor using point wise product
     *
     * @param other other factor
     * @return joined factor
     */
    public Factor join(Factor other) {
        Set<Node> f1Variables = this.getOrderedVariables();
        Set<Node> f2Variables = other.getOrderedVariables();

        // all nodes v4= union of variables in both factors
        Set<Node> v4 = new LinkedHashSet<>(f1Variables);
        v4.addAll(f2Variables);
        Factor f3 = new Factor(v4);
        // generate f3 values for every combination of the
        for (boolean[] combination : f3.truthTableCombinations()) {
            // map combination to a label
            Map<String, Boolean> labelValueMap = f3.generateQueryMap(combination);
            // get the prob for both f1 and f2 and set that as the value for f3
            f3.assignProbability(combination, this.get(labelValueMap) * other.get(labelValueMap));
        }
        return f3;
    }

    /**
     * Removes a random variable from a factor by marginalization.
     *
     * @param randomVariableToRemove random variable node
     * @return factor excluding the random variable
     */
    public Factor sumOut(Node randomVariableToRemove) {
        // make a new factor that doesnt include the label you want to remove
        Set<Node> newVariables = new LinkedHashSet<>(this.getOrderedVariables());
        newVariables.remove(randomVariableToRemove);
        Factor f4 = new Factor(newVariables);
        // sum where random variable == true and when == false
        List<Boolean> booleanList = List.of(true, false);
        for (boolean[] combination : f4.truthTableCombinations()) {
            double sum = 0;
            for (Boolean booleanValue : booleanList) {
                Map<String, Boolean> labelValueMap = f4.generateQueryMap(combination);
                // add left out node
                labelValueMap.put(randomVariableToRemove.getLabel(), booleanValue);
                // get probability and add to the sum
                sum += get(labelValueMap);
            }
            f4.assignProbability(combination, sum);
        }
        return f4;
    }

    /**
     * Projects the probability of an event for a random variable to 0
     *
     * @param node  random variable node
     * @param value random variable value
     */
    public void projectToZero(Node node, boolean value) {
        for (boolean[] combination : truthTableCombinations()) {
            Map<String, Boolean> labelValueMap = generateQueryMap(combination);
            if (labelValueMap.get(node.getLabel()) == value) {
                assignProbability(combination, 0.0);
            }
        }
    }

    /**
     * Generate a mapping of random variables label to their value.
     *
     * @param values values for the random variables used
     * @return random variables and value mapping
     */
    public Map<String, Boolean> generateQueryMap(boolean[] values) {
        Map<String, Boolean> labelValueMap = new HashMap<>();
        Node[] orderedNodes = getOrderedVariables().toArray(Node[]::new);
        // both combinations and order nodes are the same size
        // so get a mapping of node label to value
        for (int i = 0; i < values.length; i++) {
            labelValueMap.put(orderedNodes[i].getLabel(), values[i]);
        }
        return labelValueMap;
    }

    /**
     * Generates the label for the factor
     *
     * @return factor label
     */
    public String getFactorLabel() {
        // separate node from its parents
        String label;
        Node[] randomVariablesArray = randomVariables.toArray(Node[]::new);
        if (randomVariablesArray.length == 1) {
            label = randomVariablesArray[0].getLabel();
        } else {
            label = randomVariablesArray[randomVariablesArray.length - 1].getLabel() + "|";
            label += Arrays
                    .stream(Arrays.copyOfRange(randomVariablesArray, 0, randomVariablesArray.length - 1))
                    .map(Node::getLabel).collect(Collectors.joining(","));
        }
        return "P(" + label + ")";


    }

    /**
     * Prints the probability table.
     */
    public void logCPTValues() {
        // get table header
        randomVariables.forEach(x -> System.out.print(x.getLabel() + " "));
        System.out.print("| ");
        System.out.println(getFactorLabel());

        // get table values
        cpt.forEach((key, probability) -> {
            for (char c : key.toCharArray()) {
                System.out.print((c == '1') ? "T " : "F ");
            }
            System.out.print("| ");
            System.out.print(probability);
            System.out.println();
        });
    }
}
