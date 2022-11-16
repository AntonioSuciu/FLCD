package Domain;

import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.Scanner;

public class FiniteAutomaton {
    public Set<String> alphabet;
    public Set<String> states;
    public String initialState;
    public Set<String> finalStates;

    public Map<Pair<String, String>, Set<String>> transitions;
    // map of two entries
    // a pair of source state + value to access the destination state
    // a set of destination states

    public FiniteAutomaton(String fileName) {
        this.alphabet = new HashSet<>();
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashMap<>();

        readFiniteAutomaton(fileName);

    }

    private void readFiniteAutomaton(String fileName) {
        try {

            // each FA file is defined as such:
            ///states (e.g.: p q r)
            // alphabet (e.g.: 0 1)
            // initial state(e.g.: p)
            // final state(s) (e.g.: p)
            // transitions (e. g.: p 0 p
            //                     p 1 q)

            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            String statesLine = scanner.nextLine();
            this.states = new HashSet<>(Arrays.asList(statesLine.split(" ")));

            String alphabetLine = scanner.nextLine();
            this.alphabet = new HashSet<>(Arrays.asList(alphabetLine.split((" "))));

            initialState = scanner.nextLine();

            String finalStatesLine = scanner.nextLine();
            this.finalStates = new HashSet<>(Arrays.asList(finalStatesLine.split(" ")));

            while (scanner.hasNextLine()) {
                String transitionLine = scanner.nextLine();
                String[] transitionElems = transitionLine.split(" ");

                if (states.contains(transitionElems[0]) && states.contains(transitionElems[2]) && alphabet.contains(transitionElems[1])) {
                    /// we check so that the input states are part of the list of states
                    Pair<String, String> transitionStates = new Pair<>(transitionElems[0], transitionElems[1]);
                    // if so, we create a pair with the initial state and the alphabet element

                    if (!this.transitions.containsKey(transitionStates)) {
                        /// if we don't have it, then we add it to the transition state set
                        Set<String> transitionStatesSet = new HashSet<>();
                        transitionStatesSet.add(transitionElems[2]);
                        this.transitions.put(transitionStates, transitionStatesSet);

                    } else {
                        transitions.get(transitionStates).add(transitionElems[2]);
                    }
                }
                /// ERROR MESSAGE IF FA.IN WRONG

            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String writeAlphabet()
    {
        StringBuilder string = new StringBuilder();
        string.append("alphabet: ");
        for(String s : alphabet)
        {
            string.append(s).append(" ");
        }

        return string.toString();
    }

    public String writeStates()
    {
        StringBuilder string = new StringBuilder();
        string.append("states: ");
        for (String s: states)
        {
            string.append(s).append(" ");
        }

        return string.toString();
    }
    public String writeFinalStates()
    {
        StringBuilder string = new StringBuilder();
        string.append("final states: ");
        for (String fs: finalStates)
        {
            string.append(fs).append(" ");
        }

        return string.toString();
    }

    public String writeTransitions()
    {
        StringBuilder string = new StringBuilder();
        string.append("transitions: \n");
        transitions.forEach(
                (K, V) -> {
                    string.append("(")
                            .append(K.getKey())
                            .append(",")
                            .append(K.getValue())
                            .append(") => ")
                            .append(V)
                            .append("\n");
                }
        );

        return string.toString();
    }

    public boolean checkSequence(String seq)
    {
        /// we check if a sequence is accepted by the dfa

        /// if the sequence is epsilon, we check whether the initial state is also a final state
        if(seq.length() == 0)
            return finalStates.contains(initialState);

        String state = initialState;
        /// we start from the initial state
        for (int i = 0; i<seq.length(); i++)
        /// we iterate through the characters of the sequence
        {
            Pair<String, String> key = new Pair<>(state, String.valueOf(seq.charAt(i)));
            /// we check that the pair (currentState, valueOfCurrentChar) is mapped to a set with a single value
            if(transitions.containsKey(key))
            {
                state = transitions.get(key).iterator().next();
                // if so, it means that this is our new current state in this iteration
            }
            else
            {
                return false;
                /// if we don't find any mapping => not accepted by the dfa
            }
            /// if we find the mapping => accepted by the dfa
        }
        return finalStates.contains(state);
    }


    public boolean checkDFA(){
        return this.transitions.values().stream().noneMatch(list -> list.size() > 1);
        /// we check that each pair (each key) is mapped to a single value in the transitions' map
    }


    @Override
    public String toString()
    {
        return "FiniteAutomaton{" +
                "alphabet=" + alphabet +
                ", states=" + states +
                ", finalStates=" + finalStates +
                ", initialState='" + initialState + '\'' +
                ", transitions=" + transitions +
                '}';
    }
}

