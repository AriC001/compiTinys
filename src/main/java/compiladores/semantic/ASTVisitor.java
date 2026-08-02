package compiladores.semantic;

import compiladores.AST.*;
import compiladores.AST.Definitions.*;
import compiladores.AST.Statements.*;
import compiladores.AST.literals.*;
import compiladores.AST.operations.*;
import compiladores.AST.typeNodes.*;

public interface ASTVisitor {

        //Definitions
        void visit(ClassNode n);
        void visit(ConstructorNode n);
        void visit(MethodNode n);
        void visit(ImplNode n);
        // ---
        //Literals
        void visit(BooleanLiteralNode n);
        void visit(IntegerLiteralNode n);
        void visit(NilLiteralNode n);
        void visit(StringLiteralNode n);
        // ---
        //operations
        void visit(AddNode n);
        void visit(AndNode n);
        void visit(BinaryOperation n);
        void visit(DivNode n);
        void visit(EqualNode n);
        void visit(GraterNode n);
        void visit(GraterOrEqualNode n);
        void visit(LessNode n);
        void visit(MulNode n);
        void visit(NotNode n);
        void visit(OrNode n);
        void visit(SubstractionNode n);
        void visit(UnaryOperation n);
        // ---
        //Statements
        void visit(AssigNode n);
        void visit(BlockNode n);
        void visit(ForNode n);
        void visit(IfNode n);
        void visit(RetNode n);
        void visit(SentenceNode n);
        void visit(WhileNode n);
        // ---
        //types
        void visit(ArrayTypeNode n);
        void visit(AttributeNode n);
        void visit(PrimitiveTypeNode n);
        void visit(ReferenceTypeNode n);
        void visit(VoidTypeNode n);
        // ---

        void visit(ArrayAccessNode n);
        void visit(ArrayCreationNode n);
        void visit(AstNode n);
        void visit(ConstructorCallNode n);
        void visit(ExpresionNode n);
        void visit(FieldAccessNode n);
        void visit(FormalParameterNode n);
        void visit(MethodCallNode n);
        void visit(NewNode n);
        void visit(ProgramNode n);
        void visit(SelfNode n);
        void visit(SimpleSentenceNode n);
        void visit(StartNode n);
        void visit(StaticMethodCallNode n);
        void visit(VariableDeclarationNode n);
        void visit(VarNode n);
        void visit(VisibilityNode n);
}
