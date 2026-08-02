package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

import java.util.HashMap;
import java.util.Map;

public class MethodSymbol extends Symbol  {
    private Map<String, ParameterSymbol> parameters = new HashMap<>();

    private Map<String, VariableSymbol> locals = new HashMap<>();

    public MethodSymbol(String name, TypeNode returnType) {
        super(name, returnType);
    }

    public Map<String, ParameterSymbol> getParameters() {
        return parameters;
    }

    public Map<String, VariableSymbol> getLocals() {
        return locals;
    }
}
