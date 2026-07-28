package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class DivNode extends ExpresionNode{
    ExpresionNode left;
    ExpresionNode right;

    public DivNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.DivNode,"/");
        this.left = left;
        this.right = right;
    }
}
