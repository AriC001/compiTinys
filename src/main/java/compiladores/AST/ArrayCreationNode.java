package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;

public class ArrayCreationNode extends ExpresionNode {
    TypeNode elementType;
    ExpresionNode size;

    public ArrayCreationNode(TypeNode elementType, ExpresionNode size) {
        super(NodeType.ArrayCreationNode, null);
        this.elementType = elementType;
        this.size = size;
    }
}
