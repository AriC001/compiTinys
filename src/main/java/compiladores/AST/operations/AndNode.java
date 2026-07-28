package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class AndNode extends ExpresionNode {
    ExpresionNode left;
    ExpresionNode right;

    public AndNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.AndNode, "&&");
        this.left = left;
        this.right = right;
    }
}
