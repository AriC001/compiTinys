package compiladores.AST;

public class StartNode extends Node {
    private Node root;

    public StartNode(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
}
