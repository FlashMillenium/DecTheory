import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;

/**
 *
 */
class SolutionOnCriteria {

    static List<Integer> minMaxCriteria(List<List<Integer>> decisionTable) {
        return decisionTable.stream().parallel()
                .max(Comparator.comparingInt(p -> p.stream().min(Integer::compareTo).get()))
                .orElse(emptyList());
    }

    static List<Integer> savageCriteria(List<List<Integer>> decisionTable) {

        Integer[] maxArray = decisionTable.stream()
                .map((s) -> s.stream().max(Integer::compareTo).get())
                .toArray(Integer[]::new);

        ArrayList<List<Integer>> invertTable = new ArrayList<>();

        IntStream.range(0, maxArray.length)
                .forEachOrdered(s -> invertTable.add(
                        s,
                        decisionTable.get(s)
                                .stream()
                                .map(e -> maxArray[s] - e)
                                .collect(Collectors.toList()))
                );

        return decisionTable.stream()
                .filter(s -> s.contains(
                        IntStream.range(0, maxArray.length).parallel()
                                .mapToObj(i -> decisionTable.get(i)
                                        .stream()
                                        .map(e -> (maxArray[i] - e) == 0
                                                ? maxArray[i]
                                                : maxArray[i] - e)
                                        .max(Integer::compareTo)
                                        .get())
                                .min(Integer::compareTo)
                                .get()))
                .reduce((i, j) -> i.stream().max(Integer::compareTo).get() > j.stream().max(Integer::compareTo).get()
                        ? j
                        : i)
                .orElse(emptyList());
    }

    static List<Integer> hurwitzCriteria(List<List<Integer>> decisionTable, double c) {
        Winner mapHW = IntStream.range(0, decisionTable.size())
                .mapToObj((i) -> makeHwMap(i, decisionTable, c))
                .max(comparingDouble(Winner::getCoefficient))
                .orElseThrow(IllegalStateException::new);

        return decisionTable.get(mapHW.getIndex());
    }

    static List<Integer> bayesLaplasCriteria(List<List<Integer>> decisionTable, List<Double> probabilityList) {

        Winner mapBL = IntStream.range(0, decisionTable.size()).parallel()
                .mapToObj((i) -> makeBlMap(i, decisionTable, probabilityList))
                .max(comparingDouble(Winner::getCoefficient))
                .orElseThrow(IllegalStateException::new);

        return decisionTable.get(mapBL.getIndex());
    }

    private static Winner makeHwMap(int n, List<List<Integer>> decisionTable, double c) {
        if (c < 0 || c > 1) throw new IllegalArgumentException("Argument c must be between 0 and 1, but c is " + c);
        return new Winner(
                c * decisionTable.get(n).stream().max(Integer::compareTo).get() +
                        (1 - c) * decisionTable.get(n).stream().min(Integer::compareTo).get(),
                n);
    }

    private static Winner makeBlMap(int n, List<List<Integer>> decisionTable, List<Double> probabilityList) {
        if (decisionTable.get(0).size() != probabilityList.size())
            throw new IllegalArgumentException("Size List's in decision table not equal size probability table");
        return new Winner(
                IntStream.range(0, decisionTable.get(n).size())
                        .boxed()
                        .mapToDouble((e) -> decisionTable.get(n).get(e) * probabilityList.get(e))
                        .sum(),
                n
        );
    }

    private static class Winner {

        private int index;
        private double coefficient;

        public Winner() {
        }

        public Winner(double coefficient, int index) {
            this.coefficient = coefficient;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(double coefficient) {
            this.coefficient = coefficient;
        }
    }


}
