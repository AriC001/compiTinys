package compiladores.semantic;

import java.util.HashMap;
import java.util.Map;

public class ConstructorSymbol extends MethodSymbol {
    String className;
    private final Map<String, ParameterSymbol> parameters = new HashMap<>();

    public ConstructorSymbol(String className) {
        super(".", null);
        this.className = className;
    }

    public ConstructorSymbol(String className, Map<String, ParameterSymbol> parameters) {
        super(".", null);
        this.className = className;
        this.parameters.putAll(parameters);
    }

    public Map<String, ParameterSymbol> getParameters() {
        return parameters;
    }
    public String getClassName() {
        return className;
    }
}
