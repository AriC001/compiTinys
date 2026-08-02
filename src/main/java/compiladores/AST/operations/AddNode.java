package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class AddNode extends ExpresionNode {
    private ExpresionNode left;
    private ExpresionNode right;

    public AddNode(String value, ExpresionNode left, ExpresionNode right) {
        super(NodeType.AddNode, value);
        this.left = left;
        this.right = right;
    }

    public ExpresionNode getLeft() {
        return left;
    }

    public void setLeft(ExpresionNode left) {
        this.left = left;
    }

    public ExpresionNode getRight() {
        return right;
    }

    public void setRight(ExpresionNode right) {
        this.right = right;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
