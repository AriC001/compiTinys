package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;

public class VariableDeclarationNode extends AstNode {

    private TypeNode type;

    private String name;

    public VariableDeclarationNode(TypeNode type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
