package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class EqualNode extends ExpresionNode{
    private ExpresionNode left;
    private ExpresionNode right;

    public EqualNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.EqualNode, "==");
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
}
