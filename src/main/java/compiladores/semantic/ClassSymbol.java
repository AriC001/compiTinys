package compiladores.semantic;

import java.util.HashMap;
import java.util.Map;

public class ClassSymbol extends Symbol {

    private ClassSymbol parent;

    private Map<String, AttributeSymbol> attributes = new HashMap<>();

    private Map<String, MethodSymbol> methods = new HashMap<>();

    private ConstructorSymbol constructor;

    public ClassSymbol(String name) {
        super(name, null);
    }

    public Map<String, AttributeSymbol> getAttributes() {
        return attributes;
    }

    public Map<String, MethodSymbol> getMethods() {
        return methods;
    }

    public ConstructorSymbol getConstructor() {
        return constructor;
    }

    public void setConstructor(ConstructorSymbol constructor) {
        this.constructor = constructor;
    }

    public ClassSymbol getParent() {
        return parent;
    }

    public void setParent(ClassSymbol parent) {
        this.parent = parent;
    }
}
