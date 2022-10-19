
public class SymbolTable {
    static private class NodeList
    {
        // holds keys
        // used to implement linked lists
        // H(K1) = H(K2) <=> We add K1, K2 in the same list
        // holds a (K, V) pair
        Object key;
        Object value;
        NodeList nextNode;
        // acts as a pointer to the nextNode
        // nextNode == null <=> the end of the list
    }

    private NodeList[] table;
    // the hash table -> array of linked lists

    private int size;

    public SymbolTable(int givenSize)
    {
        // create a hashtable with a given size
        this.table = new NodeList[givenSize];
    }

    public int getSize()
    {
        return size;
    }

    private int hashFunction(Object key)
    {
        // The hashFunction is computed by summing up all of the ascii codes of the characters
        // of the key, divided by the length of the table
        int sum = 0;
        for(int i = 0; i<key.length(); i++)
        {
            // sum = sum + key.charAt(i);
            sum = sum + (int) key.charAt(i);
        }

        return sum % table.length;

    }

    public boolean contains(Object key)
    {
        int bucket = hashFunction(key);
        // to know where to look for it
        NodeList list = table[bucket];
        // for traversing the list
        while(list != null)
        {
            if(list.key.equals(key))
                return true;
            list = list.nextNode;
        }
        return false;
    }

    public void put(String key)
    {
        int bucket = hashFunction(key);
        NodeList list = table[bucket];
        // for traversing the linked list
        while(list != null)
        {
            // search the nodes in the list, to see if the key already exists.
            if (list.key.equals(key))
                break;
            list = list.nextNode;
        }
        // at this point, we either have a null list, or list.key.equals(key)
        if (list != null)
        {
            list.value = hashFunction(key);
        }
        else
        {
            // the list is null, so we don't have the key
            // we add a new node

            NodeList newNode = new NodeList();
            newNode.key = key;
            newNode.value = hashFunction(key);
            newNode.nextNode = table[bucket];
            table[bucket] = newNode;
            size++;
        }
    }


}
