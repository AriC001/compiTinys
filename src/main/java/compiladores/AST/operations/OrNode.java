package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class OrNode extends ExpresionNode {
    ExpresionNode left;
    ExpresionNode right;

    public OrNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.OrNode, "||");
        this.left = left;
        this.right = right;
    }
}
