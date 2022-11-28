public class Main {
    public static void main(String[] args)
    {
        Grammar grammar = new Grammar("src/Utils/g3.in");
//        System.out.println(grammar.printProductions());
        Parser parser = new Parser(grammar);
//        System.out.println(parser.printFirst());
    }
}
