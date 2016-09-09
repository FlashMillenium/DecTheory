import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

    public static void main(String args[]){

        ArrayList<List<Integer>> decisionTable = new ArrayList<>();
        decisionTable.add(Arrays.asList(new Integer[]{ 15, 10,  0, -6, 17}));
        decisionTable.add(Arrays.asList(new Integer[]{  3, 14,  8,  9,  2}));
        decisionTable.add(Arrays.asList(new Integer[]{  1,  5, 14, 20, -3}));
        decisionTable.add(Arrays.asList(new Integer[]{  7, 19, 10,  2,  0}));

        System.out.println("Decision table: ");
        decisionTable.stream().forEach((s) -> System.out.println(s.toString()));

        List resultMM = SolutionOnCriteria.minMaxCriteria(decisionTable);

        System.out.println("result with MinMaxCriteria: " +resultMM);

        List resultS = SolutionOnCriteria.savageCriteria(decisionTable);

        System.out.println("result with SavageCriteria: " + resultS);


        double c = 0.5;

        List<Integer> resultHW = SolutionOnCriteria.HurwitzCriteria(decisionTable,c);

        System.out.println("result with HurwitzCriteria "+resultHW);


        ///create decision table for baies-laplase solution

        final int goodPrice = 49, badPrice = 15, stockPrice = 25;


        List<List<Integer>> gendecisionTable = IntStream.iterate(100, i-> i+50).limit(5).mapToObj((supply) ->
                IntStream.iterate(100, i->i+50).limit(5).boxed()
                        .map((demand) ->
                                (supply > demand ? demand*goodPrice : supply*goodPrice)
                                        - (stockPrice*supply) +
                                        (supply > demand ? badPrice*(supply-demand): 0)
                        )
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        System.out.println("Generate table:");
        gendecisionTable.stream().forEach((s) -> System.out.println(s.toString()));

        List<Double> probabilityList = Arrays.asList(new Double[]{0.15, 0.2, 0.25, 0.3, 0.1});

        List<Integer> resultBL = SolutionOnCriteria.BayesLaplasCriteria(gendecisionTable, probabilityList);

        System.out.println("result BL: " + resultBL);
    }
}
