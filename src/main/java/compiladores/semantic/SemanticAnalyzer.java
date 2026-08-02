package compiladores.semantic;

import compiladores.AST.*;
import compiladores.AST.Definitions.*;
import compiladores.AST.Statements.*;
import compiladores.AST.literals.*;
import compiladores.AST.operations.*;
import compiladores.AST.typeNodes.*;

import java.util.Optional;

public class SemanticAnalyzer implements ASTVisitor {
    private final SymbolTable symbolTable;
    private ClassSymbol currentClass;
    private MethodSymbol currentMethod;
    private TypeNode lastType;
    private boolean traceEnabled;
    private int traceDepth;
    private final StringBuilder traceLog = new StringBuilder();
    private ConstructorSymbol currentConstructor;

    private static final PrimitiveTypeNode INT_TYPE = new PrimitiveTypeNode("Int");
    private static final PrimitiveTypeNode BOOL_TYPE = new PrimitiveTypeNode("Bool");
    private static final PrimitiveTypeNode STR_TYPE = new PrimitiveTypeNode("Str");
    private static final PrimitiveTypeNode NIL_TYPE = new PrimitiveTypeNode("Nil");

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        initializeBuiltins();
    }

    private void initializeBuiltins() {

        ClassSymbol object = new ClassSymbol("Object");
        symbolTable.addClass(object);

        createIO();
        //ClassSymbol io = new ClassSymbol("IO");
        //io.setParent(object);
        //symbolTable.addClass(io);

        ClassSymbol string = new ClassSymbol("Str");
        string.setParent(object);
        symbolTable.addClass(string);

        ClassSymbol integer = new ClassSymbol("Int");
        integer.setParent(object);
        symbolTable.addClass(integer);

        ClassSymbol bool = new ClassSymbol("Bool");
        bool.setParent(object);
        symbolTable.addClass(bool);
    }

    private void createIO(){
        ClassSymbol io = new ClassSymbol("IO");
        io.setParent(resolveClass("Object"));
        io.getMethods().put("out_int",  new MethodSymbol("out_int", null));
        io.getMethods().put("out_str", new MethodSymbol("out_str", null));
        io.getMethods().put("in_int", new MethodSymbol("in_int", INT_TYPE));
        io.getMethods().put("in_str", new MethodSymbol("in_str", STR_TYPE));
        //System.out.println("IO methods: " + io.getMethods().keySet());

        symbolTable.addClass(io);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }


    public TypeNode debugTypeOf(ExpresionNode expr) {
        //System.out.println("debugTypeOf");
        traceLog.setLength(0);
        traceDepth = 0;
        traceEnabled = true;
        try {
            TypeNode result = typeOf(expr);
            appendTrace("RESULT " + describeExpr(expr) + " => " + describeType(result));
            return result;
        } finally {
            traceEnabled = false;
        }
    }

    public String getLastTypeTrace() {
        return traceLog.toString();
    }

    private void declareClass(ClassSymbol symbol) {
        //System.out.println("[CLASS] " + symbol.getName());
        if (!symbolTable.addClass(symbol)) {
            throw new RuntimeException("Class already declared: " + symbol.getName());
        }
    }

    private void declareMethod(MethodSymbol symbol) {
        //System.out.println("[METHOD] " + symbol.getName());
        if (!currentClass.getMethods().containsKey(symbol.getName())) {
            currentClass.getMethods().put(symbol.getName(), symbol);
        } else {
            throw new RuntimeException("Duplicate method " + symbol.getName());
        }
    }

    private void declareConstructor(ConstructorSymbol symbol) {
        if (currentClass.getConstructor() != null) {
            throw new RuntimeException("Duplicate constructor");
        }

        currentClass.setConstructor(symbol);
    }

    private void declareVariable(VariableSymbol symbol) {
        if (currentMethod != null) {
            if (currentMethod.getLocals().containsKey(symbol.getName())) {
                throw new RuntimeException("Variable ya declarada " + symbol.getName());
            }
            currentMethod.getLocals().put(symbol.getName(), symbol);
            return;
        }
        else if (currentConstructor != null) {
            if (currentConstructor.getLocals().containsKey(symbol.getName())) {
                throw new RuntimeException("Variable ya declarada " + symbol.getName());
            }
            currentConstructor.getLocals().put(symbol.getName(), symbol);
            return;
        }
        throw new RuntimeException("Variable declarada fuera de metodo/constructor");
    }

    private void declareAttribute(AttributeSymbol symbol) {
        if (currentClass == null) {
            throw new RuntimeException("Attribute declared outside class");
        }
        if (currentClass.getAttributes().containsKey(symbol.getName())) {
            throw new RuntimeException("Duplicate attribute " + symbol.getName());
        }
        currentClass.getAttributes().put(symbol.getName(), symbol);
    }
    private void declareParameter(ParameterSymbol symbol) {
        if (currentMethod != null) {
            if (currentMethod.getParameters().containsKey(symbol.getName())) {
                throw new RuntimeException("Duplicate parameter " + symbol.getName());
            }
            currentMethod.getParameters().put(symbol.getName(), symbol);
            return;
        }
        else if (currentConstructor != null) {
            if (currentConstructor.getParameters().containsKey(symbol.getName())) {
                throw new RuntimeException("Duplicate parameter " + symbol.getName());
            }
            currentConstructor.getParameters().put(symbol.getName(), symbol);
            return;
        }
        throw new RuntimeException("Parameter declared outside method/constructor");
    }

    private VariableSymbol resolveVariable(String name) {
        if (currentMethod != null) {
            VariableSymbol local = currentMethod.getLocals().get(name);
            if (local != null){return local;}
            ParameterSymbol param = currentMethod.getParameters().get(name);
            if (param != null){return param;}
        }
        if (currentClass != null) {
            AttributeSymbol attr = currentClass.getAttributes().get(name);
            if (attr != null){return attr;}
        }
        throw new RuntimeException("Variable no declarada: " + name);
    }

    private MethodSymbol resolveMethod(ClassSymbol clazz, String name) {
        if (clazz == null) {
            throw new RuntimeException("Cannot resolve method '" + name + "' without a receiver class");
        }
        //System.out.println("Buscando " + name + " en " + clazz.getName());
        while (clazz != null) {
            //System.out.println("  clase = " + clazz.getName());
            MethodSymbol local = clazz.getMethods().get(name);
            if (local != null){return local;}
            clazz = clazz.getParent();
        }
        throw new RuntimeException("Metodo no declarado: " + name);
    }

    private ClassSymbol resolveReceiverClass(TypeNode parentType) {
        if (parentType == null) {
            if (currentClass == null) {
                throw new RuntimeException("Method call without receiver outside of a class context");
            }
            return currentClass;
        }
        if (parentType instanceof ReferenceTypeNode refType) {
            return resolveClass(refType.getName());
        }
        if (parentType instanceof PrimitiveTypeNode primType) {
            return resolveClass(primType.getType());
        }
        throw new RuntimeException("Cannot call method on non-object type: " + describeType(parentType));
    }

    private ClassSymbol resolveClass(String name) {
        if (symbolTable != null) {
            Optional<ClassSymbol> local = symbolTable.getClass(name);
            if (local.isPresent()){return local.get();}
        }
        throw new RuntimeException("Clase no declarado: " + name);
    }

    private boolean isPrimitive(TypeNode t, String name) {
        return t instanceof PrimitiveTypeNode && name.equals(((PrimitiveTypeNode) t).getType());
    }

    private boolean isInt(TypeNode t) {
        return isPrimitive(t, "Int");
    }

    private boolean isBool(TypeNode t) {
        return isPrimitive(t, "Bool");
    }

    private boolean isString(TypeNode t) {
        return isPrimitive(t, "Str");
    }

    private boolean isNil(TypeNode t) {
        return isPrimitive(t, "Nil");
    }


    private TypeNode typeOf(ExpresionNode expr) {
        if (expr == null) {
            return null;
        }
        if (traceEnabled) {
            appendTrace("ENTER " + describeExpr(expr));
            traceDepth++;
        }
        //System.out.println(expr.getClass());
        //System.out.println(expr instanceof BinaryOperation);
        expr.accept(this);
        TypeNode result = lastType;
        if (traceEnabled) {
            traceDepth--;
            appendTrace("EXIT  " + describeExpr(expr) + " => " + describeType(result));
        }
        return result;
    }

    private void appendTrace(String line) {
        for (int i = 0; i < traceDepth; i++) {
            traceLog.append("  ");
        }
        traceLog.append(line).append(System.lineSeparator());
    }

    private String describeExpr(ExpresionNode expr) {
        if (expr == null) {
            return "<null-expr>";
        }
        return expr.getClass().getSimpleName() + "(" + expr.getValue() + ")";
    }

    private String describeType(TypeNode type) {
        if (type == null) {
            return "<null-type>";
        }
        if (type instanceof PrimitiveTypeNode) {
            return "PrimitiveType(" + ((PrimitiveTypeNode) type).getType() + ")";
        }
        if (type instanceof ReferenceTypeNode) {
            return "ReferenceType(" + ((ReferenceTypeNode) type).getName() + ")";
        }
        if (type instanceof ArrayTypeNode) {
            return "ArrayType(" + describeType(((ArrayTypeNode) type).getElementType()) + ")";
        }
        return type.getClass().getSimpleName();
    }

    private boolean isSameType(TypeNode a, TypeNode b) {
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof PrimitiveTypeNode && b instanceof PrimitiveTypeNode) {
            return ((PrimitiveTypeNode) a).getType().equals(((PrimitiveTypeNode) b).getType());
        }
        if (a instanceof ReferenceTypeNode && b instanceof ReferenceTypeNode) {
            return ((ReferenceTypeNode) a).getName().equals(((ReferenceTypeNode) b).getName());
        }
        if (a instanceof ArrayTypeNode && b instanceof ArrayTypeNode) {
            return isSameType(((ArrayTypeNode) a).getElementType(), ((ArrayTypeNode) b).getElementType());
        }
        return false;
    }

    private boolean isComparable(TypeNode left, TypeNode right) {
        return isSameType(left, right)
                || (isNil(left) && (right instanceof ReferenceTypeNode || right instanceof ArrayTypeNode))
                || (isNil(right) && (left instanceof ReferenceTypeNode || left instanceof ArrayTypeNode));
    }

    private boolean isAssignable(TypeNode target, TypeNode source) {
        return isComparable(target, source);
    }

    private boolean isZeroLiteral(ExpresionNode expr) {
        return expr instanceof IntegerLiteralNode && ((IntegerLiteralNode) expr).getIntValue() == 0;
    }

    @Override
    public void visit(ProgramNode node) {
        //pushScope();

        if (node.getClases() != null) {
            for (DefinitionNode definition : node.getClases()) {
                if (definition != null) {
                    definition.accept(this);
                }
            }
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        //popScope();

    }

    @Override
    public void visit(ClassNode node) { //public ClassNode(String name,TypeNode type,List<AttributeNode> parameters)
        ClassSymbol previous = currentClass;
        ClassSymbol symbol = new ClassSymbol(node.getName());
        declareClass(symbol);
        currentClass = symbol;
        String parent = node.getParent();
        if(parent.equals("Object")){
            currentClass.setParent(null);
        }else {
            currentClass.setParent(resolveClass(parent));
        }
        if (node.getParameters() != null) {
            for (AttributeNode attribute : node.getParameters()) {
                if (attribute != null) {
                    attribute.accept(this);
                }
            }
        }
        currentClass = previous;
    }

    @Override
    public void visit(ConstructorNode node) {
        ConstructorSymbol previous = currentConstructor;
        //ConstructorSymbol constructor = new ConstructorSymbol(node.getClassName());
        //currentClass.setConstructor(constructor);
        currentConstructor = currentClass.getConstructor();;
        for (FormalParameterNode p : node.getParameters()) {
            p.accept(this);
        }
        node.getBody().accept(this);

        currentConstructor = previous;
    }

    @Override
    public void visit(MethodNode node) {
        MethodSymbol previousMethod = currentMethod;
        currentMethod = resolveMethod(currentClass,node.getName());
        if (node.getParameters() != null) {
            for (FormalParameterNode parameter : node.getParameters()) {
                if (parameter != null) {
                    parameter.accept(this);
                }
            }
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        currentMethod = previousMethod;
    }

    @Override
    public void visit(ImplNode node) {
        ClassSymbol previousClass = currentClass;
        currentClass = resolveClass(node.getName());
        if (node.getMiembros() != null) {
            for (DefinitionNode member : node.getMiembros()) {
                if (member instanceof MethodNode m) {
                    declareMethod(new MethodSymbol(m.getName(), m.getReturnType()));
                }

                if (member instanceof ConstructorNode c) {
                    declareConstructor(new ConstructorSymbol(c.getClassName()));
                }
            }
            for (DefinitionNode member : node.getMiembros()) {
                member.accept(this);
            }
        }
        currentClass = previousClass;
    }

    @Override
    public void visit(BooleanLiteralNode node) {
        lastType = BOOL_TYPE;
    }

    @Override
    public void visit(IntegerLiteralNode node) {
        lastType = INT_TYPE;
    }

    @Override
    public void visit(NilLiteralNode node) {
        lastType = NIL_TYPE;
    }

    @Override
    public void visit(StringLiteralNode node) {
        lastType = STR_TYPE;
    }

    @Override
    public void visit(AddNode node) {
        TypeNode left = typeOf(node.getLeft());
        TypeNode right = typeOf(node.getRight());
        if (isInt(left) && isInt(right)) {
            lastType = INT_TYPE;
            return;
        }
        if (isString(left) && isString(right)) {
            lastType = STR_TYPE;
            return;
        }
        throw new RuntimeException("Operator + expects Int or Str operands, got: " + left + " and " + right);
    }

    @Override
    public void visit(AndNode node) {
        TypeNode left = typeOf(node.getLeft());
        TypeNode right = typeOf(node.getRight());
        if (!isBool(left) || !isBool(right)) {
            throw new RuntimeException("Operator && expects Bool operands, got: " + left + " and " + right);
        }
        lastType = BOOL_TYPE;
    }

    @Override
    public void visit(BinaryOperation node) {
        //System.out.println("Visit BinaryOperation: " + node.getOperator());
        TypeNode left = typeOf(node.getLeft());
        TypeNode right = typeOf(node.getRight());
        String op = node.getOperator().toString();
        switch (op) {
            case "OPSUMA":
                if (isInt(left) && isInt(right)) {
                    lastType = INT_TYPE;
                } else if (isString(left) && isString(right)) {
                    lastType = STR_TYPE;
                } else {
                    throw new RuntimeException("Operator + expects Int or Str operands, got: " + left + " and " + right);
                }
                break;
            case "OPRESTA":
            case "OPMULT":
                if (isInt(left) && isInt(right)) {
                    lastType = INT_TYPE;
                } else {
                    throw new RuntimeException("Operator " + op + " expects Int operands, got: " + left + " and " + right);
                }
                break;
            case "OPDIVENT":
                if (isInt(left) && isInt(right) && !isZeroLiteral(node.getRight())) {
                    lastType = INT_TYPE;
                } else {
                    throw new RuntimeException("Operator / expects Int operands and a non-zero literal divisor, got: " + left + " and " + right);
                }
                break;
            case "==":
            case "OPIGUAL":
            case "OPDIST":
            case "!=":
                if (!isComparable(left, right)) {
                    throw new RuntimeException("Operator " + op + " expects comparable operands of the same type, got: " + left + " and " + right);
                }
                lastType = BOOL_TYPE;
                break;
            case "OPMAYOR":
            case "OPMAYORIGUAL":
            case "OPMENOR":
            case "OPMENORIGUAL":
            case ">":
            case ">=":
            case "<":
            case "<=":
                if (isInt(left) && isInt(right)) {
                    lastType = BOOL_TYPE;
                } else {
                    throw new RuntimeException("Operator " + op + " expects Int operands, got: " + left + " and " + right);
                }
                break;
            case "OPAND":
            case "OPOR":
            case "&&":
            case "||":
                if (isBool(left) && isBool(right)) {
                    lastType = BOOL_TYPE;
                } else {
                    throw new RuntimeException("Operator " + op + " expects Bool operands, got: " + left + " and " + right);
                }
                break;
            case "OPINCR":
                if (isInt(left) && isInt(right)) {
                    lastType = INT_TYPE;
                } else {
                    throw new RuntimeException("Operator ++ expects Int operands, got: " + left + " and " + right);
                }
                break;
            default:
                throw new RuntimeException("Unsupported operator: " + node.getOperator());
        }
    }

    @Override
    public void visit(DivNode node) {
        TypeNode left = typeOf(node.getLeft());
        TypeNode right = typeOf(node.getRight());
        if (!isInt(left) || !isInt(right) || isZeroLiteral(node.getRight())) {
            throw new RuntimeException("Operator / expects Int operands and a non-zero divisor, got: " + left + " and " + right);
        }
        lastType = INT_TYPE;
    }

    @Override
    public void visit(EqualNode node) {
        TypeNode left = typeOf(node.getLeft());
        TypeNode right = typeOf(node.getRight());
        if (!isComparable(left, right)) {
            throw new RuntimeException("Operator == expects comparable operands of the same type, got: " + left + " and " + right);
        }
        lastType = BOOL_TYPE;
    }

    @Override
    public void visit(UnaryOperation node) {
        TypeNode operandType = typeOf(node.getOperand());
        String op = node.getOperator().toString();
        switch (op) {
            case "!":
                if (!isBool(operandType)) {
                    throw new RuntimeException("Operator ! expects Bool, got: " + operandType);
                }
                lastType = BOOL_TYPE;
                break;
            case "OPINCR":
            case "OPDECR":
                if (!isInt(operandType)) {
                    throw new RuntimeException("Operator " + op + " expects Int, got: " + operandType);
                }
                lastType = INT_TYPE;
                break;
            default:
                throw new RuntimeException("Unsupported operator: " + node.getOperator());
        }
    }

    @Override
    public void visit(AssigNode node) {
        TypeNode leftType = typeOf(node.getVariable());
        TypeNode rightType = typeOf(node.getExpresion());
        if (!isAssignable(leftType, rightType)) {
            throw new RuntimeException("Cannot assign " + rightType + " to " + leftType);
        }
        lastType = leftType;
    }

    @Override
    public void visit(BlockNode node) {
        //pushScope();

        if (node.getVariables() != null) {
            for (VariableDeclarationNode variable : node.getVariables()) {
                if (variable != null) {
                    variable.accept(this);
                }
            }
        }
        if (node.getStatements() != null) {
            for (SentenceNode statement : node.getStatements()) {
                if (statement != null) {
                    statement.accept(this);
                }
            }
        }
        //popScope();

    }

    @Override
    public void visit(ForNode node) {
        //pushScope();

        if (node.getVariable() != null) {
            node.getVariable().accept(this);
        }
        if (node.getIterable() != null) {
            node.getIterable().accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        //popScope();

    }

    @Override
    public void visit(IfNode node) {
        //pushScope();

        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getThenBranch() != null) {
            node.getThenBranch().accept(this);
        }
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        //popScope();

    }

    @Override
    public void visit(RetNode node) {
        TypeNode returnType = typeOf(node.getExpresion());
        if (currentMethod == null) {
            throw new RuntimeException("Return statement not inside a method");
        }
        TypeNode expectedReturnType = currentMethod.getType();
        if (!isAssignable(expectedReturnType, returnType)) {
            throw new RuntimeException("Return type mismatch in method " + currentMethod.getName() + ": expected " + expectedReturnType + ", got " + returnType);
        }
    }

    @Override
    public void visit(SentenceNode node) {
        // base node
    }

    @Override
    public void visit(WhileNode node) {
        //pushScope();

        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        //popScope();

    }

    @Override
    public void visit(ArrayTypeNode node) {
        if (node.getElementType() != null) {
            node.getElementType().accept(this);
        }
    }

    @Override
    public void visit(AttributeNode node) {
        AttributeSymbol symbol = new AttributeSymbol(node.getName(), node.getType(), node.getVisibility()!=null);
        declareAttribute(symbol);
    }

    @Override
    public void visit(PrimitiveTypeNode node) {
        lastType = node;
    }

    @Override
    public void visit(ReferenceTypeNode node) {
        if (symbolTable.getClass(node.getName()).isEmpty()) {
            throw new RuntimeException("Tipo no declarado: " + node.getName());
        }
        lastType = node;
    }

    @Override
    public void visit(VoidTypeNode node) {
        lastType = node;
    }

    @Override
    public void visit(ArrayAccessNode node) {
        TypeNode arrayType = typeOf(node.getArray());
        TypeNode indexType = typeOf(node.getIndex());
        if (!isInt(indexType)) {
            throw new RuntimeException("Array index must be Int");
        }
        if (!(arrayType instanceof ArrayTypeNode)) {
            throw new RuntimeException("Trying to index non-array type: " + arrayType);
        }
        lastType = ((ArrayTypeNode) arrayType).getElementType();
    }

    @Override
    public void visit(ArrayCreationNode node) {
        if (node.getElementType() != null) {
            node.getElementType().accept(this);
        }
        TypeNode sizeType = typeOf(node.getSize());
        if (!isInt(sizeType)) {
            throw new RuntimeException("Array size must be Int");
        }
        lastType = new ArrayTypeNode(node.getElementType());
    }

    @Override
    public void visit(AstNode node) {
        // base node
    }

    @Override
    public void visit(ConstructorCallNode node) {
        if (node.getArguments() != null) {
            for (ExpresionNode arg : node.getArguments()) {
                if (arg != null) {
                    typeOf(arg);
                }
            }
        }
    }

    @Override
    public void visit(ExpresionNode node) {
        if (node != null) {
            node.accept(this);
        }
    }

    @Override
    public void visit(FieldAccessNode node) {
        // Tipo del objeto de la izquierda
        TypeNode objectType = typeOf(node.getObject());
        if (!(objectType instanceof ReferenceTypeNode refType)) {
            throw new RuntimeException("Cannot access field '" + node.getFieldName() +"' of non-object type " + describeType(objectType));
        }
        // Buscar la clase
        ClassSymbol clazz = resolveClass(refType.getName());

        // Buscar el atributo
        AttributeSymbol attribute = clazz.getAttributes().get(node.getFieldName());
        if (attribute == null) {
            throw new RuntimeException("Class '" + clazz.getName() + "' has no attribute '" + node.getFieldName() + "'");
        }

        // El tipo de la expresión es el tipo del atributo
        lastType = attribute.getType();
    }

    @Override
    public void visit(FormalParameterNode node) {
        ParameterSymbol symbol = new ParameterSymbol(node.getNombre(), node.getTipo());
        declareParameter(symbol);
    }

    @Override
    public void visit(MethodCallNode node) {
        //System.out.println("visit MethodCallNode -> " + node.getMethodName());
        TypeNode parentType = typeOf(node.getParent());
        ClassSymbol receiverClass = resolveReceiverClass(parentType);
        MethodSymbol methodSymbol =  resolveMethod(receiverClass, node.getMethodName());
        /*if (parentType == null) { //es como si fuese void
            throw new RuntimeException("Method call without receiver: " + node.getMethodName());
        }*/
        if (node.getArguments() != null) {
            for (ExpresionNode arg : node.getArguments()) {
                if (arg != null) {
                    typeOf(arg);
                }
            }
        }
        lastType = methodSymbol.getType();
    }

    @Override
    public void visit(NewNode node) {
        if (symbolTable.getClass(node.getClassName()).isEmpty()) {
            throw new RuntimeException("Class not declared: " + node.getClassName());
        }
        if (node.getArguments() != null) {
            for (ExpresionNode arg : node.getArguments()) {
                if (arg != null) {
                    typeOf(arg);
                }
            }
        }
        lastType = new ReferenceTypeNode(node.getClassName());
    }

    @Override
    public void visit(SelfNode node) {
        if (currentClass == null) {
            throw new RuntimeException("'self' used outside of a class context");
        }
        lastType = new ReferenceTypeNode(currentClass.getName());
    }

    @Override
    public void visit(SimpleSentenceNode node) {
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
    }

    @Override
    public void visit(StartNode node) {
        MethodSymbol previousMethod = currentMethod;
        currentMethod = new MethodSymbol("$start", new VoidTypeNode());
        if (node.getRoot() != null) {
            node.getRoot().accept(this);
        }
        currentMethod = previousMethod;
    }

    @Override
    public void visit(StaticMethodCallNode node) {
        //System.out.println("visit StaticMethodCallNode -> "+ node.getClassName() + "." + node.getMethodName());
        ClassSymbol clazz = resolveClass(node.getClassName());
        MethodSymbol method = resolveMethod(clazz, node.getMethodName());
        if (node.getArguments() != null) {
            for (ExpresionNode arg : node.getArguments()) {
                typeOf(arg);
            }
        }
        lastType = method.getType();
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        VariableSymbol symbol = new VariableSymbol(node.getName(), node.getType());
        declareVariable(symbol);
    }

    @Override
    public void visit(VarNode node) {
        VariableSymbol var = resolveVariable(node.getName());
        lastType = var.getType();
    }

    @Override
    public void visit(VisibilityNode node) {
        // base node
    }
    @Override
    public void visit(LessNode n){}
    @Override
    public void visit(MulNode n){}
    @Override
    public void visit(NotNode n){}
    @Override
    public void visit(OrNode n){}
    @Override
    public void visit(SubstractionNode n){}
    @Override
    public void visit(GraterNode n){}
    @Override
    public void visit(GraterOrEqualNode n){}
}
