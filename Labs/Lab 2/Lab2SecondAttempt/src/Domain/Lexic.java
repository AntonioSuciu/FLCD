package Domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Lexic {

    private HashMap<String, Integer> codification = new HashMap<>();
    private List<String> operators =
            Arrays.asList("+", "-", "*", "/", "=", "<", "<=", "==", ">", ">=", "!=", "+=", "-=", "*=", "/=");
    private List<String> separators =
            Arrays.asList( "[", "]", "{", "}", "(", ")", ":", ";", "'","\"", ".");
    private List<String> reservedWords =
            Arrays.asList("array", "char", "const", "declare", "do", "else", "for", "if","in", "integer", "main",
                    "of", "read", "then", "var", "while", "write", "ARRAY", "CHAR", "CONST", "DECLARE", "DO", "ELSE",
                    "FOR", "IF", "INTEGER", "MAIN", "IN",
                    "OF", "READ", "THEN", "VAR", "WHILE", "WRITE");

    public Lexic()
    {
        codify();
    }

    private void codify()
    {
        codification.put("identifier", 0);
        codification.put("constant", 1);

        int code = 2;

        for (String op : operators)
        {
            codification.put(op , code);
            code++;
        }

        for (String sep :  separators)
        {
            codification.put(sep, code);
            code++;
        }

        for (String resWord : reservedWords)
        {
            codification.put(resWord, code);
            code++;
        }


    }

    public boolean isPartOfOperator(char op)
    {
        return isOperator(String.valueOf(op)) || op == '!';
    }

    public boolean isOperator(String token) {
        return operators.contains(token);
    }

    public boolean isSeparator(String token)
    {
        return separators.contains(token);
    }

    public boolean isReservedWord(String token)
    {
        return reservedWords.contains(token);
    }
    public boolean isConstant(String token)
    {

//        c. Constants
//        1. Integers:
//          integer = [sign] nzd {digit} | "0"
//          sign = "+" | "-"
//          nzd = "1" | ... | "9"
//          numconst = nzd {digit}
//
//        2. Characters:
//          char = letter | digit
//
//          charconst = "'" char "'"
//
//          string = ``"`` string ``"``
//
//        (**) i used `` `` to escape the double quote
//
//          stringconst = "char{string}"
//         String numconstRegex = "^0|[+|-][1-9](\\d)*|[1-9](\\d)*|[+|-][1-9](\\d)*\\.(\\d)*|[1-9](\\d)*\\.(\\d)*$";
        String numconstRegex = "^0|[+|-][1-9](\\d)*|[1-9](\\d)*|[+|-][1-9](\\d)*$";
        /// zero | sign nzd {zd} | nzd {zd} |
        String charconstRegex = "^'[a-zA-Z0-9_?!#*./%+=<>;)(}{ ]'";
        String stringconstRegex = "^\"[a-zA-Z0-9_?!#*./%+=<>;)(}{ ]+\"";
        return token.matches(numconstRegex) ||
                token.matches(charconstRegex) ||
                token.matches(stringconstRegex);
    }

    public boolean isIdentifier(String token)
    {
//        b. Identifiers
//          - a sequence of characters, where
//          - first character is a letter
//          - the next characters may be 0 or more letters or digits, in no particular order
//
//        digit = "0" | "1" | ... | "9"
//        letter = "a" | "b" | "c" |...| "z" | "A" | "B" | ... | "Z"
//        letter
//        identifier = letter {(letter|digit)}
        String identifierRegex ="^[a-zA-Z]([a-z|A-Z|\\d])*$";
        return token.matches(identifierRegex);
    }

    public Integer getCode(String token)
    {
        return codification.get(token);
    }


}
