package compiladores.AST.Definitions;

import java.util.List;

public class ImplNode extends DefinitionNode {
    private List<DefinitionNode> miembros;
    public ImplNode(String name, List<DefinitionNode> miembros) {
        super(name);
        this.miembros = miembros;
    }
}
