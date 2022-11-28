import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

//Class Grammar
// (required operations:
// read a grammar from file,
// print set of nonterminals,
// set of terminals,
// set of productions,
// productions for a given nonterminal,
// CFG check)

// G = (N, Sigma, P, S)
public class Grammar
{
    private Set<String> N = new HashSet<>();
    private Set<String> Sigma = new HashSet<>();
    private final HashMap<Set<String>, Set<List<String>>> P = new HashMap<>();
    private String S = "";

    public Grammar (String file)
    {
        readFromFile(file);
    }

    private void readFromFile(String file)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String input = reader.readLine();

            // reading the nonterminals
            String[] NLineSplit = input.split("=", input.indexOf("="));
            StringBuilder Nline = new StringBuilder();
            for(int i = 1; i<NLineSplit.length; i++)
            {
                Nline.append(NLineSplit[i]);
            }

            StringBuilder builder = new StringBuilder(Nline.toString());
//            System.out.println(builder);
            builder.deleteCharAt(1).deleteCharAt(Nline.length()-2);
            /// deletes the { }

            Nline = new StringBuilder(builder.toString());
            this.N = new HashSet<>(Arrays.asList(Nline.toString().strip().split(" ")));

            input = reader.readLine();

            /// reading the alphabet
            String[] SigmaLineSplit = input.split("=", input.indexOf("="));
            StringBuilder Sigmaline = new StringBuilder();
            for(int i = 1; i<SigmaLineSplit.length; i++)
            {
                Sigmaline.append(SigmaLineSplit[i]);
            }

            builder = new StringBuilder(Sigmaline.toString());
//            System.out.println(builder);
            builder.deleteCharAt(1).deleteCharAt(Sigmaline.length()-2);
            /// deletes the { }

            Sigmaline = new StringBuilder(builder.toString());
            this.Sigma = new HashSet<>(Arrays.asList(Sigmaline.toString().strip().split(" ")));

            this.S = reader.readLine().split("=")[1].strip();

            reader.readLine();
            // P = {
            String line = reader.readLine();
            while(line != null) {
                if (!line.equals("}")) {
                    String[] tokens = line.split("->");
                    String[] leftTokens = tokens[0].split(",");
                    String[] rightTokens = tokens[1].split("\\|");

                    Set<String> left = new HashSet<>();
                    // the left-hand side

                    Set<String> right = new HashSet<>();
                    // the right-hand side

                    for (String leftToken : leftTokens) {
                        left.add(leftToken.strip());
                    }

                    if (!P.containsKey(left)) {
                        P.put(left, new HashSet<>());
                    }

                    for (String rightToken : rightTokens) {
                        ArrayList<String> productionElems = new ArrayList<>();
                        String[] rightTokenElem = rightToken.strip().split(" ");
                        for (String r : rightTokenElem) {
                            productionElems.add(r.strip());
                        }

                        P.get(left).add(productionElems);
                    }
                }
                line = reader.readLine();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String printNonterminals()
    {
        StringBuilder nonterminals = new StringBuilder("N = { ");
        for (String nt : N)
        {
            nonterminals.append(nt)
                    .append(" ");
        }
        nonterminals.append("}");

        return nonterminals.toString();
    }

    public String printTerminals()
    {
        StringBuilder terminals = new StringBuilder("Sigma = { ");
        for (String t : Sigma)
        {
            terminals.append(t)
                    .append(" ");
        }
        terminals.append("}");

        return terminals.toString();
    }

    public String printProductions()
    {
        StringBuilder production = new StringBuilder("P = { \n");
        P.forEach((lhs, rhs) -> {
            production.append("\t");
            int count = 0;
            for(String lh:lhs)
            {
                production.append(lh);
                count++;
                /// if we have more than one production in the lhs
                if(count<lhs.size())
                {
                    production.append(", ");
                }
            }

            production.append(" -> ");
            count = 0;
            for(List<String> rh: rhs)
            {
                for (String r : rh)
                {
                    production.append(r).append(" ");
                }
                count++;
                /// if we have more productions in the right-hand side
                if (count < rhs.size())
                    production.append("| ");
            }
            production.append("\n");
        });
        production.append("}");
        return production.toString();
    }

    public String printProductionsForGivenNonterminal(String nonTerminal)
    {
        StringBuilder prod = new StringBuilder();
        for(Set<String> lhs : P.keySet())
        {
            if (lhs.contains(nonTerminal))
            {
                prod.append(nonTerminal).append(" -> ");
                Set<List<String>> rhs = P.get(lhs);
                int count = 0;
                for(List<String> rh : rhs)
                {
                    for(String r: rh)
                    {
                        prod.append(r).append(" ");
                    }
                    count++;
                    if(count < rhs.size())
                    {
                        prod.append("| ");
                    }
                }
            }
        }
        return prod.toString();
    }

    public boolean CFGCheck()
    {
        boolean checkStartingSymbol = false;

        for(Set<String> lhs: P.keySet())
        {
            if (lhs.contains(S))
            {
                checkStartingSymbol = true;
                break;
            }
        }
        if(!checkStartingSymbol)
            return false;

        for(Set<String> lhs: P.keySet())
        {
            if(lhs.size() > 1)
                return false;
            else
                if (!N.contains(lhs.iterator().next()))
                    return false;
            Set<List<String>> rhs = P.get(lhs);

            for(List<String> rh : rhs) {
                for (String r : rh) {
                    if(!(N.contains(r) || Sigma.contains(r) || r.equals("epsilon")))
                        return false;
                }
            }
        }
        return true;
    }

    public Set<String> getN() {
        return N;
    }

    public Set<String> getSigma() {
        return Sigma;
    }

    public HashMap<Set<String>, Set<List<String>>> getP() {
        return P;
    }

    public String getS() {
        return S;
    }

    public Set<List<String>> getProductionForNonterminal(String nonTerminal) {
        for (Set<String> lhs : P.keySet()) {
            if (lhs.contains(nonTerminal)) {
                return P.get(lhs);
            }
        }
        return new HashSet<>();
    }
}
