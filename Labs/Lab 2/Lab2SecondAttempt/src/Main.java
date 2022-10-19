import Domain.SymbolTable;

public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(13);

        symbolTable.put("321");
        symbolTable.put("123");
        symbolTable.put("456");

        System.out.println(symbolTable.position("123"));
    }
}
