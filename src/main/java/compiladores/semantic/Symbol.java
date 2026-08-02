package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

public abstract class Symbol {

    protected final String name;
    protected final TypeNode type;

    protected Symbol(String name, TypeNode type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeNode getType() {
        return type;
    }
}
