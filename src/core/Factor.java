package core;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factor Class.
 * This class denotes a probability distribution table.
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

    public Factor(Node node) {
        randomVariables.addAll(node.getParents());
        randomVariables.add(node);
    }

    public Map<String, Double> getCpt() {
        return cpt;
    }

    public Factor(Set<Node> randomVariables) {
        this.randomVariables.addAll(randomVariables);
    }

    public Set<Node> getOrderedVariables() {
        return randomVariables;
    }

    public void addValues(double... vals) {
        int noOfVariables = getOrderedVariables().size();
        int tableSize = (int) Math.pow(2, noOfVariables);
        // assert that table size is equal to the number of values provided
        if (vals.length == tableSize) {
            for (int i = 0; i < tableSize; i++) {
                String key = expandBinary(Integer.toBinaryString(i), noOfVariables);
                cpt.put(key, vals[i]);
            }
        }
    }

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

    private String expandBinary(String binaryString, int size) {
        if (size > binaryString.length()) {
            return "0".repeat(size - binaryString.length()) + binaryString;
        }
        return binaryString;
    }

    public String getKey(boolean[] nodeValues) {
        String key = "";
        if (nodeValues.length == getOrderedVariables().size()) {
            for (int i = 0; i < nodeValues.length; i++) {
                key += (nodeValues[i]) ? "1" : "0";
            }
        }
        return key;
    }

    public boolean[] keyToBoolean(String key) {
        boolean[] booleanKey = new boolean[key.length()];
        char[] keyArray = key.toCharArray();
        for (int i = 0; i < keyArray.length; i++) {
            booleanKey[i] = keyArray[i] == '1';
        }
        return booleanKey;
    }

    public double get(Map<String, Boolean> nodeLabelValueMap) {
        String key = "";
        for (Node orderedVariable : getOrderedVariables()) {
            key += (nodeLabelValueMap.get(orderedVariable.getLabel())) ? "1" : "0";
        }
        return cpt.get(key);
    }

    public Factor copy() {
        Factor factor = new Factor(getOrderedVariables());
        getCpt().forEach((key, prob) -> factor.assignProbability(keyToBoolean(key), prob));
        return factor;

    }


    public void assignProbability(boolean[] values, double prob) {
        if (values.length == getOrderedVariables().size()) {
            cpt.put(getKey(values), prob);
        }
    }


    public boolean includes(Node otherNode) {
        return getOrderedVariables().contains(otherNode);
    }

    public Factor join(Factor other) {
        Set<Node> f1Variables = this.getOrderedVariables();
        Set<Node> f2Variables = other.getOrderedVariables();

        // get nodes that are in both f1 and f2
//        Set<Node> v1 = new HashSet<>(f1Variables);
//        v1.retainAll(f2Variables);

//        // get nodes only in f1
//        Set<Node> v2 = new HashSet<>(f1Variables);
//        v2.removeAll(f2Variables);
//
//        // get nodes only in f2
//        Set<Node> v3 = new HashSet<>(f2Variables);
//        v2.removeAll(f1Variables);

        // all nodes v4= union of variables in both factors
        Set<Node> v4 = new HashSet<>(f1Variables);
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

    public Factor sumOut(Node randomVariableToRemove) {
        // make a new factor that doesnt include the label you want to remove
        Set<Node> newVariables = new HashSet<>(this.getOrderedVariables());
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

    public Map<String, Boolean> generateQueryMap(boolean[] values) {
        Map<String, Boolean> labelValueMap = new HashMap<>();
        Node[] orderedNodes = getOrderedVariables().toArray(Node[]::new);
        // both combinations and ordernodes are the same size
        // so get a mapping of node label to value
        for (int i = 0; i < values.length; i++) {
            labelValueMap.put(orderedNodes[i].getLabel(), values[i]);
        }
        return labelValueMap;
    }

    private String getFactorLabel() {
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

    public void logCPTValues() {
        // get table header
        randomVariables.forEach(x -> System.out.print(x.getLabel() + " "));
        System.out.print("| ");
        System.out.println(getFactorLabel());

        // get table values
        cpt.forEach((key, probability) -> {
            for (char c : key.toCharArray()) {
                System.out.print(c + " ");
            }
            System.out.print("| ");
            System.out.print(probability);
            System.out.println();
        });
    }


}
