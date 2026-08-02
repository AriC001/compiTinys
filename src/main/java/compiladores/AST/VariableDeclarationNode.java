package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;
import compiladores.semantic.ASTVisitor;

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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
