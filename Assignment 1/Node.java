import java.util.ArrayList;
import java.util.List;

public class Node {
    public String value;
    public List<Node> children;
    public Node parent;
    public int depth;
    public String type;
    public double fitness;

    public Node(String value, String type) {
        this.value = value;
        this.children = new ArrayList<>();
        this.type = type;
    }

    public Node(String value, String type, List<Node> children){
        this.value = value;
        this.children = children;
        this.type = type;
    }

    public void printTree(String indent) {
        System.out.println(indent + "|___ " + value + " [" + type + "]");
        for (Node child : children) {
            child.printTree(indent + "     ");
        }
    }

    public Node copy() {
        List<Node> clonedChildren = new ArrayList<>();

        for (Node child : this.children) {
            clonedChildren.add(child.copy());
        }

        return new Node(this.value, this.type, clonedChildren);
    }
}