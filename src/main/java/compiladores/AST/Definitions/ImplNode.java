package compiladores.AST.Definitions;

import compiladores.semantic.ASTVisitor;

import java.util.List;

public class ImplNode extends DefinitionNode {
    private List<DefinitionNode> miembros;
    public ImplNode(String name, List<DefinitionNode> miembros) {
        super(name);
        this.miembros = miembros;
    }

    public List<DefinitionNode> getMiembros() {
        return miembros;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
