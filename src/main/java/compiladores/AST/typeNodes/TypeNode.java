package compiladores.AST.typeNodes;

import compiladores.semantic.ASTVisitor;

public abstract class TypeNode {
    protected String type;

    public TypeNode(String type) {
        this.type = type;
    }
    public String getName() {
        return type;
    }

    public abstract void accept(ASTVisitor visitor);
}
