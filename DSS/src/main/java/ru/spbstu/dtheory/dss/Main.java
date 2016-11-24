package ru.spbstu.dtheory.dss;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;

public class Main {
    public static void main(String[] args) {
        List<List<List<Integer>>> bRelationList = getBinaryRelationMatrix();
        List<String> itemName = getItemName();
        List<String> parameterName = getParameterName();
        List<Double> weightCoefficient = getWeightCoefficient();

        if(weightCoefficient.size() != bRelationList.size())
            throw new IllegalArgumentException("Size Weight Coefficient and number Binary Relation tables must be" +
                    " the same, but Weight Coefficien is " + weightCoefficient.size() + " and Binary Relation table is " + bRelationList.size());

        List<Pair> dominationWinList = dominationSelect(bRelationList);

        List<Pair> dominationResult = getResultSortedPairs(dominationWinList);


        System.out.println("For domination criteria\n");
        dominationResult.forEach(e -> System.out.println("In " + itemName.get(e.getKey())
                + " win count: " + e.getValue()));
//        IntStream.range(0, dominationWinList.size())
//                .forEach(e -> System.out.println("Variant "+ parameterName.get(e) + " binary relation\n" +
//                        "Win " + itemName.get(dominationWinList.get(e).getKey())
//                  + " with sum " + dominationWinList.get(e).getValue()));

        List<Pair> blockingWinList = blockingSelect(bRelationList);
        List<Pair> blockingResult = getResultSortedPairs(blockingWinList);

        System.out.println("\n\nFor blocking criteria\n");
        blockingResult.forEach(e -> System.out.println("In " + itemName.get(e.getKey())
                + " win count: " + e.getValue()));
//        range(0, blockingWinList.size())
//                .forEach(e->System.out.println("In "+ parameterName.get(e) + " binary relation\n" +
//                        "Win " + itemName.get(blockingWinList.get(e).getKey()) +
//                        " with sum " + blockingWinList.get(e).getValue()));


        List<List<Double>> tourSumInBinaryRelationElement = getTableOfTournamentSum(bRelationList);
         Map<Double, Integer> tournamentResult = getTournamentResult(weightCoefficient, tourSumInBinaryRelationElement);

        System.out.println("\n\nFor Tornament criteria\n");
        tournamentResult.entrySet().stream()
                .forEachOrdered(e-> System.out.println("For "+ itemName.get(e.getValue()) +
                        "sum is " + e.getKey()));
        System.out.println(tournamentResult);

        List<List<Pair>> binaryAlternativeSum =  calcBinRelationAlternativeSum(bRelationList);
        List<List<Pair>> kMax = getKMaxValue(binaryAlternativeSum); //index is a number of k-max
        List<List<Pair>> kMaxSort = getKMaxPosition(kMax);

        System.out.println("\n\nFor k-Max kriteria");
        IntStream.range(0, kMax.get(0).size()).forEach(e ->
                System.out.println("For " + itemName.get(e)
                + " position on kmax-1 =" + kMax.get(0).get(e).getValue()
                        + " kmax-2 = " + kMax.get(1).get(e).getValue()
                + " kmax-3 = " + kMax.get(2).get(e).getValue()
                + " kmax-4 = " + kMax.get(3).get(e).getValue()));
    }

    private static List<List<Pair>> getKMaxPosition(List<List<Pair>> kMax) {
        List<List<Pair>> kMaxSortSum = kMax.stream().map(kMaxTable ->
        kMaxTable.stream().sorted(comparingInt(Pair::getValue).reversed())
                .collect(Collectors.toList())).collect(Collectors.toList());

        kMaxSortSum.stream().forEachOrdered(kMaxTable ->
        range(0,kMaxTable.size()).boxed().forEachOrdered(e -> kMaxTable.get(e).setValue(e+1)));

        List<List<Pair>> kMaxSort = kMaxSortSum.stream().map(kMaxTable ->
                kMaxTable.stream().sorted(comparingInt(Pair::getKey))
                        .collect(Collectors.toList())).collect(Collectors.toList());

        return kMaxSort;
    }

    private static List<List<Pair>> getKMaxValue(List<List<Pair>> binaryAlternativeSum) {
        List<List<Pair>> kMax = new ArrayList<>();

        List<Pair> sr1 = range(0,binaryAlternativeSum.get(0).size()).boxed()
                .map(binaryVariant ->
                new Pair(binaryVariant,
                        binaryAlternativeSum.stream()
                                .mapToInt(bAlternative-> bAlternative.get(binaryVariant).getValue()).sum()
                        )).collect(Collectors.toList());
        kMax.add(sr1);
        List<Pair> sr2 = range(0,binaryAlternativeSum.get(0).size()).boxed()
                .map(binaryVariant ->
                        new Pair(binaryVariant,
                                of(0,2).map(bAlternative-> binaryAlternativeSum.get(bAlternative)
                                                            .get(binaryVariant).getValue()).sum())
                        ).collect(Collectors.toList());
        kMax.add(sr2);
        List<Pair> sr3 = range(0,binaryAlternativeSum.get(0).size()).boxed()
                .map(binaryVariant ->
                        new Pair(binaryVariant,
                                of(0,1).map(bAlternative-> binaryAlternativeSum.get(bAlternative)
                                                            .get(binaryVariant).getValue()).sum())
                ).collect(Collectors.toList());
        kMax.add(sr3);
        List<Pair> sr4 = range(0,binaryAlternativeSum.get(0).size()).boxed()
                .map(binaryVariant ->
                        new Pair(binaryVariant,
                                of(0).map(bAlternative-> binaryAlternativeSum.get(bAlternative)
                                                            .get(binaryVariant).getValue()).sum())
                ).collect(Collectors.toList());
        kMax.add(sr4);
        return kMax;
    }

    /**
     * @param bRelationList Table of Binary Relation Result
     * @return 3 List's of Pair List. In order 1 - Hr (whem x->y) , 2 - Er (when x->y and y->x),
     * 3 - Nr(when x not -> y and y not -> x)
     */
    private static List<List<Pair>> calcBinRelationAlternativeSum(List<List<List<Integer>>> bRelationList) {
        List<List<Pair>> result = new ArrayList<>();

        List<Pair> Hr = range(0, bRelationList.get(0).size()).boxed()
                .map(binaryVariant -> new Pair(binaryVariant,range(0, bRelationList.size())
                        .boxed()
                        .map(binRelationElement -> Stream.concat(
                                range(0,binaryVariant).boxed(),
                                range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                .map(binaryElement -> {
                                    if(bRelationList.get(binRelationElement).get(binaryVariant).get(binaryElement)==1 &&//x
                                        bRelationList.get(binRelationElement).get(binaryElement).get(binaryVariant)==0)//y
                                            return 1;
                                        else return 0;
                                })
                                .collect(Collectors.summingInt(Integer::intValue))
                        ).collect(Collectors.summingInt(Integer::intValue)))).collect(Collectors.toList());
        result.add(Hr);

        List<Pair> Er = range(0, bRelationList.get(0).size()).boxed()
                .map(binaryVariant -> new Pair(binaryVariant,range(0, bRelationList.size())
                        .boxed()
                        .map(binRelationElement -> Stream.concat(
                                range(0,binaryVariant).boxed(),
                                range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                .map(binaryElement -> {
                                    if(bRelationList.get(binRelationElement).get(binaryVariant).get(binaryElement)==1 &&//x
                                            bRelationList.get(binRelationElement).get(binaryElement).get(binaryVariant)==1)//y
                                        return 1;
                                    else return 0;
                                })
                                .collect(Collectors.summingInt(Integer::intValue))
                        ).collect(Collectors.summingInt(Integer::intValue)))).collect(Collectors.toList());
        result.add(Er);

        List<Pair> Nr = range(0, bRelationList.get(0).size()).boxed()
                .map(binaryVariant -> new Pair(binaryVariant,range(0, bRelationList.size())
                        .boxed()
                        .map(binRelationElement -> Stream.concat(
                                range(0,binaryVariant).boxed(),
                                range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                .map(binaryElement -> {
                                    if(bRelationList.get(binRelationElement).get(binaryVariant).get(binaryElement)==0 &&//x
                                            bRelationList.get(binRelationElement).get(binaryElement).get(binaryVariant)==0)//y
                                        return 1;
                                    else return 0;
                                })
                                .collect(Collectors.summingInt(Integer::intValue))
                        ).collect(Collectors.summingInt(Integer::intValue)))).collect(Collectors.toList());
        result.add(Nr);
        return result;
    }

    private static Map<Double, Integer> getTournamentResult(List<Double> weightCoefficient, List<List<Double>> tourSumInBinaryRelationElement) {
        List<Double> tournamentResult = range(0, tourSumInBinaryRelationElement.size()).boxed()
                .map(binaryVariant ->
                range(0, tourSumInBinaryRelationElement.get(binaryVariant).size())//.boxed()
                        .mapToDouble(e ->
                                tourSumInBinaryRelationElement.get(binaryVariant).get(e)*weightCoefficient.get(e))
                        .sum())
                .collect(Collectors.toList());
        Map<Double, Integer> tournamentRes = new TreeMap<>(Collections.reverseOrder());
        range(0, tournamentResult.size()).forEach(e-> tournamentRes.put(tournamentResult.get(e),e));
        return tournamentRes;
    }

    private static List<List<Double>> getTableOfTournamentSum(List<List<List<Integer>>> bRelationList) {
        return range(0, bRelationList.get(0).size()).boxed()
            .map(binaryVariant -> range(0, bRelationList.size())
                            .boxed()
                            .map(binRelationElement -> Stream.concat(
                                    range(0,binaryVariant).boxed(),
                                    range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                    .map(binaryElement -> {
                                        if(bRelationList.get(binRelationElement).get(binaryVariant).get(binaryElement)==1)//x
                                            if(bRelationList.get(binRelationElement).get(binaryElement).get(binaryVariant)==1)//y
                                                return 0.5d;
                                            else return 1d;
                                        else return 0d;
                                        })
                                    .collect(Collectors.summingDouble(Double::doubleValue))
                            ).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    private static List<Pair> getResultSortedPairs(List<Pair> winPairList) {
        Map<Integer, Long> blockingResultMap = winPairList.stream()
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.counting()));

        return blockingResultMap.keySet().stream()
                .map(e -> new Pair(e, blockingResultMap.get(e).intValue()))
                .sorted((o1,o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                .collect(Collectors.toList());
    }

    private static List<Pair> dominationSelect(List<List<List<Integer>>> bRelationList) {
        return range(0, bRelationList.size()).boxed()
                .map(binRelationElement -> range(0,bRelationList.get(binRelationElement).size()).boxed()
                        .map(binaryVariant ->
                                new Pair(binaryVariant,
                                        Stream.concat(
                                                range(0, binaryVariant).boxed(),
                                                range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                        .mapToInt(e->bRelationList.get(binRelationElement).get(binaryVariant).get(e))
                                                .sum()))//.peek(System.out::println)
                        .max(comparingInt(Pair::getValue)).orElse(new Pair(-1, 0)))
                .collect(Collectors.toList());
    }

    private static List<Pair> blockingSelect(List<List<List<Integer>>> bRelationList) {
        return range(0, bRelationList.size()).boxed()
                .map(binRelationElement -> range(0,bRelationList.get(binRelationElement).size()).boxed()
                        .map(binaryVariant ->
                                new Pair(binaryVariant,
                                        Stream.concat(
                                                range(0, binaryVariant).boxed(),
                                                range(binaryVariant+1, bRelationList.get(binRelationElement).get(binaryVariant).size()).boxed())
                                                .mapToInt(e->bRelationList.get(binRelationElement).get(e).get(binaryVariant))
                                                .sum()))//.peek(System.out::println)
                        .min(comparingInt(Pair::getValue)).orElse(new Pair(-1, 0)))
                .collect(Collectors.toList());
    }

    private static List<String> getParameterName() {
        return Arrays.asList("Price", "CPU", "RAM frequency", "VGA", "Disks", "Weight");
    }

    private static List<String> getItemName() {
        return Arrays.asList("Lenovo thinkpad Edge E560", "Dell XPS 15 9550",
                "Hp EliteBook 755 G3(V1A66EA)", "ASUS Zenbook UX305CA",
                "MSI GP72 2QE Leopard Pro");
    }


    private static List<Double> getWeightCoefficient(){
        return Arrays.asList(0.2,0.15,0.1,0.15,0.2,0.2);
    }

    @NotNull
    private static List<List<List<Integer>>> getBinaryRelationMatrix() {
        List<List<List<Integer>>> result = new ArrayList<>();
        //add Price Relation Matrix less is better
        result.add(Arrays.asList(
                Arrays.asList(0, 1, 1, 1, 1),
                Arrays.asList(0, 0, 0, 0, 0),
                Arrays.asList(0, 1, 0, 0, 1),
                Arrays.asList(0, 1, 0, 0, 0),
                Arrays.asList(0, 1, 1, 1, 0)
        ));
        //add CPU Relation Matrix based on points in cpuboss.com
        result.add(Arrays.asList(
                Arrays.asList(0, 1, 0, 1, 1),
                Arrays.asList(0, 0, 0, 0, 0),
                Arrays.asList(1, 1, 0, 0, 1),
                Arrays.asList(1, 1, 0, 0, 1),
                Arrays.asList(0, 1, 0, 0, 0)
        ));
        //add RAM Relation Matrix based on it frequency
        result.add(Arrays.asList(
                Arrays.asList(0, 0, 1, 0, 1),
                Arrays.asList(1, 0, 1, 1, 1),
                Arrays.asList(1, 0, 0, 0, 1),
                Arrays.asList(1, 0, 1, 0, 1),
                Arrays.asList(1, 0, 1, 0, 0)
        ));
        //add GRU Relation Matrix based on points in gpuboss.com
        result.add(Arrays.asList(
                Arrays.asList(0, 0, 1, 1, 0),
                Arrays.asList(1, 0, 1, 1, 1),
                Arrays.asList(0, 0, 0, 1, 0),
                Arrays.asList(0, 0, 0, 0, 0),
                Arrays.asList(1, 0, 1, 1, 0)
        ));
        //add Hard Drive Relation Matrix
        result.add(Arrays.asList(
                Arrays.asList(0, 0, 0, 0, 1),
                Arrays.asList(1, 0, 0, 0, 1),
                Arrays.asList(1, 1, 0, 0, 1),
                Arrays.asList(1, 1, 1, 0, 1),
                Arrays.asList(1, 0, 0, 0, 0)
        ));
        //add Weight Relation Matrix less is better
        result.add(Arrays.asList(
                Arrays.asList(0, 0, 0, 0, 1),
                Arrays.asList(1, 0, 0, 0, 1),
                Arrays.asList(1, 1, 0, 0, 1),
                Arrays.asList(1, 1, 1, 0, 1),
                Arrays.asList(0, 0, 0, 0, 0)
        ));

        return result;
    }
}

class Pair {
    private int Key;
    private int Value;

    Pair(int key, int value) {
        Key = key;
        Value = value;
    }

    int getKey() {

        return Key;
    }

    public void setKey(int key) {
        Key = key;
    }

    int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "Key=" + Key +
                ", Value=" + Value +
                '}';
    }
}