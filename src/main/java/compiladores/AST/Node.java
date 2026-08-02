package compiladores.AST;

import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

public abstract class Node {
    public abstract void accept(ASTVisitor visitor);
}
