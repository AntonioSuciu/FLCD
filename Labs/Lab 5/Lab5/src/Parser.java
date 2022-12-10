import java.util.*;

public class Parser
{
    private final Grammar grammar;

    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;

    private HashMap<Pair, Pair> parseTable;

    private List<List<String>> productionsRHS;

    public Parser(Grammar grammar){
        this.grammar = grammar;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();

        this.parseTable = new HashMap<>();

        generateFirst();
        generateFollow();
        generateParseTable();
    }


    private Set<String> concatSizeOne(List<String> nonTerminals) {
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
//                            rhsTerminal = symbol;
                            /// if it is a non-terminal => it is a terminal => we stop
                            break;
                        }
                        /// we perform the concatenation of size one
                    toAdd.addAll(concatSizeOne(rhsNonTerminals));
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


    public void generateFollow()
    {
        // we initialize
        for (String nonterminal: grammar.getN())
        {
            followSet.put(nonterminal, new HashSet<>());
        }

        followSet.get(grammar.getS()).add("epsilon");

        // the other iteration

        var isChanged = true;

        while(isChanged)
        {
            isChanged = false;
            HashMap<String, Set<String>> buffer = new HashMap<>();

            for(String nonterminal : grammar.getN())
            {
                /// we add in a buffer each nonterminal with each corresponding follow elements
                buffer.put(nonterminal, new HashSet<>());

                /// we build up the mapping between the productions and
                var productionsWithNonterminalInRhs = new HashMap<String, Set<List<String>>>();


                var allProductions = grammar.getP();
                allProductions.forEach((k, v) -> {
                    /// for each production in the set
                    for (var eachProduction : v) {
                        /// if it contains the nonterminal
                        if (eachProduction.contains(nonterminal)) {
                            var key = k.iterator().next();
                            /// we store the LHS of the production
                            if (!productionsWithNonterminalInRhs.containsKey(key))

                                // and if it's not already added
                                productionsWithNonterminalInRhs.put(key, new HashSet<>());
                            /// we add the LHS -> ProdWithNonTRHS thingy
                            productionsWithNonterminalInRhs.get(key).add(eachProduction);
                        }
                    }
                });

                ///the follow set of the current nonterminal that is to be added to the buffer
                var toAdd = new HashSet<>(followSet.get(nonterminal));
                productionsWithNonterminalInRhs.forEach((k, v) -> {
                    for (var production : v) {
                        ///for each production
                        var productionList = (ArrayList<String>) production;
                        System.out.println(productionList);
                        for (var indexOfNonterminal = 0; indexOfNonterminal < productionList.size(); indexOfNonterminal++)
                            /// if we find the nonterminal in the production lists and
                            if (productionList.get(indexOfNonterminal).equals(nonterminal)) {
                                if (indexOfNonterminal + 1 == productionList.size()) {
                                    /// keeps it in order to add it to look further for the follow (being the last terminal, nothing follows)
                                    toAdd.addAll(followSet.get(k));
                                } else {
                                    /// otherwise we get the next symbol
                                    var followSymbol = productionList.get(indexOfNonterminal + 1);
                                    if (grammar.getSigma().contains(followSymbol))
                                        /// and if it is part of the alphabet, it will be added
                                        toAdd.add(followSymbol);
                                    else {
                                        /// if it is not part of the alphabet, we move on
                                        for (var symbol : firstSet.get(followSymbol)) {
                                            /// if it is epsilon, we add the follow of the lhs
                                            if (symbol.equals("epsilon"))
                                                toAdd.addAll(followSet.get(k));
                                            else
                                                toAdd.addAll(firstSet.get(followSymbol));
                                        }
                                    }
                                }
                            }
                    }
                });
                if (!toAdd.equals(followSet.get(nonterminal))) {
                    isChanged = true;
                }
                buffer.put(nonterminal, toAdd);
            }

            followSet = buffer;
        }
    }


    public void generateParseTable() {

        // we initialize the parse table
        List<String> rows = new ArrayList<>();
        rows.addAll(grammar.getN());
        rows.addAll(grammar.getSigma());
        rows.add("$");

        List<String> columns = new ArrayList<>();
        columns.addAll(grammar.getSigma());
        columns.add("$");

        for (var row : rows)
            for (var col : columns)
                parseTable.put(new Pair<String, String>(row, col), new Pair<String, Integer>("err",-1));

        for (var col : columns)
            parseTable.put(new Pair<String, String>(col, col), new Pair<String, Integer>("pop",-1));

        parseTable.put(new Pair<String, String>("$", "$"), new Pair<String, Integer>("acc",-1));

        /// get the right hand side of every production
        var productions = grammar.getP();
        this.productionsRHS = new ArrayList<>();
        productions.forEach((k,v) -> {
            var nonterminal = k.iterator().next();
            for(var prod : v)
                if(!prod.get(0).equals("epsilon"))
                    productionsRHS.add(prod);
                else {
                    productionsRHS.add(new ArrayList<>(List.of("epsilon", nonterminal)));
                }
        });

        System.out.println(productionsRHS);

        /// for each production
        productions.forEach((k, v) -> {
            var key = k.iterator().next();

            for (var production : v) {
                /// we get the first symbol
                var firstSymbol = production.get(0);
                /// if it is in the alphabet
                if (grammar.getSigma().contains(firstSymbol))
                    if (parseTable.get(new Pair<>(key, firstSymbol)).getFirst().equals("err"))
                        parseTable.put(new Pair<>(key, firstSymbol), new Pair<>(String.join(" ", production),productionsRHS.indexOf(production)+1));
                    else {
                        try {
                            throw new IllegalAccessException("There is a conflict: Pair "+key+","+firstSymbol);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                else if (grammar.getN().contains(firstSymbol)) {
                    if (production.size() == 1)
                        for (var symbol : firstSet.get(firstSymbol))
                            if (parseTable.get(new Pair<>(key, symbol)).getFirst().equals("err"))
                                parseTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", production),productionsRHS.indexOf(production)+1));
                            else {
                                try {
                                    throw new IllegalAccessException("There is a conflict: pair " + key + ", " +firstSymbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                    else {
                        var i = 1;
                        var nextSymbol = production.get(1);
                        var firstSetForProduction = firstSet.get(firstSymbol);

                        while (i < production.size() && grammar.getN().contains(nextSymbol)) {
                            var firstForNext = firstSet.get(nextSymbol);
                            if (firstSetForProduction.contains("epsilon")) {
                                firstSetForProduction.remove("epsilon");
                                firstSetForProduction.addAll(firstForNext);
                            }

                            i++;
                            if (i < production.size())
                                nextSymbol = production.get(i);
                        }

                        for (var symbol : firstSetForProduction) {
                            if (symbol.equals("epsilon"))
                                symbol = "$";
                            if (parseTable.get(new Pair<>(key, symbol)).getFirst().equals("err"))
                                parseTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", production), productionsRHS.indexOf(production) + 1));
                            else {
                                try {
                                    throw new IllegalAccessException("There is a conflict: pair " + key + ", " +firstSymbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    var follow = followSet.get(key);
                    for (var symbol : follow) {
                        if (symbol.equals("epsilon")) {
                            if (parseTable.get(new Pair<>(key, "$")).getFirst().equals("err")) {
                                var prod = new ArrayList<>(List.of("epsilon",key));
                                parseTable.put(new Pair<>(key, "$"), new Pair<>("epsilon", productionsRHS.indexOf(prod) + 1));
                            }
                            else {
                                try {
                                    throw new IllegalAccessException("There is a conflict: pair " + key + ", " +firstSymbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (parseTable.get(new Pair<>(key, symbol)).getFirst().equals("err")) {
                            var prod = new ArrayList<>(List.of("epsilon",key));
                            parseTable.put(new Pair<>(key, symbol), new Pair<>("epsilon", productionsRHS.indexOf(prod) + 1));
                        }
                        else {
                            try {
                                throw new IllegalAccessException("There is a conflixt: pair " + key + ", " +firstSymbol);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

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

    public String printFollow() {
        StringBuilder builder = new StringBuilder();
        followSet.forEach((k, v) -> {
            builder.append(k).append(": ").append(v).append("\n");
        });
        return builder.toString();
    }

    public String printParseTable() {
        StringBuilder builder = new StringBuilder();
        parseTable.forEach((k, v) -> {
            builder.append(k).append(" -> ").append(v).append("\n");
        });
        return builder.toString();
    }

    public List<Integer> analyseSequence(List<String> sequence)
    {
        Stack<String> inputStack = new Stack<>();
        Stack<String> workingStack = new Stack<>();

        List<Integer> output = new ArrayList<>();

        /// we initialize the stacks

        inputStack.push("$");
        for (var i = sequence.size() - 1; i>=0; i--)
            inputStack.push(sequence.get(i));

        workingStack.push("$");
        workingStack.push(grammar.getS());

        while(!(inputStack.peek().equals("$") && workingStack.peek().equals("$")))
        {
            String inputStackPeek = inputStack.peek();
            String workingStackPeek = workingStack.peek();
            Pair<String,String> key = new Pair<>(workingStackPeek, inputStackPeek);
            Pair<String,Integer> value = parseTable.get(key);

            if(!value.getFirst().equals("err")){
                if(value.getFirst().equals("pop")){
                    inputStack.pop();
                    workingStack.pop();
                }
                else {
                    workingStack.pop();
                    if(!value.getFirst().equals("epsilon")) {
                        String[] val = value.getFirst().split(" ");
                        for (var i = val.length - 1; i >= 0; --i)
                            workingStack.push(val[i]);
                    }
                    output.add(value.getSecond());
                }
            }
            else
            {
                System.out.println("Syntax error for key "+key);
                System.out.println("Current input stack and working stack for sequence parsing:");
                System.out.println(inputStack);
                System.out.println(workingStack);
                output = new ArrayList<>(List.of(-1));
                return output;
            }
        }
        return output;
    }

    public List<String> getProductionByOrderNumber(int order){
        var production = productionsRHS.get(order-1);
        if(production.contains("epsilon"))
            return List.of("epsilon");
        return production;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public List<List<String>> getProductionsRHS() {
        return productionsRHS;
    }

}


