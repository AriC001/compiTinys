package compiladores.sintax;

import compiladores.AST.*;
import compiladores.AST.Definitions.*;
import compiladores.AST.Statements.*;
import compiladores.AST.literals.*;
import compiladores.AST.operations.*;
import compiladores.AST.typeNodes.*;
import compiladores.lexicon.Analyzer;
import compiladores.lexicon.Token;
import compiladores.lexicon.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static compiladores.lexicon.TokenType.*;

public class SintaxAnalyzer {
    private final Analyzer lexiconAnalyzer;
    Token lookahead;
    Token lookaheadSig;
    //private static final TokenType OPASIGNSUMA = OPSUMA;

    public SintaxAnalyzer(Analyzer lexiconAnalyzer) {
        this.lexiconAnalyzer = lexiconAnalyzer;
        this.lookahead = lexiconAnalyzer.nextToken();
        this.lookaheadSig = lexiconAnalyzer.nextToken();
    }

    public ProgramNode parse() {
        ProgramNode programNode = program();
        if (lookahead.getTokenName() != EOF) {
            error(EOF);
        }
        return programNode;
    }


    void match(TokenType esperado) {
        if (lookahead.getTokenName() == esperado) {
            lookahead = lookaheadSig;
            lookaheadSig = lexiconAnalyzer.nextToken();
        } else {
            error(esperado);
        }
    }
    void error(TokenType... esperados) {
        throw new RuntimeException(
                "Error sintáctico en línea: "
                        + lookahead.getLineCode()
                        + ", columna: "
                        + lookahead.getColumnCode()
                        + "; se esperaba "
                        + Arrays.toString(esperados)
                        + " y se encontró "
                        + "'" + lookahead.getLexeme() + "'"
                        + " (" + lookahead.getTokenName() + ")"
        );
    }

    ProgramNode program() { //parAbre
        List<DefinitionNode> clases = listaDefiniciones();
        StartNode start = start();
        return new ProgramNode(clases, start);
    }

    StartNode start() {
        match(PSTART);
        BlockNode bloque = bloqueMetodo();
        return new StartNode(bloque);
    }

    List<DefinitionNode> listaDefiniciones() {
        List<DefinitionNode> definiciones = new ArrayList<>();
        while (lookahead.getTokenName() == PRCLASS || lookahead.getTokenName() == PRIMPL) {
            definiciones.add(listaDefinicionesF());
        }
        return definiciones;
    }

    DefinitionNode listaDefinicionesF() {
        if (lookahead.getTokenName() == PRCLASS ) {
            return classRule();
        }
        else if (lookahead.getTokenName() == PRIMPL) {
            return impl();
        }
        else {
            error(PRCLASS, PRIMPL);
            return null;
        }
    }
    /*
    OLD IMPLEMENTATION
    List<DefinitionNode> listaDefiniciones() {
        List<DefinitionNode> definiciones = new ArrayList<>();
        if (lookahead.getTokenName() == PARABRE) {
            match(PARABRE);
            definiciones.add(listaDefinicionesF());
        }
        else if (lookahead.getTokenName() == PSTART) {
            // lambda
        }
        else {
            error(PARABRE, PSTART);
        }
        return definiciones;
    }
    DefinitionNode listaDefinicionesF() {
        if (lookahead.getTokenName() == IDCLASS) {
            ClassNode classnode = classRule();
            match(PARCIERRA);
            listaDefiniciones();
            return classnode;
        }
        else if (lookahead.getTokenName() == PRIMPL) { //IDMETAT o IMPL
            ImplNode implnode = impl();
            match(PARCIERRA);
            listaDefiniciones();
            return implnode;
        }
        else {
            error(IDCLASS, IDMETAT, PRIMPL);
        }
    }*/

    ClassNode classRule() {


        match(PRCLASS);

        String name = lookahead.getLexeme();
        match(IDCLASS);
        //ClassBody body = classF();

        return classF(name);
    }
    ClassNode classF(String name) {
        if (lookahead.getTokenName() == DOSPUNTOS) {
            TypeNode type = herencia();
            match(LLAVEABRE);
            List<AttributeNode> atributes = atributoIt();
            match(LLAVECIERRA);
            return new ClassNode(name,type, atributes);
        }
        else if (lookahead.getTokenName() == LLAVEABRE) {
            match(LLAVEABRE);
            List<AttributeNode> atributes = atributoIt();
            match(LLAVECIERRA);
            return new ClassNode(name, atributes);
        }
        else {
            error(DOSPUNTOS,LLAVEABRE);
            return null;
        }
    }
    ImplNode impl() {
        match(PRIMPL);
        String name = lookahead.getLexeme();
        match(IDCLASS);
        match(LLAVEABRE);
        List<DefinitionNode> miembros = new ArrayList<>();
        while (lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST || lookahead.getTokenName() == PUNTO ) {
            miembros.add(miembro(name));
        }
        match(LLAVECIERRA);
        return new ImplNode(name,miembros);
    }
    TypeNode herencia() {
        match(DOSPUNTOS);
        return tipo();
    }
    /*
    void miembroIt(String name){
        if (lookahead.getTokenName() == PUNTO || lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST) {
            miembro(name);
            miembroIt(name);
        }
        else if (lookahead.getTokenName() == LLAVECIERRA) {
            // lambda
        }
        else {
            error(LLAVEABRE, PUNTO, PRFN, PRST);
        }
    }
    */
    DefinitionNode miembro(String name){ //	punto,fn,st
        if(lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST){
            return metodo();
        }
        else if(lookahead.getTokenName() == PUNTO){
            return constructor(name);
        }
        else{
            error(PRFN, PRST, PUNTO);
            return null;
        }
    }
    DefinitionNode constructor (String name){ //	punto
        match(PUNTO);
        List<FormalParameterNode> parameters = argumentosFormales();
        BlockNode body = bloqueMetodo();
        return new ConstructorNode(name, parameters, body); // Replace with actual constructor node creation
    }
    List<AttributeNode> atributoIt(){ //	pub, Str, Bool,Int
        List<AttributeNode> atributos = new ArrayList<>();
        while (lookahead.getTokenName() == PRPUB || lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT) {
            atributos.addAll(atributo());
        }
        if (lookahead.getTokenName() == LLAVECIERRA) {
            return atributos;
            // lambda
        }
        else {
            error(PRPUB, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, LLAVECIERRA);
            return null;
        }
    }
    List<AttributeNode> atributo(){  //	pub, Str, Bool,Int
        VisibilityNode visibility = null;
        if (lookahead.getTokenName() == PRPUB) {
            visibility = visibilidad();
        }
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT ) {
            TypeNode tipo = tipo();
            List<String> nombres = listaDeclaracionVariables();
            match(PUNTOYCOMA);
            List<AttributeNode> atributos = new ArrayList<>();
            for(String nombre : nombres)
                atributos.add(new AttributeNode(visibility, tipo, nombre));
            return atributos;
        }
        else {
            error(PRPUB, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT);
            return null;
        }
    }
    MethodNode metodo(){  //	fn, st
        boolean isStatic = false;
        if (lookahead.getTokenName() == PRFN) {
            match(PRFN);
            return metodoF(isStatic);
        }
        else if(lookahead.getTokenName() == PRST ) {
            isStatic = formaMetodo();
            match(PRFN);
            return metodoF(isStatic);
        }
        else {
            error(PRFN, PRST);
            return null;
        }
    }
    MethodNode metodoF(boolean isStatic){ //	objectID, void, Str, Bool,Int, idclass, Array
        TypeNode returnType = null;
        if (lookahead.getTokenName() == IDMETAT) {
            String name = lookahead.getLexeme();
            match(IDMETAT);
            List<FormalParameterNode> parameters = argumentosFormales();
            BlockNode body = bloqueMetodo();
            return new MethodNode(name, returnType, parameters, body, isStatic);
        }
        else if (lookahead.getTokenName() == PRVOID || lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO) {
            returnType = tipoMetodo();
            String name = lookahead.getLexeme();
            match(IDMETAT);
            List<FormalParameterNode> parameters = argumentosFormales();
            BlockNode body = bloqueMetodo();
            return new MethodNode(name, returnType, parameters, body, isStatic);
        }
        else {
            error(IDMETAT, PRVOID, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASSARRAY, IDCLASS, IDCLASSIO);
            return null;
        }
    }
    VisibilityNode visibilidad(){  //	pub
        match(PRPUB);
        return new VisibilityNode("pub");
    }
    Boolean formaMetodo(){ //	st
        match(PRST);
        return true;
    }
    BlockNode bloqueMetodo(){  //	llaveAbre
        match(LLAVEABRE);
        List<VariableDeclarationNode> variables = declVarLocalesIt();
        List<SentenceNode> statements = sentenciaIt();
        match(LLAVECIERRA);
        return new BlockNode(variables, statements);
    }
    //incorporacion IDMETAT en el lookahead para que reconozca variables locales
    List<VariableDeclarationNode> declVarLocalesIt() {
        List<VariableDeclarationNode> variables = new ArrayList<>();
        if(lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASSIO ) {
            while (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASSIO ) {
                TypeNode tipo = tipo();
                List<String> nombres = (listaDeclaracionVariables());
                match(PUNTOYCOMA);
                for(String nombre : nombres)
                    variables.add(new VariableDeclarationNode(tipo, nombre));
            }
            return variables;
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE) {
            return variables;
            // lambda LE FALTA ID SOLO arriba en los OR
        }
        else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, PUNTOYCOMA, IDMETAT, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, IDCLASSIO);
            return null;
        }
    }
    /*OLD IMPLEMENTATION
    void declVarLocalesIt(){ 	//Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY) {
            declVarLocales();
            declVarLocalesIt();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE) {
            // lambda LE FALTA ID SOLO arriba en los OR
        }
        else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, PUNTOYCOMA, IDMETAT, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE);
        }
    }
    void declVarLocales(){ //	Str, Bool,Int, idclass, Array
        tipo();
        listaDeclaracionVariables();
        match(PUNTOYCOMA);
    }
     */
    List<String> listaDeclaracionVariables() { 	//	idMetAt
        List<String> names = new ArrayList<>();
        names.add(lookahead.getLexeme());
        match(IDMETAT);
        //variables.add(new VariableDeclarationNode(type, name));
        while(lookahead.getTokenName() == COMA) {
            match(COMA);
            names.add(lookahead.getLexeme());
            match(IDMETAT);
            //variables.add(new VariableDeclarationNode(type, name2));
        }
        /*
        if (lookahead.getTokenName() == PUNTOYCOMA) {
            return variables;
            // lambda
        }
        else {
            error(COMA, PUNTOYCOMA);
            return null;
        }*/
        return names;
    }

    /* OLD IMPLEMENTATION
    void listaDeclaracionVariables(){ //	idMetAt

        String name = lookahead.getLexeme();
        match(IDMETAT);
        listaDeclaracionVariablesF();
    }
    void listaDeclaracionVariablesF(){ //	coma
        if(lookahead.getTokenName() == COMA) {
            match(COMA);
            listaDeclaracionVariables();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA) {
            // lambda
        }
        else {
            error(COMA, PUNTOYCOMA);
        }
    }*/
    List<FormalParameterNode> argumentosFormales(){ //	parAbre
        match(PARABRE);
        return argumentosFormalesF();
    }
    List<FormalParameterNode> argumentosFormalesF(){ 	//parCierra, Str, Bool,Int, idclass, Array
        List<FormalParameterNode> argumentos = new ArrayList<>();
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO) {
            argumentos = listaArgumentosFormales();
            match(PARCIERRA);
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            match(PARCIERRA);
        }
        else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, PARCIERRA);
        }
        return argumentos;
    }
    List<FormalParameterNode> listaArgumentosFormales() {
        if (lookahead.getTokenName() != IDCLASSSTR && lookahead.getTokenName() != IDCLASSBOOL && lookahead.getTokenName() != IDCLASSINT && lookahead.getTokenName() != IDCLASS && lookahead.getTokenName() != IDCLASSARRAY && lookahead.getTokenName() != IDCLASSIO) {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, IDCLASSIO);
            return null;
        }
        List<FormalParameterNode> argumentos = new ArrayList<>();
        argumentos.add(argumentoFormal());
        while (lookahead.getTokenName() == COMA) {
            match(COMA);
            argumentos.add(argumentoFormal());
        }
        if (lookahead.getTokenName() != PARCIERRA) {
            error(PARCIERRA);
        }
        return argumentos;
    }


    /* OLD IMPLEMENTATION with Recursion
    List<FormalParameterNode> listaArgumentosFormales(){  //	Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS) {
            List<FormalParameterNode> argumentos = new ArrayList<>();
            argumentos.add(argumentoFormal());
            listaArgumentosFormalesF();
            return argumentos;
        }else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, IDCLASS);
            return null;
        }

    }
    void listaArgumentosFormalesF(){ 	//coma
        if (lookahead.getTokenName() == COMA) {
            match(COMA);
            listaArgumentosFormales();
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            // lambda
        }
        else {
            error(COMA,PARCIERRA);
        }
    }
    */
    FormalParameterNode argumentoFormal(){  //	Str, Bool,Int, idclass, Array
        TypeNode tipo = tipo();
        String nombre = lookahead.getLexeme();
        match(IDMETAT);
        return new FormalParameterNode(tipo,nombre);
    }
    TypeNode tipoMetodo(){  //	void, Str, Bool,Int, idclass, Array
        if(lookahead.getTokenName() == PRVOID) {
            match(PRVOID);
            return new VoidTypeNode();
        }
        else if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY) {
            return tipo();
        }
        else {
            error(PRVOID, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY);
            return null;
        }
    }
    TypeNode tipo() {  //	Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT) {
            return tipoPrimitivo();
        } else if (lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO) {
            return tipoReferencia();
        } else if (lookahead.getTokenName() == IDCLASSARRAY) {
            return tipoArreglo();
        } else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY);
            return null;
        }
    }
    TypeNode tipoPrimitivo(){  //	Str, Bool, Int
        switch (lookahead.getTokenName()) {
            case IDCLASSSTR:
                match(IDCLASSSTR);
                return new PrimitiveTypeNode("str");
            case IDCLASSBOOL:
                match(IDCLASSBOOL);
                return new PrimitiveTypeNode("bool");
            case IDCLASSINT:
                match(IDCLASSINT);
                return new PrimitiveTypeNode("int");
            default:
                error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT);
                return null; // This line will never be reached due to the error() call, but it's needed to satisfy the compiler.
        }
    }
    TypeNode tipoReferencia(){  //	idclass
        if (lookahead.getTokenName() == IDCLASSIO) {
            match(IDCLASSIO);
            return new ReferenceTypeNode("io");
        }else{
            match(IDCLASS);
            return new ReferenceTypeNode(lookahead.getLexeme());
        }
    }
    TypeNode tipoArreglo(){  //	Array
        match(IDCLASSARRAY);
        return new ArrayTypeNode(tipoPrimitivo());
    }

    List<SentenceNode> sentenciaIt(){
        List<SentenceNode> sentences = new ArrayList<>();
        while(lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE || lookahead.getTokenName() == IDMETAT){
            sentences.add(sentencia());
        }
        if (lookahead.getTokenName() == LLAVECIERRA) {
            return sentences;
            // lambda
        }
        else {
            error(PUNTOYCOMA, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, LLAVECIERRA, IDMETAT);
            return null;
        }
    } 	//puntoYcoma, if,else,while,for, ret, id, self, parAbre, llaveAbre

    /*
    void sentenciaIt(){ 	//puntoYcoma, if,else,while,for, ret, id, self, parAbre, llaveAbre

        if(lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE) {
            sentencia();
            sentenciaIt();
        }
        else if (lookahead.getTokenName() == LLAVECIERRA) {
            //match(LLAVECIERRA); no se si va a chequear
            // lambda
        }
        else {
            error(PUNTOYCOMA, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, LLAVECIERRA);
        }
    }
    */
    SentenceNode sentencia(){  //	puntoYcoma, if,while,for, ret, id, self, parAbre, llaveAbre
        switch (lookahead.getTokenName()) {
            case PUNTOYCOMA:
                match(PUNTOYCOMA);
                break;
            case PRSELF:
                return asignacion();
            case IDCLASS: //o IDMETAT no se????
            case IDMETAT:
                return asignacion();
            case PARABRE:
                SentenceNode sent = sentenciaSimple();
                match(PUNTOYCOMA);
                return sent;
            case PRIF:
                match(PRIF);
                match(PARABRE);
                ExpresionNode exp = expresion();
                match(PARCIERRA);
                SentenceNode sent2 = sentencia();
                //sentenciaIF();
                return new IfNode(exp, sent2, sentenciaIF());
            case PRWHILE:
                match(PRWHILE);
                match(PARABRE);
                ExpresionNode exp2 = expresion();
                match(PARCIERRA);
                return new WhileNode(exp2, sentencia());
            case PRFOR:
                //modificacion para usar nodos de variables
                match(PRFOR);
                match(PARABRE);
                TypeNode type = tipoPrimitivo();
                String varName = lookahead.getLexeme();
                VariableDeclarationNode varDecNode = new VariableDeclarationNode(type, varName);
                match(IDMETAT);
                match(PRIN);
                String iterableName = lookahead.getLexeme();
                VarNode iterable = new VarNode(iterableName);
                match(IDMETAT);
                match(PARCIERRA);
                SentenceNode body = sentencia();
                return new ForNode(varDecNode,iterable, body);
            case PRRET:
                match(PRRET);
                return sentenciaRet();
            case LLAVEABRE:
                return bloque();
            default:
                error(PUNTOYCOMA, PRIF, PRWHILE, PRFOR, PRRET, IDCLASS, PRSELF, PARABRE, LLAVEABRE);
                return null;
        }
        return null;
    }
    SentenceNode sentenciaIF(){ 	//else
        if (lookahead.getTokenName() == PRELSE) {
            match(PRELSE);
            return sentencia();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE || lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSARRAY) {
            return null;
            // lambda
        }
        else {
            error(PRELSE, PUNTOYCOMA, IDMETAT, PRIF, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASSARRAY);
            return null;
        }
    }
    SentenceNode sentenciaRet(){ //	puntoYcoma, =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == PUNTOYCOMA) {
            match(PUNTOYCOMA);
            return null;
        }
        else if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            ExpresionNode exp = expresion();
            match(PUNTOYCOMA);
            return new RetNode(NodeType.ReturnNode,"return",exp);
        }
        else {
            error(PUNTOYCOMA, OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
            return null;
        }
    }
    SentenceNode bloque(){  //	llaveAbre
        match(LLAVEABRE);
        List<SentenceNode> sent = sentenciaIt();
        match(LLAVECIERRA);
        return new BlockNode(sent);
    }
    SentenceNode asignacion(){  //	id, self
        if (lookahead.getTokenName() == PRSELF) {
            ExpresionNode var = accesoSelfSimple();
            match(OPASIGN);
            ExpresionNode exp = expresion();
            return new AssigNode(var, exp);
        }
        else if (lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT) {
           ExpresionNode var = accesoVarSimple();
           match(OPASIGN);
           ExpresionNode exp = expresion();
          return new AssigNode(var, exp);
        }
        else {
            error(PRSELF, IDCLASS, IDMETAT);
            return null;
        }
    }
    ExpresionNode accesoVarSimple(){  //	id es solo IDMETAT o tambien IDCLASS????
        String name = lookahead.getLexeme();
        if (lookahead.getTokenName() == IDCLASS) {
            match(IDCLASS);
        }else if (lookahead.getTokenName() == IDMETAT) {
            match(IDMETAT);
        }
        VarNode var = new VarNode(name);
        return accesoVarSimpleF(var);
    }
    ExpresionNode accesoVarSimpleF(ExpresionNode parent){ 	//corAbre, punto
        if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            ExpresionNode index = expresion();
            match(CORCIERRA);
            ArrayAccessNode array = new ArrayAccessNode(parent, index);
            return accesoVarSimpleF(array);
        }
        else if (lookahead.getTokenName() == PUNTO) {
            return encadenadoSimpleIt(parent);
        } else if (lookahead.getTokenName() == OPASIGN) {
            return parent;
            // lambda
        } else {
            error(CORABRE, PUNTO, OPASIGN);
            return null;
        }
    }
    ExpresionNode accesoSelfSimple(){  //	self,
        match(PRSELF);
        SelfNode selfNode = new SelfNode();
        return encadenadoSimpleIt(selfNode);
    }
    ExpresionNode encadenadoSimpleIt(ExpresionNode parent) {
        while (lookahead.getTokenName() == PUNTO) {
            parent = encadenadoSimple(parent);
        }
        if (lookahead.getTokenName() == OPIGUAL) {
            return parent;
            // lambda
        }
        else {
            error(PUNTO, OPIGUAL);
            return null;
        }
    }
    ExpresionNode encadenadoSimple(ExpresionNode parent){
        match(PUNTO);
        String name = lookahead.getLexeme();
        match(IDMETAT);
        return new FieldAccessNode(parent, name);
    }

    /* OLD IMPLEMENTATION
    void encadenadoSimpleIt(){ 	//punto
        if(lookahead.getTokenName() == PUNTO) {
            encadenadoSimple();
            encadenadoSimpleIt();
        }
        else if (lookahead.getTokenName() == OPIGUAL) {
            // lambda
        }
        else {
            error(PUNTO, OPIGUAL);
        }
    }
    void encadenadoSimple(){ //	punto
        match(PUNTO);
        match(IDMETAT);
    }
    */
    SentenceNode sentenciaSimple(){  //	parAbre
        match(PARABRE);
        ExpresionNode exp = expresion();
        match(PARCIERRA);
        return new SimpleSentenceNode(exp);
    }

    ExpresionNode expresion(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            return expOr();
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW,IDCLASSIO);
            return null;
        }
    }
    ExpresionNode expOr (){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expAnd();
        return expOr2(left);
    }
    ExpresionNode expOr2(ExpresionNode left){ 	//opOR
        if (lookahead.getTokenName() == OPOR) {
            match(OPOR);
            ExpresionNode right = expAnd();
            OrNode nuevo = new OrNode(
                    left,
                    right
            );
            return expOr2(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA ) {
            return left;
        }
        else {
            error(OPOR, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA);
            return null;
        }
    }
    ExpresionNode expAnd(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expIgual();
        return expAnd2(left);
    }
    ExpresionNode expAnd2(ExpresionNode left){ 	//opAND
        if (lookahead.getTokenName() == OPAND) {
            match(OPAND);
            ExpresionNode right = expIgual();
            AndNode nuevo = new AndNode(
                    left,
                    right
            );
            return expAnd2(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR) {
            // lambda
            return left;
        }
        else {
            error(OPAND, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR);
            return null;
        }
    }
    ExpresionNode expIgual(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expCompuesta();
        return expIgual2(left);
    }
    ExpresionNode expIgual2(ExpresionNode left){ 	//==, !=
        if(lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            TokenType op = opIgual();
            ExpresionNode right = expCompuesta();
            BinaryOperation nuevo = new BinaryOperation(
                    op,
                    left,
                    right
            );
            return expIgual2(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND) {
            return left;
            // lambda
        }
        else {
            error(OPIGUAL, OPDISTINTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND);
            return null;
        }
    }
    ExpresionNode expCompuesta(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expAdd();
        return expCompuestaF(left);
    }
    ExpresionNode expCompuestaF(ExpresionNode left){ 	//=<, >, <=, <
        if (lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL) {
            TokenType op = opCompuesto();
            //match(lookahead.getTokenName());
            ExpresionNode right = expAdd();
            BinaryOperation nuevo = new BinaryOperation(op, left, right);
            return expCompuestaF(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            return left;
            // lambda
        }
        else {
            error(OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO);
            return null;
        }

    }
    ExpresionNode expAdd(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expMul();
        return expAdd2(left);
    }
    ExpresionNode expAdd2(ExpresionNode left){ 	//=+, -
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPSUMA) {
            //TokenType op = lookahead.getTokenName();
            TokenType op = opAd();
            ExpresionNode right = expMul();
            BinaryOperation nuevo =
                    new BinaryOperation(op, left, right);

            return expAdd2(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL ) {
            return left;
        }
        else {
            error(OPASIGNSUMA, OPRESTA, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL);
            return null;
        }
    }
    ExpresionNode expMul(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        ExpresionNode left = expUn();
        return expMul2(left);
    }
    ExpresionNode expMul2(ExpresionNode left){ 	//*, /
        if (lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            //TokenType op = lookahead.getTokenName();
            TokenType op = opMul();
            ExpresionNode right = expUn();
            BinaryOperation nuevo =
                    new BinaryOperation(op, left, right);
            return expMul2(nuevo);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPSUMA) {
            return left;
        }
        else {
            error(OPMULT, OPDIVENT, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA);
            return null;
        }
    }
    ExpresionNode expUn(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT) {
            TokenType opUn = opUnario();
            ExpresionNode expr = expUn();
            return new UnaryOperation(opUn, expr);
        }
         else if(lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            return operando();
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
            return null;
        }
    }
     TokenType opIgual(){ 	//==, !=
        if (lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            TokenType op = lookahead.getTokenName();
            match(lookahead.getTokenName());
            return op;
        }
        else {
            error(OPIGUAL, OPDISTINTO);
            return null;
        }
    }
     TokenType opCompuesto(){ 	//=<, >, <=, <
        if (lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL) {
            TokenType op = lookahead.getTokenName();
            match(lookahead.getTokenName());
            return op;
        }
        else {
            error(OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL);
            return null;
        }
    }
    TokenType opAd(){  //	=+, -
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPSUMA) {
            TokenType op = lookahead.getTokenName();
            match(lookahead.getTokenName());
            return op;
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPSUMA);
            return null;
        }
    }
    TokenType opUnario(){ 	//=+,-, !,++,--, (Int)
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT) {
            TokenType operator = lookahead.getTokenName();
            match(lookahead.getTokenName());
            return operator;
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT);
            return null;
        }
    }
    TokenType opMul(){  //	*, /
        if (lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            TokenType op = lookahead.getTokenName();
            match(lookahead.getTokenName());
            return op;
        }
        else {
            error(OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode operando(){ //	nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR) {
            return literal();
        }
        else if (lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            ExpresionNode exp = primario();
            return operandoF(exp);
        }
        else {
            error(PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
            return null; // This line will never be reached due to the error() call, but it's needed to satisfy the compiler.
        }
    }
    ExpresionNode operandoF(ExpresionNode left){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(left);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPSUMA) {
            return left;
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT);
            return null;
        }
    }
    LiteralNode literal(){ //	nil, true, false, intLiteral, StrLiteral
        if (lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR)
        {
            //match(lookahead.getTokenName());
            switch (lookahead.getTokenName()) {
                case PRNIL:
                    match(lookahead.getTokenName());
                    return new NilLiteralNode();
                case PRTRUE:
                    match(lookahead.getTokenName());
                    return new BooleanLiteralNode(true);
                case PRFALSE:
                    match(lookahead.getTokenName());
                    return new BooleanLiteralNode(false);
                case LITINT:
                    int value = Integer.parseInt(lookahead.getLexeme());
                    match(lookahead.getTokenName());
                    return new IntegerLiteralNode(value);
                case LITSTR:
                    String valueStr = lookahead.getLexeme();
                    match(lookahead.getTokenName());
                    return new StringLiteralNode(valueStr);
                default:
                    error(PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR);
                    return null; // This line will never be reached due to the error() call, but it's needed to satisfy the compiler.
            }
        }else{
            error(PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR);
            return null;
        }
    }
    ExpresionNode primario(){ //	parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == PARABRE) {
            return expresionParentizada();
        }
        else if (lookahead.getTokenName() == PRSELF) {
            return accesoSelf();
        }
        else if (lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO) {
            return llamadaMetodoEstatico();
        }
        else if (lookahead.getTokenName() == IDMETAT) {
            if(lookaheadSig.getTokenName() == PARABRE){
                return llamadaMetodo();
            }
            return accesoVar();
        }
        else if (lookahead.getTokenName() == PRNEW) {
            return llamadaConclassor();
        }
        else {
            error(PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
            return null;
        }
    }
    ExpresionNode expresionParentizada(){ //	parAbre
        match(PARABRE);
        ExpresionNode exp = expresion();
        match(PARCIERRA);
        return expresionParentizadaF(exp);
    }
    ExpresionNode expresionParentizadaF(ExpresionNode parNode){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(parNode);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            return parNode;
            //lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode accesoSelf(){ //	self
        match(PRSELF);
        SelfNode selfNode = new SelfNode();
        return accesoSelfF(selfNode);
    }
    ExpresionNode accesoSelfF(SelfNode selfNode){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(selfNode);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
            return selfNode;
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode accesoVar (){ //	id
        String value = lookahead.getLexeme();
        //String lexeme = lookahead.getTokenName().toString();
        match(IDMETAT);
        VarNode varNode = new VarNode(value);
        return accesoVarF1(varNode);
    }
    ExpresionNode accesoVarF1(VarNode varNode){ //	punto, corAbre
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(varNode);
        }
        else if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            ExpresionNode exp = expresion();
            match(CORCIERRA);
            ArrayAccessNode arrayAccessNode = new ArrayAccessNode(varNode,exp);
            return accesoVarF2(arrayAccessNode);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT || lookahead.getTokenName() == OPSUMA) {
            return varNode;
        } else {
            error(PUNTO, CORABRE, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode accesoVarF2(ExpresionNode parent){ 	//punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(parent);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
            return parent;
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode llamadaMetodo(){ //	id
        String name = lookahead.getLexeme();
        match(IDMETAT);
        List<ExpresionNode> args = argumentosActuales();
        MethodCallNode methodCallNode = new MethodCallNode(null, name, args);
        return llamadaMetodoF(methodCallNode);
    }
    ExpresionNode llamadaMetodoF(MethodCallNode methodCallNode){ 	//punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(methodCallNode);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
            return methodCallNode;
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode llamadaMetodoEstatico(){ //	idclass
        String className  = lookahead.getLexeme();
        if(lookahead.getTokenName() == IDCLASSIO) {
            match(IDCLASSIO);
            className = "io";
        } else {
            match(IDCLASS);
        }
        match(PUNTO);
        MethodCallNode method = (MethodCallNode) llamadaMetodo();
        StaticMethodCallNode node = new StaticMethodCallNode(className,method);
        return llamadaMetodoEstaticoF(node);
    }
    ExpresionNode llamadaMetodoEstaticoF(ExpresionNode node){ //	punto
        if(lookahead.getTokenName() == PUNTO) {
            return encadenado(node);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            return node;
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode llamadaConclassor (){ //	new
        match(PRNEW);
        return llamadaConclassorF1();
    }
    ExpresionNode llamadaConclassorF1(){ //	idclass, Str, Bool, Int
        if (lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO) {
            String name = lookahead.getLexeme();
            match(IDCLASS);
            List<ExpresionNode> arguments = argumentosActuales();
            return llamadaConclassorF2(new ConstructorCallNode(name,arguments));
        }
        else if (lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSSTR) {
            TypeNode type = tipoPrimitivo();
            match(CORABRE);
            ExpresionNode exp = expresion();
            match(CORCIERRA);
            return new ArrayCreationNode(type, exp);

        }
        else {
            error(IDCLASS, IDCLASSINT, IDCLASSBOOL, IDCLASSSTR);
            return null;
        }
    }
    ExpresionNode llamadaConclassorF2(ExpresionNode parent){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(parent);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            return parent;
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    List<ExpresionNode> argumentosActuales(){ //	parAbre
        match(PARABRE);
        return argumentosActualesF();
    }
    List<ExpresionNode> argumentosActualesF(){ //	parCierra, =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        //if =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPNOT || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE ||lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSIO || lookahead.getTokenName() == PRNEW) {
            List<ExpresionNode> arguments = listaExpresiones();
            match(PARCIERRA);
            return arguments;
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            match(PARCIERRA);
            return new ArrayList<>();
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPNOT, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDMETAT, IDCLASS, PRNEW, PARCIERRA);
            return null;
        }
    }
    List<ExpresionNode> listaExpresiones() {
        List<ExpresionNode> expresiones = new ArrayList<>();
        expresiones.add(expresion());
        while (lookahead.getTokenName() == COMA) {
            match(COMA);
            expresiones.add(expresion());
        }
        if (lookahead.getTokenName() == PARCIERRA) {
            return expresiones;
            // lambda
        }else {
            error(COMA, PARCIERRA);
            return null;
        }
    }

    /* OLD IMPLEMENTATION
    List<ExpresionNode> listaExpresiones(){ //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new

        List<ExpresionNode> expresiones = new ArrayList<>();
        expresiones.add(expresion());
        listaExpresionesF(expresiones);
        return expresiones;
    }
    void listaExpresionesF(List<ExpresionNode> expresiones){ //	coma
        if (lookahead.getTokenName() == COMA) {
            match(COMA);
            listaExpresiones();
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            // lambda
        }
        else {
            error(COMA, PARCIERRA);
        }
    }*/
    ExpresionNode encadenado(ExpresionNode parent){ //	punto
        match(PUNTO);
        return encadenadoF(parent);
    }
    ExpresionNode encadenadoF(ExpresionNode parent){  //	id
        if (lookahead.getTokenName() == IDMETAT && lookaheadSig.getTokenName() == PARABRE) {
            return llamadaMetodoEncadenado(parent);
        }
        else if (lookahead.getTokenName() == IDMETAT && (lookaheadSig.getTokenName() == PUNTO || lookaheadSig.getTokenName() == CORABRE || lookaheadSig.getTokenName() == OPASIGNSUMA || lookaheadSig.getTokenName() == OPRESTA || lookaheadSig.getTokenName() == OPNOT || lookaheadSig.getTokenName() == OPINCR || lookaheadSig.getTokenName() == OPDECR || lookaheadSig.getTokenName() == IDCLASSINT || lookaheadSig.getTokenName() == PRNIL || lookaheadSig.getTokenName() == PRTRUE || lookaheadSig.getTokenName() == PRFALSE || lookaheadSig.getTokenName() == LITINT || lookaheadSig.getTokenName() == LITSTR || lookaheadSig.getTokenName() == PARABRE ||lookaheadSig.getTokenName() == PRSELF || lookaheadSig.getTokenName() == IDMETAT || lookaheadSig.getTokenName() == IDCLASS || lookaheadSig.getTokenName() == PRNEW) ) {
            return accesoVariableEncadenado(parent);
        }
        else {
            error(IDMETAT);
            return null;
        }
    }
    ExpresionNode llamadaMetodoEncadenado(ExpresionNode parent){ //	id
        String methodName = lookahead.getLexeme();
        match(IDMETAT);
        List<ExpresionNode> arguments = argumentosActuales();
        MethodCallNode call = new MethodCallNode(parent, methodName, arguments);
        return llamadaMetodoEncadenadoF(call);
    }
    ExpresionNode llamadaMetodoEncadenadoF(ExpresionNode parent){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(parent);
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            return parent;
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode accesoVariableEncadenado(ExpresionNode parent){ //	id
        String name = lookahead.getLexeme();
        match(IDMETAT);
        FieldAccessNode fieldAccessNode = new FieldAccessNode(parent, name);
        return accesoVariableEncadenadoF1(fieldAccessNode);
    }
    ExpresionNode accesoVariableEncadenadoF1(ExpresionNode fieldAccessNode) { //	punto, corAbre
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(fieldAccessNode);
        } else if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            ExpresionNode indice = expresion();
            match(CORCIERRA);
            ArrayAccessNode array =
                    new ArrayAccessNode(fieldAccessNode, indice);
            return accesoVariableEncadenadoF2(array);
        } else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            return fieldAccessNode;
            //lambda
        }
        else {
            error(PUNTO, CORABRE, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }
    ExpresionNode accesoVariableEncadenadoF2(ExpresionNode parent) { //	punto
        if (lookahead.getTokenName() == PUNTO) {
            return encadenado(parent);
        } else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            return parent;
            // lambda
        } else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
            return null;
        }
    }

}
