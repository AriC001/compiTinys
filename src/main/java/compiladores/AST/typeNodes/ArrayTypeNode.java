package compiladores.AST.typeNodes;

import compiladores.semantic.ASTVisitor;

public class ArrayTypeNode extends TypeNode {
    private TypeNode elementType;

    public ArrayTypeNode(TypeNode elementType) {
        super("array");
        this.elementType = elementType;
    }

    public TypeNode getElementType() {
        return elementType;
    }

    public void setElementType(TypeNode elementType) {
        this.elementType = elementType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
