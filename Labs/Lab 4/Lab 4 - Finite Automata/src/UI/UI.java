package UI;

import Domain.FiniteAutomaton;
import Domain.Scanner;

public class UI {
    public static void printMenu(){
        System.out.println("1. Print states.");
        System.out.println("2. Print alphabet.");
        System.out.println("3. Print final states.");
        System.out.println("4. Print transitions.");
        System.out.println("5. Check if sequence is accepted by DFA.");
        System.out.println("0. Exit");
    }

    public static void optionsForDFA(){
        FiniteAutomaton fa = new FiniteAutomaton("src/Files/FA.in");

        System.out.println("FA read from file.");
        printMenu();
        System.out.println("Your option: ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int option = scanner.nextInt();

        while(option != 0) {
            switch (option) {
                case 1 -> System.out.println(fa.writeStates());
                case 2 -> System.out.println(fa.writeAlphabet());
                case 3 -> System.out.println(fa.writeFinalStates());
                case 4 -> System.out.println(fa.writeTransitions());
                case 5 -> {
                    if(fa.checkDFA()) {
                        System.out.println("Your sequence: ");
                        java.util.Scanner scanner2 = new java.util.Scanner(System.in);
                        String sequence = scanner2.nextLine();

                        if (fa.checkSequence(sequence))
                            System.out.println("Sequence is valid");
                        else
                            System.out.println("Invalid sequence");
                    }
                    else {
                        System.out.println("FA is not deterministic.");
                    }
                }
            }
            System.out.println();
            printMenu();
            System.out.println("Your option: ");
            option = scanner.nextInt();
        }
    }

    public static void runScanner(){
        Scanner scannerP1 = new Scanner("src/Files/p1.TXT",
                "src/Files/pif1.TXT",
                "src/Files/idST1.txt",
                "src/Files/cST1.txt");
        scannerP1.scan();
    }
}

