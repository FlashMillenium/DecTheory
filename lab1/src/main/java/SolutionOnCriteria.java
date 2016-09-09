import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class SolutionOnCriteria {

    public static List<Integer> minMaxCriteria(List<List<Integer>> decisionTable){

        List result = decisionTable.stream().parallel()
                                    .max((p1, p2) ->
                                            Integer.compare(
                                                    p1.stream().min(Integer::compareTo).get()
                                                   ,p2.stream().min(Integer::compareTo).get()))
                                    .get();
        return  result;

    }

    public static List<Integer> savageCriteria(List<List<Integer>> decisionTable){

        Integer[] maxArray = decisionTable.stream()
                .map((s) -> s.stream().max(Integer::compareTo).get())
                .toArray(Integer[]::new);

        ArrayList<List<Integer>> invertTable = new ArrayList<>();
        IntStream.range(0, maxArray.length).parallel()
                .forEachOrdered((s) -> invertTable.add(s ,decisionTable.get(s)
                        .stream()
                        .map((e) -> e = maxArray[s] - e).collect(Collectors.toList())));

        List result = decisionTable.stream().parallel()
                .filter((s) ->s.contains(
                        IntStream.range(0, maxArray.length).parallel()
                                .mapToObj((i) ->
                                        decisionTable.get(i).stream()
                                                .map((e) ->
                                                        e = (maxArray[i] - e)==0? maxArray[i] : maxArray[i] - e)
                                                .max(Integer::compareTo).get())
                                .min(Integer::compareTo).get()))
                .reduce((i,j) -> i.stream().max(Integer::compareTo).get() >
                        j.stream().max(Integer::compareTo).get() ? j : i)
                .get();

        return result;
    }

    public static List<Integer> HurwitzCriteria(List<List<Integer>> decisionTable, double c){

        HashMap<Double, Integer> mapHW = IntStream.range(0,decisionTable.size()).parallel()
                .mapToObj((i) -> makeHwMap(i,decisionTable,c))
                .reduce((hM1, hM2) -> hM1.keySet().stream().findFirst().get() >
                        hM2.keySet().stream().findFirst().get() ? hM1 : hM2)
                .get();

        List<Integer> result = decisionTable.get(mapHW.values().stream().findFirst().get());

        return result;
    }

    public static List<Integer> BayesLaplasCriteria(List<List<Integer>> decisionTable, List<Double> probabilityList) {

        HashMap<Double, Integer> mapBL = IntStream.range(0, decisionTable.size()).parallel()
                .mapToObj((i) -> makeBlMap(i, decisionTable, probabilityList))
                .reduce((bL1, bL2) -> bL1.keySet().stream().findFirst().get() >
                        bL2.keySet().stream().findFirst().get() ? bL1 : bL2)
                .get();

        List<Integer> result = decisionTable.get(mapBL.values().stream().findFirst().get());

        return result;
    }

    private static HashMap<Double, Integer> makeHwMap(int n, List<List<Integer>> decisionTable, double c){
        if(c<0 || c>1) throw new IllegalArgumentException("Argument c must be between 0 and 1, but c is " + c);
        HashMap<Double, Integer> result = new HashMap<>();
        result.put(c*decisionTable.get(n).stream().max(Integer::compareTo).get()+
                        (1-c)*decisionTable.get(n).stream().min(Integer::compareTo).get(),
                n);
        return result;
    }

    private static HashMap<Double, Integer> makeBlMap(int n, List<List<Integer>> decisionTable, List<Double> probabilityList){
        if(decisionTable.get(0).size() != probabilityList.size())
            throw new IllegalArgumentException("Size List's in decision table not equal size probability table");
        HashMap<Double, Integer> result = new HashMap<>();
        result.put(IntStream.range(0,decisionTable.get(n).size()).boxed().mapToDouble((e) -> decisionTable.get(n).get(e)*probabilityList.get(e))
                        .sum(),
                n);
        return result;
    }



}
