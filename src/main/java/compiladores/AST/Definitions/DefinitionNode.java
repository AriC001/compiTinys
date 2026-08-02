package compiladores.AST.Definitions;

import compiladores.AST.Node;
import compiladores.semantic.ASTVisitor;

public abstract class DefinitionNode extends Node {
    private String name;

    public DefinitionNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void accept(ASTVisitor visitor);
}
