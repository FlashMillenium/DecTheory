package ru.spbstu.dtheory.task1;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        ArrayList<List<Double>> mainCriteriaTable = new ArrayList<>();
        mainCriteriaTable.add(Arrays.asList(0.008,  0.100,   0.5, 44000d, 500d, 2800000d, 0.3, 30d));
        mainCriteriaTable.add(Arrays.asList( 0.01, 0.0625, 0.125, 70000d, 700d, 3000000d, 0.8, 45d));
        mainCriteriaTable.add(Arrays.asList( 0.01, 0.1111,   0.2, 47000d, 500d, 2550000d, 0.2, 19d));
        mainCriteriaTable.add(Arrays.asList( 0.01,  0.125,  0.05, 49000d, 600d, 2600000d, 0.6, 20d));
        mainCriteriaTable.add(Arrays.asList(0.008,    0.2, 0.143, 45000d, 400d, 2500000d, 0.3, 13d));
        mainCriteriaTable.add(Arrays.asList(0.003,   0.25, 0.167, 80000d, 600d, 3300000d, 0.1, 92d));
        mainCriteriaTable.add(Arrays.asList(0.001,   0.75, 0.038, 85000d, 600d, 2500000d, 0.9, 11d));

        String[] nameCriteria = {"Смена" , "Час Пик", "Невское время", "Вечерний Петербург",
                "СПб ведомости", "Деловой Петербург", "Реклама - Шанс"};

        List<Double> restrictionList = Arrays.asList(0.01, 0.1, 0.038, 44000d, 400d, 2500000d, 0.3, 10d);

        System.out.println("Table for Main Criteria");
        mainCriteriaTable.forEach(System.out::println);

        List<List<Double>> matchingList = mainCriteriaTable.parallelStream().filter(s ->
                IntStream.range(0,s.size()).filter(i -> s.get(i) < restrictionList.get(i)).count() == 0)
                .collect(Collectors.toList());

        int index = IntStream.range(0, mainCriteriaTable.size()).filter(i -> mainCriteriaTable.get(i)
                .equals(matchingList.get(0))).findFirst().getAsInt();

        System.out.println("\nMatching lists for Main Criteria:");
//        matchingList.forEach(System.out::println); // win tuple
//        System.out.println("Index is " + index);  // index win tuple and namecriteria
//        System.out.println("Best newspaper for advertising: " + nameCriteria[index]);
        System.out.println("| " + index + " | " + nameCriteria[index] + " | " + matchingList.get(0) + " |");


        ArrayList<List<Double>> generalCriteriaTable = new ArrayList<>();
        generalCriteriaTable.add(Arrays.asList(10d, 2d,  3d, 2d, 2d, 4d));
        generalCriteriaTable.add(Arrays.asList( 4d, 7d,  3d, 4d, 3d, 1d));
        generalCriteriaTable.add(Arrays.asList( 6d, 8d,  3d, 5d, 4d, 2d));
        generalCriteriaTable.add(Arrays.asList( 9d, 2d,  3d, 2d, 1d, 3d));
        generalCriteriaTable.add(Arrays.asList( 5d, 1d, 0.1, 5d, 2d, 2d));
        generalCriteriaTable.add(Arrays.asList( 3d, 8d, 0.1, 2d, 4d, 1d));
        generalCriteriaTable.add(Arrays.asList( 3d, 5d,  4d, 2d, 7d, 7d));
        generalCriteriaTable.add(Arrays.asList( 3d, 6d,  3d, 4d, 4d, 1d));
        generalCriteriaTable.add(Arrays.asList( 2d, 5d,  3d, 1d, 5d, 7d));


        System.out.println("\nTable for General Criteria: ");
        generalCriteriaTable.forEach(System.out::println);

        List<Double> list =  DoubleStream.iterate(0.1, n -> n +0.1).limit(9).boxed().collect(Collectors.toList());

        List<HashMap<List<Double>, List<List<Double>>>> listalpha = Collections.synchronizedList(new ArrayList<>());
        // List<HashMap<List<Double>, List<List<Double>>>> listalpha =
        list.stream().flatMap(e1 -> list.stream()
                .flatMap(e2 -> list.stream()
                        .flatMap(e3 -> list.stream().
                                flatMap(e4 ->  list.stream()
                                        .map(e5 -> list.stream()
                                                .filter(e6 -> Math.abs(1d - (e6+e5+e4+e3+e2+e1)) < 0.00000001)
                                                .map(e6 -> Arrays.asList(e1, e2, e3, e4, e5, e6))
                                                .collect(Collectors.toList())
                                         )
                                )
                        )
                )
                )
                .filter(e -> !e.isEmpty())
                .map(e -> makeAlphaMap(generalCriteriaTable,e.get(0)))
                .filter(e -> checkListEquals(e.values().stream().findFirst().get()))
                .forEach(listalpha::add);

        HashMap<List<Double>, List<Double>> altHM = new HashMap<>();
        listalpha.forEach(e -> altHM.put(e.values().stream().findFirst().get().get(0)
                ,e.keySet().stream().findFirst().get()));


        HashMap<List<Double>, List<List<Double>>> altHMSecond = new HashMap<>();
        for(HashMap<List<Double>, List<List<Double>>> e: listalpha) {
            if(!altHMSecond.containsKey(e.values().stream().findFirst().get().get(0))) {
                altHMSecond.put(e.values().stream().findFirst().get().get(0)
                        , new ArrayList<>());
            }
            altHMSecond.get(e.values().stream().findFirst().get().get(0)).add(e.keySet().stream().findFirst().get());

        }


        System.out.println("\nAlpha and win tuple:");
//        listalpha.forEach(e -> System.out.println(e.keySet().stream().findFirst().get() + " =" +
//                " " + e.values().stream().findFirst().get().get(0))); // alpha -> win tuple
//        altHM.entrySet().forEach(System.out::println);  // one win tuple -> one alpha
//        System.out.println(listalpha.get(0 /* can be random number between 0 and listalpha.size*/)); // simple random output
          altHMSecond.entrySet().forEach(System.out::println); // one win tuple -> several alpha
    }


    private static boolean checkListEquals(List<List<Double>> list){
        List<Double> value = list.get(0);
        return list.stream().filter(e-> value.equals(e)).count() == list.size();
    }

    private static HashMap<List<Double> , List<List<Double>>> makeAlphaMap
            (ArrayList<List<Double>> generalCriteriaTable, List<Double> alphaList){
        HashMap<List<Double> , List<List<Double>>> result = new HashMap<>();

        HashMap<Double, List<Double>> addFolging = additiveFolging(generalCriteriaTable, alphaList);
        HashMap<Double, List<Double>> mulFolging = multiFolging(generalCriteriaTable,alphaList);
        HashMap<Double, List<Double>> mFolging = minFolging(generalCriteriaTable,alphaList);
        HashMap<Double, List<Double>> mxFolging = maxFolging(generalCriteriaTable,alphaList);

        List<List<Double>> entry = new ArrayList<>();

        entry.add(addFolging.values().stream().findFirst().get());
        entry.add(mulFolging.values().stream().findFirst().get());
        entry.add(mFolging.values().stream().findFirst().get());
        entry.add(mxFolging.values().stream().findFirst().get());

        result.put(alphaList, entry);

        return result;
    }
    private static HashMap<Double, List<Double>> additiveFolging
            (ArrayList<List<Double>> generalCriteriaTable, List<Double> alphaList){

        return generalCriteriaTable.stream().map(e -> makeAddMap(e,alphaList))
                .reduce((i,j) -> i.keySet().stream().findFirst().get() >
                        j.keySet().stream().findFirst().get() ? i : j )
                .get();

    }

    private static HashMap<Double, List<Double>> makeAddMap
            (List<Double> genCriteriaTuple,List<Double> alphaList){

        HashMap<Double, List<Double>> result = new HashMap<>();

        result.put(IntStream.range(0,genCriteriaTuple.size()).
                        mapToObj(i -> genCriteriaTuple.get(i)*alphaList.get(i))
                        .reduce((i,j) -> i+j)
                        .get()
                ,genCriteriaTuple);

        return result;
    }

    private static HashMap<Double, List<Double>> maxFolging
            (ArrayList<List<Double>> generalCriteriaTable, List<Double> alphaList){

        return generalCriteriaTable.stream().map(e -> makeMaxMap(e,alphaList))
                .reduce((i,j) -> i.keySet().stream().findFirst().get() >
                        j.keySet().stream().findFirst().get() ? i : j)
                .get();
    }

    private static HashMap<Double, List<Double>> makeMaxMap
            (List<Double> genCriteriaTuple,List<Double> alphaList) {

        HashMap<Double, List<Double>> result = new HashMap<>();

        result.put(IntStream.range(0,genCriteriaTuple.size())
                        .mapToObj(i -> genCriteriaTuple.get(i)*alphaList.get(i))
                        .max(Double::compareTo)
                        .get()
                ,genCriteriaTuple);

        return result;
    }

    private static HashMap<Double, List<Double>> minFolging
            (ArrayList<List<Double>> generalCriteriaTable, List<Double> alphaList){

        return generalCriteriaTable.stream().map(e -> makeMinMap(e,alphaList))
                .reduce((i,j)->i.keySet().stream().findFirst().get() >
                        j.keySet().stream().findFirst().get() ? i : j)
                .get();
    }
    private static HashMap<Double, List<Double>> makeMinMap
            (List<Double> genCriteriaTuple,List<Double> alphaList) {

        HashMap<Double, List<Double>> result = new HashMap<>();

        result.put(IntStream.range(0,genCriteriaTuple.size()).
                        mapToObj(i -> genCriteriaTuple.get(i)/alphaList.get(i))
                        .min(Double::compareTo)
                        .get()
                ,genCriteriaTuple);

        return result;
    }


    private static HashMap<Double, List<Double>> multiFolging
            (ArrayList<List<Double>> generalCriteriaTable, List<Double> alphaList){

        return generalCriteriaTable.stream().map(e -> makeMultMap(e, alphaList))
                .reduce((i,j)->i.keySet().stream().findFirst().get() >
                        j.keySet().stream().findFirst().get() ? i : j)
                .get();
    }
    private static HashMap<Double, List<Double>> makeMultMap
            (List<Double> genCriteriaTuple,List<Double> alphaList){

        HashMap<Double, List<Double>> result = new HashMap<>();

        result.put(IntStream.range(0,genCriteriaTuple.size())
                        .mapToObj(i -> Math.pow(genCriteriaTuple.get(i),alphaList.get(i)))
                        .reduce((i,j) -> i*j)
                        .get()
                ,genCriteriaTuple);

        return result;
    }



}
