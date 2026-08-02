package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
    private Map<String, ClassSymbol> classes = new HashMap<>();

    private ClassSymbol currentClass;
    private MethodSymbol currentMethod;

    public boolean addClass(ClassSymbol c) {
        return classes.put(c.getName(), c) == null;
    }
    public Optional<ClassSymbol> getClass(String name) {
        return Optional.ofNullable(classes.get(name));
    }
    public void setCurrentClass(ClassSymbol c) {
        currentClass = c;
    }
    public void setCurrentMethod(MethodSymbol m) {
        currentMethod = m;
    }
    public ClassSymbol getCurrentClass() {
        return currentClass;
    }
    public MethodSymbol getCurrentMethod() {
        return currentMethod;
    }

    public VariableSymbol resolveVariable(String name) {

        if (currentMethod != null) {

            if (currentMethod.getLocals().containsKey(name))
                return currentMethod.getLocals().get(name);

            if (currentMethod.getParameters().containsKey(name))
                return currentMethod.getParameters().get(name);
        }

        if (currentClass != null) {

            if (currentClass.getAttributes().containsKey(name))
                return currentClass.getAttributes().get(name);
        }

        return null;
    }
}



