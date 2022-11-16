package Domain;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private Lexic lexic = new Lexic();
    private PIF pif = new PIF();
    private final int sizeST = 19;
    private SymbolTable identifierST = new SymbolTable(sizeST);
    private SymbolTable constantST = new SymbolTable(sizeST);

    private String programFile;
    private String PIFFile;
    private String identifierSTFile;
    private String constantSTFile;

    public Scanner(String programFile, String pifFile, String identifierSTFile, String constantSTFile) {
        this.programFile = programFile;
        this.PIFFile = pifFile;
        this.identifierSTFile = identifierSTFile;
        this.constantSTFile = constantSTFile;
    }


    public ArrayList<String> tokenize(String line) {
        ArrayList<String> tokenList = new ArrayList<>();

        for (int i = 0; i < line.length(); i++) {
            /// we need to take each line token by token
            /// to check whether it is an operator, a separator, a reserved word,
            /// or a constant / identifier

            if (lexic.isSeparator(String.valueOf(line.charAt(i))) && !(String.valueOf(line.charAt(i))).equals(" ")) {
                tokenList.add(String.valueOf(line.charAt(i)));
            } else if (line.charAt(i) == '\"') {
                String constant = isStringConstant(line, i);
                tokenList.add(constant);
                i += constant.length() - 1;
            } else if (line.charAt(i) == '\'') {
                String constant = isCharConstant(line, i);
                tokenList.add(constant);
                i += constant.length() - 1;
            } else if (line.charAt(i) == '-') {
                String token = isMinusToken(line, i, tokenList);
                tokenList.add(token);
                i += token.length() - 1;
            } else if (line.charAt(i) == '+') {
                String token = isPlusToken(line, i, tokenList);
                tokenList.add(token);
                i += token.length() - 1;
            } else if (lexic.isPartOfOperator(line.charAt(i))) {
                String operator = isOperator(line, i);
                tokenList.add(operator);
                i += operator.length() - 1;
            } else if (line.charAt(i) != ' ') {
                String token = isToken(line, i);
                tokenList.add(token);
                i += token.length() - 1;
            }
        }
        return tokenList;


    }

    private String isMinusToken(String line, int pos, ArrayList<String> tokenList) {
        if (lexic.isIdentifier(tokenList.get(tokenList.size() - 1))
                || lexic.isConstant(tokenList.get(tokenList.size() - 1))) {
            return "-";
        }
        // if there is an identifier or a constant behind "-" => it is an op.

        // otherwise, there is an op / a sep. behind "-"     => "-" means negative const
        StringBuilder token = new StringBuilder();
        token.append('-');

        for (int i = pos + 1; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.') {
                token.append(line.charAt(i));
            }
        }
        return token.toString();
    }

    private String isPlusToken(String line, int pos, ArrayList<String> tokenList) {
        if (lexic.isIdentifier(tokenList.get(tokenList.size() - 1))
                || lexic.isConstant(tokenList.get(tokenList.size() - 1))) {
            return "+";
        }
        // if there is an identifier or a constant behind "+" => it is an op.

        // otherwise, there is an op / a sep. behind "+"     => "+" means positive const
        StringBuilder token = new StringBuilder();
        token.append('+');

        for (int i = pos + 1; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.') {
                token.append(line.charAt(i));
            }
        }
        return token.toString();
    }

    private String isOperator(String line, int pos) {
        StringBuilder operator = new StringBuilder();
        operator.append(line.charAt(pos));
        operator.append(line.charAt(pos + 1));

        if (lexic.isOperator(operator.toString()))
            return operator.toString();

        return String.valueOf(line.charAt(pos));
    }

    private String isToken(String line, int pos) {
        StringBuilder token = new StringBuilder();

        for (int i = pos; i < line.length()
                && !lexic.isSeparator(String.valueOf(line.charAt(i)))
                && !lexic.isPartOfOperator(line.charAt(i))
                && line.charAt(i) != ' '; ++i) {
            token.append(line.charAt(i));
        }

        return token.toString();
    }

    private String isCharConstant(String line, int pos) {
        StringBuilder constant = new StringBuilder();

        for (int i = pos; i < line.length(); ++i) {
            if ((lexic.isSeparator(String.valueOf(line.charAt(i))) || lexic.isOperator(String.valueOf(line.charAt(i)))) && ((i == line.length() - 2 && line.charAt(i + 1) != '\'') || (i == line.length() - 1)))
                break;
            constant.append(line.charAt(i));
            if (line.charAt(i) == '\'' && i != pos)
                break;
        }

        return constant.toString();
    }

    private String isStringConstant(String line, int pos) {
        StringBuilder constant = new StringBuilder();

        for (int i = pos; i < line.length(); ++i) {
            if ((lexic.isSeparator(String.valueOf(line.charAt(i))) || lexic.isOperator(String.valueOf(line.charAt(i)))) && ((i == line.length() - 2 && line.charAt(i + 1) != '\"') || (i == line.length() - 1)))
                break;
            constant.append(line.charAt(i));
            if (line.charAt(i) == '\"' && i != pos)
                break;
        }

        return constant.toString();
    }

    public void buildPIF(List<Pair<String, Integer>> tokenList) {
        List<String> invalidTokens = new ArrayList<>();
        boolean isLexicallyCorrect = true;
        for (Pair<String, Integer> tokenPair : tokenList) {
            String token = tokenPair.getKey();

            if (lexic.isOperator(token) || lexic.isReservedWord(token) || lexic.isSeparator(token)) {
                int code = lexic.getCode(token);
                pif.add(code, new Pair<>(-1, -1));
            } else if (lexic.isIdentifier(token)) {
                identifierST.put(token);
                Pair<Integer, Integer> position = identifierST.position(token);
                pif.add(0, position);
            } else if (lexic.isConstant(token)) {
                constantST.put(token);
                Pair<Integer, Integer> position = constantST.position(token);
                pif.add(1, position);
            } else if (!invalidTokens.contains(token)) {
                invalidTokens.add(token);
                isLexicallyCorrect = false;
                System.out.println("Error at line " + tokenPair.getValue() + ": invalid token " + token);
            }
        }

        if (isLexicallyCorrect) {
            System.out.println("Program is lexically correct");
        }
    }

    public void writeResults() {
        try {
            File pifFile = new File(PIFFile);
            FileWriter pifFileWriter = new FileWriter(pifFile, false);
            BufferedWriter pifWriter = new BufferedWriter(pifFileWriter);
            pifWriter.write(pif.toString());
            pifWriter.close();

            File identifierSymbolTableFile = new File(identifierSTFile);
            File constantSymbolTableFile = new File(constantSTFile);
            FileWriter identifierSymbolTableFileWriter = new FileWriter(identifierSymbolTableFile, false);
            FileWriter constantSymbolTableFileWriter = new FileWriter(constantSymbolTableFile, false);

            BufferedWriter identifierSymbolTableWriter = new BufferedWriter(identifierSymbolTableFileWriter);
            BufferedWriter constantSymbolTableWriter = new BufferedWriter(constantSymbolTableFileWriter);

            identifierSymbolTableWriter.write(identifierST.toString());
            constantSymbolTableWriter.write(constantST.toString());

            identifierSymbolTableWriter.close();
            constantSymbolTableWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scan() {
        // here we need to scan the program file line by line
        // then to tokenize it in order to have it according to our lexical rules
        // in order to build the PIF

        List<Pair<String, Integer>> tokenPairs = new ArrayList<>();
        try {
            File file = new File(programFile);
            java.util.Scanner reader = new java.util.Scanner(file);

            int lineNr = 1;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                List<String> tokenList = tokenize(line);

                for (String token : tokenList) {
                    tokenPairs.add(new Pair<>(token, lineNr));
                }

                lineNr++;
            }

            reader.close();

            buildPIF(tokenPairs);
            writeResults();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

