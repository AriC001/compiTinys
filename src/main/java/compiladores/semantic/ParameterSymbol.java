package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

public class ParameterSymbol extends VariableSymbol {

    public ParameterSymbol(String name, TypeNode type) {
        super(name, type);
    }
}
