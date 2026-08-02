package compiladores.AST;

import compiladores.semantic.ASTVisitor;

public class SelfNode extends ExpresionNode {

    public SelfNode() {
        super(NodeType.SelfNode, "self");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
