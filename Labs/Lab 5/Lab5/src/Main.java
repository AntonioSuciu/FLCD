public class Main {
    public static void main(String[] args)
    {
        Grammar grammar = new Grammar("src/Utils/g2.in");
        System.out.println(grammar.printProductions());
    }
}
