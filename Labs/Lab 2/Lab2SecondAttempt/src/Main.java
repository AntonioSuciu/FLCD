import Domain.SymbolTable;
import javafx.util.Pair;

public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(13);

        symbolTable.put("321");
        symbolTable.put("123");
        symbolTable.put("456");

        Pair<Integer, Integer> positionOf123 = symbolTable.position("123");

        System.out.print("Hashvalue of 123: ");
        System.out.println(positionOf123.getKey());
        System.out.print("Position of 123: ");
        System.out.println(positionOf123.getValue());
    }
}
