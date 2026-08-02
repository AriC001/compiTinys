package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;
import compiladores.semantic.ASTVisitor;

public class FormalParameterNode {
    TypeNode tipo;

    String nombre;

    public FormalParameterNode(TypeNode tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public TypeNode getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
