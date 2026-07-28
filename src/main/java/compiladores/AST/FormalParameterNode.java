package compiladores.AST;

import compiladores.AST.typeNodes.TypeNode;

public class FormalParameterNode {
    TypeNode tipo;

    String nombre;

    public FormalParameterNode(TypeNode tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }
}
