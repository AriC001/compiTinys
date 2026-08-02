package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

public class VariableSymbol extends Symbol {

    public VariableSymbol(String name, TypeNode type) {
        super(name, type);
    }
}
