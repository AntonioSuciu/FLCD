import Domain.Scanner;
import Domain.SymbolTable;
import javafx.util.Pair;
import Domain.Lexic;

import static UI.UI.optionsForDFA;
import static UI.UI.runScanner;

public class Main {
    public static void main(String[] args) {
//        SymbolTable symbolTable = new SymbolTable(13);
//
//        symbolTable.put("321");
//        symbolTable.put("123");
//        symbolTable.put("456");
//
//        Pair<Integer, Integer> positionOf123 = symbolTable.position("123");
//
//        System.out.print("Hashvalue of 123: ");
//        System.out.println(positionOf123.getKey());
//        System.out.print("Position of 123: ");
//        System.out.println(positionOf123.getValue());
//
////        Lexic lexic = new Lexic();
//
////        System.out.println(lexic.isConstant("0"));
//
//        System.out.println("Program 1");
//        Scanner scannerP1 = new Scanner("src/Files/p1.TXT", "src/Files/pif1.TXT", "src/Files/idST1.TXT", "src/Files/cST1.TXT");
//        scannerP1.scan();
//
//        System.out.println("------------------------");
//
//        System.out.println("Program 2");
//        Scanner scannerP2 = new Scanner("src/Files/p2.TXT", "src/Files/pif2.TXT", "src/Files/idST2.TXT", "src/Files/cST2.TXT");
//        scannerP2.scan();
//
//        System.out.println("------------------------");
//
//        System.out.println("Program 3");
//        Scanner scannerP3 = new Scanner("src/Files/p3.TXT", "src/Files/pif3.TXT", "src/Files/idST3.TXT", "src/Files/cST3.TXT");
//        scannerP3.scan();
//
//        System.out.println("------------------------");
//
//
//        System.out.println("Program 1ERR");
//        Scanner scannerP1err = new Scanner("src/Files/p1err.TXT", "src/Files/pif1err.TXT", "src/Files/idST1err.TXT", "src/Files/cST1err.TXT");
//        scannerP1err.scan();
//
//        System.out.println("------------------------");

            System.out.println("1. Test DFA");
            System.out.println("2. Scanner");
            System.out.println("Your option: ");

            java.util.Scanner scanner = new java.util.Scanner(System.in);
            int option = scanner.nextInt();

            switch (option) {
                case 1 -> optionsForDFA();
                case 2 -> runScanner();
            }

    }
}
