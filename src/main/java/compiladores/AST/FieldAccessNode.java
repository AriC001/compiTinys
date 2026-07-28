package compiladores.AST;

public class FieldAccessNode extends ExpresionNode {
    private ExpresionNode object;
    private String fieldName;

    public FieldAccessNode(ExpresionNode object, String fieldName) {
        super(NodeType.FieldAccessNode, null);
        this.object = object;
        this.fieldName = fieldName;
    }

    public ExpresionNode getObject() {
        return object;
    }

    public void setObject(ExpresionNode object) {
        this.object = object;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
