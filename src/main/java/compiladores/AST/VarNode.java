package compiladores.AST;

import compiladores.semantic.ASTVisitor;

public class VarNode extends ExpresionNode {
    private String name;

    public VarNode(String name) {
        super(NodeType.VarNode, name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}