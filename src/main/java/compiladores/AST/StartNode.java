package compiladores.AST;

import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
