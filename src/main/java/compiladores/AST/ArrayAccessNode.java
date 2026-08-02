package compiladores.AST;

import compiladores.semantic.ASTVisitor;

public class ArrayAccessNode extends ExpresionNode {
    private ExpresionNode array;
    private ExpresionNode index;

    public ArrayAccessNode(ExpresionNode array, ExpresionNode index) {
        super(NodeType.ArrayAccessNode, null);
        this.array = array;
        this.index = index;
    }

    public ExpresionNode getArray() {
        return array;
    }

    public void setArray(ExpresionNode array) {
        this.array = array;
    }

    public ExpresionNode getIndex() {
        return index;
    }

    public void setIndex(ExpresionNode index) {
        this.index = index;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
