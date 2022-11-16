package Domain;

import java.util.ArrayList;
import javafx.util.Pair;

public class SymbolTable {

    private ArrayList<ArrayList<String>> keys;
    private int size;


    public SymbolTable(int givenSize)
    {
        this.size = givenSize;
        this.keys = new ArrayList<>();
        // the "hash table" column
        for (int i=0; i < givenSize; i++)
        {
            this.keys.add(new ArrayList<>());
        }
    }

    public int getSize()
    {
        return size;
    }

    private int hashFunction (String key)
    {

        // The hashFunction is computed by summing up all of the ascii codes of the characters
        // of the key, divided by the length of the table
        int sum = 0;
        for(int i = 0; i< key.length(); i++)
        {
            // sum = sum + key.charAt(i);
            sum = sum + (int) key.charAt(i);
        }
        return sum % this.size;
    }

    public boolean contains(String key)
    {
        int hashval = hashFunction(key);
        // we compute the hash value of the key

        return keys.get(hashval).contains(key);
        // we look it up at the specific hashvalue and we return
        // whether we find it
    }

    public int put(String key)
    {
        int hashval = hashFunction(key);
        if (!keys.get(hashval).contains(key)) {
            keys.get(hashval).add(key);
        }
        return keys.get(hashval).indexOf(key);
        // otherwise, we add it and we return its position

    }

//    public int position(String key)
//    {
//        int hashval = hashFunction(key);
//        if(keys.get(hashval).contains(key))
//        {
//           return keys.get(hashval).indexOf(key);
//        }
//        return -1;
//    }
//
    public Pair<Integer, Integer> position(String key)
    {
        int hashval = hashFunction(key);
        if(keys.get(hashval).contains(key))
        {
            int listPos = 0;
            for(String el:this.keys.get(hashval)) {
                if (!el.equals(key))
                    listPos++;
                else
                    break;
            }

            return new Pair<>(hashval, listPos);
        }
        return new Pair<>(-1, -1);

    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<size; ++i) {
            result.append(i).append(": [");
            String separator = "";
            for(String key: keys.get(i)){
                result.append(separator);
                separator = ", ";
                result.append(key);
            }
            result.append("]\n");
        }
        return result.toString();
    }
}

