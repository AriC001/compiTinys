package compiladores.AST.Statements;

import compiladores.AST.NodeType;
import compiladores.AST.VariableDeclarationNode;

import java.util.List;

public class BlockNode extends SentenceNode {
    private List<VariableDeclarationNode> variables;
    List<SentenceNode> statements;

    public BlockNode(List<VariableDeclarationNode> variables, List<SentenceNode> statements) {
        super(NodeType.BlockNode, "Block");
        this.variables = variables;
        this.statements = statements;
    }

    public BlockNode(List<SentenceNode> statements) {
        super(NodeType.BlockNode, "Block");
        this.statements = statements;
    }

    public List<VariableDeclarationNode> getVariables() {
        return variables;
    }

    public List<SentenceNode> getStatements() {
        return statements;
    }
}
