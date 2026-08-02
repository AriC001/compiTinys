package compiladores.AST;

import compiladores.semantic.ASTVisitor;

public class AstNode {

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
