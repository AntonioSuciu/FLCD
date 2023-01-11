import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class ParserOutput {
    private Parser parser;
    private List<Integer> productions;
    private Integer nodeNumber = 1;
    private Boolean hasErrors;
    private List<Node> nodeList = new ArrayList<>();
    private Node root;
    private String outputFile;

    public ParserOutput(Parser parser, List<String> sequence, String outputFile){
        this.parser = parser;
        this.productions = parser.analyseSequence(sequence);
        this.hasErrors = this.productions.contains(-1);
        this.outputFile = outputFile;
        generateTree();
    }

    public void generateTree(){
        if(hasErrors)
            return;

        // we create a stack to store the nodes of the parse tree as they are constructed.
        Stack<Node> nodeStack = new Stack<>();

        // we keep track of the current production rule being used.
        var productionsIndex = 0;

        //root + initialization
        Node node = new Node();
        node.setParent(0);
        node.setSibling(0);
        node.setHasRight(false);
        node.setIndex(nodeNumber);
        nodeNumber++;
        node.setValue(parser.getGrammar().getS());
        nodeStack.push(node);
        nodeList.add(node);

        // we save the root node for later reference.
        this.root = node;


        // while there are still production rules to be used and the node stack is not empty:
        while(productionsIndex < productions.size() && !nodeStack.isEmpty()){
            Node currentNode = nodeStack.peek(); // we get the current node
            // (i.e., the top of the stack / the father).
            if(parser.getGrammar().getSigma().contains(currentNode.getValue()) || currentNode.getValue().contains("epsilon")){

                // while the node stack is not empty and the top node does not have a right child:
                while(nodeStack.size()>0 && !nodeStack.peek().getHasRight()) {
                    // we pop the top node from the stack.
                    nodeStack.pop();
                }
                if(nodeStack.size() > 0)
                    nodeStack.pop();
                else
                    break;
                continue;
            }

            // the children, the the production rule to be used.
            var production = parser.getProductionByOrderNumber(productions.get(productionsIndex));
            nodeNumber+=production.size()-1;

            // for each symbol in the production (starting from the rightmost symbol):
            for(var i=production.size()-1;i>=0;i--){
                Node child = new Node();
                // we set the child's parent to be the current node.
                child.setParent(currentNode.getIndex());

                child.setValue(production.get(i));
                child.setIndex(nodeNumber);
                if(i==0)
                    // If this is the leftmost child:, we set the sibling to be 0
                    child.setSibling(0);
                else
                    // else, we set the child's sibling to be the node to its left.
                    child.setSibling(nodeNumber-1);
                child.setHasRight(i != production.size() - 1);

                nodeNumber--;
                nodeStack.push(child);
                nodeList.add(child);
            }
            nodeNumber+=production.size()+1;
            productionsIndex++;
        }
    }

    public void printTree(){
        try {
            nodeList.sort(Comparator.comparing(Node::getIndex));
            File file = new File(outputFile);
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Index | Value | Parent | Sibling"+ "\n");
            for (Node node : nodeList) {
                bufferedWriter.write(node.getIndex() + " | " + node.getValue() + " | " + node.getParent() + " | " + node.getSibling() + "\n");
            }
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}