package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;
import compiladores.semantic.ASTVisitor;

public class ArrayCreationNode extends ExpresionNode {
    TypeNode elementType;
    ExpresionNode size;

    public ArrayCreationNode(TypeNode elementType, ExpresionNode size) {
        super(NodeType.ArrayCreationNode, null);
        this.elementType = elementType;
        this.size = size;
    }

    public TypeNode getElementType() {
        return elementType;
    }

    public ExpresionNode getSize() {
        return size;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
