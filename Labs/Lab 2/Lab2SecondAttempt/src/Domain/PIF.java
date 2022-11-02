package Domain;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PIF {

    private List<Pair<Integer, Pair<Integer, Integer>>> pif = new ArrayList<>();

    public void add(Integer tokenCode, Pair<Integer, Integer> value)
    {
        Pair<Integer, Pair<Integer, Integer>> pifEntry = new Pair<>(tokenCode, value);
        pif.add(pifEntry);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Pair<Integer, Pair<Integer, Integer>> pair : pif) {
            result.append(pair.getKey()).append(" -> (").append(pair.getValue().getKey()).append(", ").append(pair.getValue().getValue()).append(")\n");
        }
        return result.toString();
    }
}
