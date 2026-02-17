public class Node {
    public String value;
    public List<Node> children;
    public Node parent;
    public int depth;

    public Node(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void printTree(String prefix) {
        System.out.println(prefix + "|___" + value);
        for (Node child : children) {
            child.printTree(prefix + "     ");
        }
    }
}