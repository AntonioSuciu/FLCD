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
        /// Return an empty set if the input list is empty
        if(nonTerminals.size() == 0)
        {
            return new HashSet<>();
        }
        /// Return the first set of the single non-terminal in the list if the input list has size 1
        if(nonTerminals.size() == 1)
        {
            return firstSet.get(nonTerminals.iterator().next());
        }
        /// Otherwise, concatenate the first sets of the non-terminals in the list
        Set<String> concatenation = new HashSet<>();
        int step = 0;

        while (step < nonTerminals.size())
        {
            boolean thereIsEpsilon = false;
            /// Iterate over the terminal symbols in the first set of the current non-terminal
            for(String s: firstSet.get(nonTerminals.get(step)))
            {
                /// If an epsilon is encountered, set a flag and continue to the next symbol
                if(s.equals("epsilon"))
                {
                    thereIsEpsilon = true;
                }
                /// Otherwise, add the symbol to the concatenated set
                else {
                    concatenation.add(s);
                }
            }
            /// If an epsilon was encountered, move on to the next non-terminal
            if(thereIsEpsilon)
                step++;
                /// If no epsilon was encountered, break out of the loop
            else
                break;
        }
        /// Return the concatenated set
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
//                        System.out.println(productionList);
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

        // we initialize the parse table with rows for each non-terminal and terminal symbol in the grammar,
        // and columns for each terminal symbol and the end-of-input symbol "$".
        // we set the default value for each cell in the table to "err" and -1.
        List<String> rows = new ArrayList<>();
        rows.addAll(grammar.getN());
        rows.addAll(grammar.getSigma());
        rows.add("$");
        // add end-of-input symbol as a row

        List<String> columns = new ArrayList<>();
        columns.addAll(grammar.getSigma());
        columns.add("$");
        // add end-of-input symbol as a column



        for (var row : rows)
            for (var col : columns)
                parseTable.put(new Pair<String, String>(row, col), new Pair<String, Integer>("err",-1));

        // Set the value of the cell in the parse table corresponding to an input symbol and stack symbol match to "pop" and -1.
        for (var col : columns)
            parseTable.put(new Pair<String, String>(col, col), new Pair<String, Integer>("pop",-1));

        parseTable.put(new Pair<String, String>("$", "$"), new Pair<String, Integer>("acc",-1));

        /// get the right hand side of every production and add it to a list, excluding epsilon productions.
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

//        System.out.println(productionsRHS);

        /// for each production
        productions.forEach((k, v) -> {
            var key = k.iterator().next();

            for (var production : v) {
                /// we get the first symbol of the right-hand side of the production.
                var firstSymbol = production.get(0);
                /// if the first symbol is a terminal symbol, add the production to the corresponding cell in the parse table.
                if (grammar.getSigma().contains(firstSymbol))
                    if (parseTable.get(new Pair<>(key, firstSymbol)).getFirst().equals("err"))
                        // if it is still "err" (like in the beginning), we add the production and its index to the cell
                        parseTable.put(new Pair<>(key, firstSymbol), new Pair<>(String.join(" ", production),productionsRHS.indexOf(production)+1));
                    else {
                        try {
                            /// we have a conflict: there are multiple productions
                            // that could be applied to the same input symbol and stack symbol.
                            throw new IllegalAccessException("There is a conflict: Pair "+key+","+firstSymbol);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    ///else, if it is in the set of nonterminals,  determine the first set for that non-terminal
                    // and add the production to the corresponding cells in the parse table.
                else if (grammar.getN().contains(firstSymbol)) {
                    if (production.size() == 1)
                        // handle case of epsilon production
                        for (var symbol : firstSet.get(firstSymbol))
                            if (parseTable.get(new Pair<>(key, symbol)).getFirst().equals("err"))
                                // If the cell is still "err" (like in the beginning), add the production and its index to the cell.
                                parseTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", production),productionsRHS.indexOf(production)+1));
                            else {
                                try {
                                    // There is a conflict: there are multiple productions that could be applied to the same input symbol and stack symbol.
                                    throw new IllegalAccessException("There is a conflict: pair " + key + ", " +firstSymbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                    else {
                        // Handle case where first symbol is a non-terminal,
                        // but the production is not an epsilon production.
                        var i = 1;
                        var nextSymbol = production.get(1);
                        var firstSetForProduction = firstSet.get(firstSymbol);

                        // Determine the first set for the production
                        // by combining the first sets of the symbols
                        // in the production until we reach a terminal symbol
                        // or the end of the production.
                        while (i < production.size() && grammar.getN().contains(nextSymbol)) {
                            var firstForNext = firstSet.get(nextSymbol);
                            if (firstSetForProduction.contains("epsilon")) {
                                // If the first set of the first symbol in the production includes epsilon,
                                // remove epsilon and
                                // add the first set of the next symbol to the first set of the production.
                                firstSetForProduction.remove("epsilon");
                                firstSetForProduction.addAll(firstForNext);
                            }

                            i++;
                            if (i < production.size())
                                nextSymbol = production.get(i);
                        }
                        // Add the production to the cells in the parse table
                        // corresponding to the first set of the production.
                        for (var symbol : firstSetForProduction) {
                            if (symbol.equals("epsilon"))
                                symbol = "$";
                            if (parseTable.get(new Pair<>(key, symbol)).getFirst().equals("err"))
                                // If the cell is still "err" (like in the beginning),
                                // add the production and its index to the cell
                                parseTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", production), productionsRHS.indexOf(production) + 1));
                            else {
                                try {
                                    // There is a conflict: there are multiple productions that could be applied
                                    // to the same input symbol and stack symbol.
                                    throw new IllegalAccessException("There is a conflict: pair " + key + ", " +firstSymbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    // If the first symbol of the production is
                    // a non-terminal and the production is an epsilon production,
                    // add the production to the cells in the parse table
                    // corresponding to the follow set of the non-terminal.
                    var follow = followSet.get(key);
                    for (var symbol : follow) {
                        if (symbol.equals("epsilon")) {
                            // Handle special case where follow set includes epsilon.
                            if (parseTable.get(new Pair<>(key, "$")).getFirst().equals("err")) {
                                // If the cell is still "err" (like in the beginning), add the production and its index to the cell.
                                var prod = new ArrayList<>(List.of("epsilon",key));
                                parseTable.put(new Pair<>(key, "$"), new Pair<>("epsilon", productionsRHS.indexOf(prod) + 1));
                            }
                            else {
                                try {
                                    // There is a conflict: there are multiple productions that could be applied
                                    // to the same input symbol and stack symbol.
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
                                // There is a conflict: there are multiple productions that could be applied
                                // to the same input symbol and stack symbol.
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
        /// while there's still something in the stacks, we process them
        {
            // Get the top symbols of the input stack and the working stack.
            String inputStackPeek = inputStack.peek();
            String workingStackPeek = workingStack.peek();

            // Look up the production to use in the parse table using
            // the top symbols of the input stack and working stack as the key.
            Pair<String,String> key = new Pair<>(workingStackPeek, inputStackPeek);
            Pair<String,Integer> value = parseTable.get(key);

            // If the value in the parse table is not "err", proceed with the parse.
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


