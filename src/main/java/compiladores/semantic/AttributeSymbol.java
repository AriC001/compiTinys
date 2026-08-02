package compiladores.semantic;

import compiladores.AST.typeNodes.TypeNode;

public class AttributeSymbol extends VariableSymbol {

    private boolean isPublic;

    public AttributeSymbol(String name,
                           TypeNode type,
                           boolean isPublic) {
        super(name, type);
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
