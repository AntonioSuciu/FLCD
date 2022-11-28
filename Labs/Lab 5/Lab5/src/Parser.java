import java.util.*;

public class Parser
{
    private final Grammar grammar;

    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;

    public Parser(Grammar grammar){
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();

        generateFirst();
//        generateFollow();
    }

    public void generateFirst()
    {
        // we initialize
        for(String nonterminal : grammar.getN())
        {
            firstSet.put(nonterminal, new HashSet<>());
            // for each nonterminal we have a corresponding set
            Set<List<String>> productionForNonterminal = grammar.getProductionForNonterminal(nonterminal);
            // we get the production rules for the current nonterminal
            for (List<String> production : productionForNonterminal)
            {
                if (grammar.getSigma().contains(production.get(0)) || production.get(0).equals("epsilon"))
                {
                    firstSet.get(nonterminal).add(production.get(0));

                    /// for each nonterminal, we add the production to the FIRST set if it's part of the alphabet
                    /// F0(A) = {x | x belongs to Sigma, A -> x alpha or A -> x belongs to P}
                }
            }
        }
        // the other iterations
        var changed = true;
        while(changed)
        {
            changed = false;
            HashMap<String, Set<String>> newColumn = new HashMap<>();

            for(String nonterminal : grammar.getN())
            ///for each non-terminal
            {
                Set<List<String>> productionForNonterminal = grammar.getProductionForNonterminal(nonterminal);
                /// we get the productions
                Set<String> toAdd = new HashSet<>(firstSet.get(nonterminal));
                /// we prepare a set of productions to be added

                for(List<String> production: productionForNonterminal)
                {

                    List<String> rhsNonTerminals = new ArrayList<>();
                    String rhsTerminal = null;
                    /// for each of those productions, we find the non-terminals, and terminals respectively
                    for(String symbol: production)
                        if(this.grammar.getN().contains(symbol))
                        {
                            rhsNonTerminals.add(symbol);
                            /// if we reach a nonterminal, we add it to the list, to be further checked
                        }
                        else
                        {
                            rhsTerminal = symbol;
                            /// if it is a non-terminal => it is a terminal => we stop
                            break;
                        }
                        /// we perform the concatenation of size one
                    toAdd.addAll(concatSizeOne(rhsNonTerminals, rhsTerminal));
                }

                if(!toAdd.equals(firstSet.get(nonterminal)))
                {
                    changed = true;
                }
                newColumn.put(nonterminal, toAdd);
            }
            firstSet = newColumn;
        }
    }

    private Set<String> concatSizeOne(List<String> nonTerminals, String terminal) {
        if(nonTerminals.size() == 0)
        {
            return new HashSet<>();
        }
        /// we return an empty set

        if(nonTerminals.size() == 1)
        {
            return firstSet.get(nonTerminals.iterator().next());
        }
        /// size == 1 => we return what is after the nonterminal

        Set<String> concatenation = new HashSet<>();
        int step = 0;

        while (step < nonTerminals.size())
        {
            boolean thereIsEpsilon = false;
            for(String s: firstSet.get(nonTerminals.get(step)))
            {
                if(s.equals("epsilon"))
                {
                    /// if we have an epsilon, we need to get further
                    thereIsEpsilon = true;
                }
                else {
                    /// otherwise, we add the symbol to the concatenation
                    concatenation.add(s);
                }
            }
            if(thereIsEpsilon)
                step++;
            else
                break;
        }
        return concatenation;
    }

    public String printFirst()
    {
        StringBuilder builder = new StringBuilder();
        firstSet.forEach((k, v) ->
                {
                    builder.append(k)
                            .append(": ")
                            .append(v)
                            .append("\n");
                }
        );
        return builder.toString();
    }
}
