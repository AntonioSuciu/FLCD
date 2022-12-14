import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<String> readSequence(String filename)
    {
        List<String> sequence = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            while (line != null) {
                var symbols = List.of(line.split(" "));
                sequence.addAll(symbols);
                line = reader.readLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return sequence;
    }
    public static void main(String[] args)
    {


        Grammar grammar = new Grammar("src/Utils/g1.in");
        Parser parser = new Parser(grammar);



//        Grammar grammar = new Grammar("src/Utils/g1_corrected.in");
//        Parser parser = new Parser(grammar);

//        Grammar grammar = new Grammar("src/Utils/g4.in");
//        System.out.println(grammar.printProductions());
//        Parser parser = new Parser(grammar);
////        System.out.println(parser.printFirst());
////        System.out.println(parser.printFollow());
//        System.out.println(parser.printParseTable());
//        List<String> sequence = readSequence("src/Utils/seq4.in");
//        System.out.println(parser.analyseSequence(sequence));
//        ParserOutput parserOutput = new ParserOutput(parser,sequence,"src/Utils/output.in");
//        parserOutput.printTree();



//        Grammar grammar = new Grammar("src/Utils/g3.in");
//        Parser parser = new Parser(grammar);
//        System.out.println(parser.printFirst());
//        System.out.println(parser.printFollow());
//        System.out.println(parser.printParseTable());

//        Grammar grammar = new Grammar("src/Utils/g2_corrected.in");
////        System.out.println(grammar.printProductions());
//        Parser parser = new Parser(grammar);
//        System.out.println(parser.printFirst());
//        System.out.println(parser.printFollow());
//        var sigma = grammar.getSigma();
//        System.out.println(sigma);
//        System.out.println(parser.printParseTable());
//        List<String> sequence = readSequence("src/Utils/seq2.in");
//        System.out.println(parser.analyseSequence(sequence));
//        ParserOutput parserOutput = new ParserOutput(parser,sequence,"src/Utils/output.in");
//        parserOutput.printTree();





//        Parser parser = new Parser(grammar);
//        System.out.println(parser.printFirst());
//        System.out.println(parser.printFollow());
//        System.out.println(parser.printParseTable());
//        List<String> sequence = List.of("(","int",")","+","int");
//        List<String> sequence = readSequence("src/Utils/seq.in");
//        List<String> sequence = readSequence("src/Utils/seq2.in");
//        System.out.println(parser.analyseSequence(sequence));
//        ParserOutput parserOutput = new ParserOutput(parser,sequence,"src/Utils/output.in");
//        parserOutput.printTree();
    }
}
