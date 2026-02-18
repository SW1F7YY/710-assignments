import java.util.ArrayList;
import java.util.List;

public class Node {
    public String value;
    public List<Node> children;
    public Node parent;
    public int depth;
    public String type;

    public Node(String value, String type) {
        this.value = value;
        this.children = new ArrayList<>();
        this.type = type;
    }

    public void printTree(String indent) {
        System.out.println(indent + "|___ " + value + " [" + type + "]");
        for (Node child : children) {
            // If the current node is a function, its children should indent further
            child.printTree(indent + "     ");
        }
    }
}