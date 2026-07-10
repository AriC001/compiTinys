package compiladores.sintax;

import compiladores.lexicon.Analyzer;
import compiladores.lexicon.Token;
import compiladores.lexicon.TokenType;

import static compiladores.lexicon.TokenType.*;

public class SintaxAnalyzer {
    private Analyzer lexiconAnalyzer;
    Token lookahead;
    Token lookaheadSig;


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
                        + esperados
                        + " y se encontró "
                        + lookahead.getLexeme()
                        + " (" + lookahead.getTokenName() + ")"
        );
    }

    void program() { //parAbre
        start();
        listaDefiniciones();
    }

    void start() {
        match(PSTART);
        bloqueMetodo();
    }
    void listaDefiniciones() {
        if (lookahead.getTokenName() == PARABRE) {
            match(PARABRE);
            listaDefinicionesF();
        }
        else if (lookahead.getTokenName() == PSTART) {
            // lambda
        }
        else {
            error(PARABRE, PSTART);
        }
    }
    void listaDefinicionesF() {
        if (lookahead.getTokenName() == IDCLASS) {
            classRule();
            match(PARCIERRA);
            listaDefiniciones();
        }
        else if (lookahead.getTokenName() == PRIMPL) { //IDMETAT o IMPL
            impl();
            match(PARCIERRA);
            listaDefiniciones();
        }
        else {
            error(IDCLASS, IDMETAT, PRIMPL);
        }
    }
    void classRule() {
        match(IDCLASS);
        classF();
    }
    void classF() {
        if (lookahead.getTokenName() == DOSPUNTOS) {
            herencia();
            match(LLAVEABRE);
            atributoIt();
            match(LLAVECIERRA);
        }
        else if (lookahead.getTokenName() == LLAVEABRE) {
            match(LLAVEABRE);
            atributoIt();
            match(LLAVECIERRA);
        }
        else {
            error(DOSPUNTOS,LLAVEABRE);
        }
    }
    void impl() {
        match(PRIMPL);
        match(IDCLASS);
        match(LLAVEABRE);
        while (lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST ) {
            miembroIt();
        }
        match(LLAVECIERRA);
    }
    void herencia() {
        match(DOSPUNTOS);
        tipo();
    }
    void miembroIt(){
        if (lookahead.getTokenName() == PUNTO || lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST) {
            miembro();
            miembroIt();
        }
        else if (lookahead.getTokenName() == LLAVECIERRA) {
            // lambda
        }
        else {
            error(LLAVEABRE, PUNTO, PRFN, PRST);
        }
    }
    void miembro(){ //	punto,fn,st
        if(lookahead.getTokenName() == PRFN || lookahead.getTokenName() == PRST){
            metodo();
        }
        else if(lookahead.getTokenName() == PUNTO){
            constructor();
        }
        else{
            error(PRFN, PRST, PUNTO);
        }
    }
    void constructor (){ //	punto
        match(PUNTO);
        argumentoFormal();
        bloqueMetodo();
    }
    void atributoIt(){ //	pub, Str, Bool,Int
        if (lookahead.getTokenName() == PRPUB || lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT) {
            atributo();
            atributoIt();
        }
        else if (lookahead.getTokenName() == LLAVECIERRA) {
            // lambda
        }
        else {
            error(PRPUB, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, LLAVECIERRA);
        }
    }
    void atributo(){  //	pub, Str, Bool,Int
        if (lookahead.getTokenName() == PRPUB) {
            visibilidad();
        }
        else if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT ) {
            tipo();
        }
        else {
            error(PRPUB, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT);
        }
    }
    void metodo(){  //	fn, st
        if (lookahead.getTokenName() == PRFN) {
            match(PRFN);
            metodoF();
        }
        else if(lookahead.getTokenName() == PRST ) {
            formaMétodo();
            match(PRFN);
            metodoF();
        }
        else {
            error(PRFN, PRST);
        }
    }
    void metodoF(){ //	objectID, void, Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSOBJECT) {
            match(IDCLASSOBJECT);
            argumentoFormal();
            bloqueMetodo();
        }
        else if (lookahead.getTokenName() == PRVOID || lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS) {
            tipoMétodo();
            match(IDCLASSOBJECT);
            argumentoFormal();
            bloqueMetodo();
        }
        else {
            error(IDCLASSOBJECT, PRVOID, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASSARRAY, IDCLASS);
        }
    }
    void visibilidad(){  //	pub
        match(PRPUB);
    }
    void formaMétodo(){ //	st
        match(PRST);
    }
    void bloqueMetodo(){  //	llaveAbre
        match(LLAVEABRE);
        declVarLocalesIt();
        sentenciaIt();
        match(LLAVECIERRA);
    }
    void declVarLocalesIt(){ 	//Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY) {
            declVarLocales();
            declVarLocalesIt();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == IDCLASSOBJECT || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE) {
            // lambda LE FALTA ID SOLO arriba en los OR
        }
        else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, PUNTOYCOMA, IDCLASSOBJECT, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE);
        }
    }
    void declVarLocales(){ //	Str, Bool,Int, idclass, Array
        tipo();
        listaDeclaraciónVariables();
        match(PUNTOYCOMA);
    }
    void listaDeclaraciónVariables(){ //	idMetAt
        match(IDMETAT);
        listaDeclaraciónVariablesF();
    }
    void listaDeclaraciónVariablesF(){ //	coma
        if(lookahead.getTokenName() == COMA) {
            match(COMA);
            listaDeclaraciónVariables();
        }
        else if (lookaheadSig.getTokenName() == PUNTOYCOMA) {
            // lambda
        }
        else {
            error(COMA, PUNTOYCOMA);
        }
    }
    void argumentosFormales(){ //	parAbre
        match(PARABRE);
        argumentosFormalesF();
    }
    void argumentosFormalesF(){ 	//parCierra, Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS) {
            listaArgumentosFormales();
            match(PARCIERRA);
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            match(PARCIERRA);
        }
        else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, PARCIERRA);
        }
    }
    void listaArgumentosFormales(){  //	Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY || lookahead.getTokenName() == IDCLASS) {
            argumentoFormal();
            listaArgumentosFormalesF();
        }else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY, IDCLASS);
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
    void argumentoFormal(){  //	Str, Bool,Int, idclass, Array
        tipo();
        match(IDMETAT);
    }
    void tipoMétodo(){  //	void, Str, Bool,Int, idclass, Array
        if(lookahead.getTokenName() == PRVOID) {
            match(PRVOID);
        }
        else if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDCLASSARRAY) {
            tipo();
        }
        else {
            error(PRVOID, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY);
        }
    }
    void tipo() {  //	Str, Bool,Int, idclass, Array
        if (lookahead.getTokenName() == IDCLASSSTR || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSINT) {
            tipoPrimitivo();
        } else if (lookahead.getTokenName() == IDCLASS) {
            tipoReferencia();
        } else if (lookahead.getTokenName() == IDCLASSARRAY) {
            tipoArreglo();
        } else {
            error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASS, IDCLASSARRAY);
        }
    }
    void tipoPrimitivo(){  //	Str, Bool, Int
        switch (lookahead.getTokenName()) {
            case IDCLASSSTR:
                match(IDCLASSSTR);
                break;
            case IDCLASSBOOL:
                match(IDCLASSBOOL);
                break;
            case IDCLASSINT:
                match(IDCLASSINT);
                break;
            default:
                error(IDCLASSSTR, IDCLASSBOOL, IDCLASSINT);
        }
    }
    void tipoReferencia(){  //	idclass
        match(IDCLASS);
    }
    void tipoArreglo(){  //	Array
        match(IDCLASSARRAY);
        tipoPrimitivo();
    }
    void sentenciaIt(){ 	//puntoYcoma, if,else,while,for, ret, id, self, parAbre, llaveAbre
        if(lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PRIF || lookahead.getTokenName() == PRELSE || lookahead.getTokenName() == PRWHILE || lookahead.getTokenName() == PRFOR || lookahead.getTokenName() == PRRET || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == LLAVEABRE) {
            sentencia();
            sentenciaIt();
        }
        else if (lookaheadSig.getTokenName() == LLAVECIERRA) {
            //match(LLAVECIERRA); no se si va a chequear
            // lambda
        }
        else {
            error(PUNTOYCOMA, PRIF, PRELSE, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, LLAVECIERRA);
        }
    }
    void sentencia(){  //	puntoYcoma, if,while,for, ret, id, self, parAbre, llaveAbre
        switch (lookahead.getTokenName()) {
            case PUNTOYCOMA:
                match(PUNTOYCOMA);
                break;
            case PRSELF:
                asignacion();
                break;
            case IDCLASS: //o IDMETAT no se????
                asignacion();
                break;
            case PARABRE:
                sentenciaSimple();
                match(PUNTOYCOMA);
                break;
            case PRIF:
                match(PRIF);
                match(PARABRE);
                expresion();
                match(PARCIERRA);
                sentencia();
                sentenciaIF();
                break;
            case PRWHILE:
                match(PRWHILE);
                match(PARABRE);
                expresion();
                match(PARCIERRA);
                sentencia();
                break;
            case PRFOR:
                match(PRFOR);
                match(PARABRE);
                tipoPrimitivo();
                match(IDMETAT);
                match(PRIN);
                match(IDMETAT);
                match(PARCIERRA);
                sentencia();
                break;
            case PRRET:
                match(PRRET);
                sentenciaRet();
                break;
            case LLAVEABRE:
                bloque();
                break;
            default:
                error(PUNTOYCOMA, PRIF, PRWHILE, PRFOR, PRRET, IDCLASS, PRSELF, PARABRE, LLAVEABRE);
        }
    }
    void sentenciaIF(){ 	//else
        if (lookahead.getTokenName() == PRELSE) {
            match(PRELSE);
            sentencia();
        }
        else if (lookaheadSig.getTokenName() == PUNTOYCOMA || lookaheadSig.getTokenName() == IDCLASSOBJECT || lookaheadSig.getTokenName() == PRIF || lookaheadSig.getTokenName() == PRELSE || lookaheadSig.getTokenName() == PRWHILE || lookaheadSig.getTokenName() == PRFOR || lookaheadSig.getTokenName() == PRRET || lookaheadSig.getTokenName() == PRSELF || lookaheadSig.getTokenName() == PARABRE || lookaheadSig.getTokenName() == LLAVEABRE || lookaheadSig.getTokenName() == IDCLASSSTR || lookaheadSig.getTokenName() == IDCLASSBOOL || lookaheadSig.getTokenName() == IDCLASSINT || lookaheadSig.getTokenName() == IDCLASSARRAY) {
            // lambda
        }
        else {
            error(PRELSE, PUNTOYCOMA, IDCLASSOBJECT, PRIF, PRWHILE, PRFOR, PRRET, PRSELF, PARABRE, LLAVEABRE, IDCLASSSTR, IDCLASSBOOL, IDCLASSINT, IDCLASSARRAY);
        }
    }
    void sentenciaRet(){ //	puntoYcoma, =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == PUNTOYCOMA) {
            match(PUNTOYCOMA);
        }
        else if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            expresion();
            match(PUNTOYCOMA);
        }
        else {
            error(PUNTOYCOMA, OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
        }
    }
    void bloque(){  //	llaveAbre
        match(LLAVEABRE);
        sentenciaIt();
        match(LLAVECIERRA);
    }
    void asignacion(){  //	id, self
        if (lookahead.getTokenName() == PRSELF) {
            accesoSelfSimple();
            match(OPIGUAL);
            expresion();
        }
        else if (lookahead.getTokenName() == IDCLASS) {
           accesoVarSimple();
           match(OPIGUAL);
           expresion();
        }
        else {
            error(PRSELF, IDCLASS);
        }
    }
    void accesoVarSimple(){  //	id
        match(IDCLASS);
        accesoVarSimpleF();
    }
    void accesoVarSimpleF(){ 	//corAbre, punto
        if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            expresion();
            match(CORCIERRA);
        }
        else if (lookahead.getTokenName() == PUNTO) {
            encadenadoSimpleIt();
        }
        else {
            error(CORABRE, PUNTO);
        }
    }
    void accesoSelfSimple(){  //	self,
        match(PRSELF);
        encadenadoSimpleIt();
    }
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
    void sentenciaSimple(){  //	parAbre
        match(PARABRE);
        expresion();
        match(PARCIERRA);
    }
    void expresion(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            expOr();
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
        }
    }
    void expOr (){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expAnd();
        expOr2();
    }
    void expOr2(){ 	//opOR
        if (lookahead.getTokenName() == OPOR) {
            match(OPOR);
            expAnd();
            expOr2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA ) {
            // lambda
        }
        else {
            error(OPOR, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA);
        }
    }
    void expAnd(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expIgual();
        expAnd2();
    }
    void expAnd2(){ 	//opAND
        if (lookahead.getTokenName() == OPAND) {
            match(OPAND);
            expIgual();
            expAnd2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR) {
            // lambda
        }
        else {
            error(OPAND, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR);
        }
    }
    void expIgual(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expCompuesta();
        expIgual2();
    }
    void expIgual2(){ 	//==, !=
        if(lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            match(lookahead.getTokenName());
            expCompuesta();
            expIgual2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND) {
            // lambda
        }
        else {
            error(OPIGUAL, OPDISTINTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND);

        }
    }
    void expCompuesta(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expAd();
        expCompuestaF();
    }
    void expCompuestaF(){ 	//=<, >, <=, <
        if (lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL) {
            match(lookahead.getTokenName());
            opCompuesto();
            expAd();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            // lambda
        }
        else {
            error(OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO);
        }

    }
    void expAd(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expMul();
        expAd2();
    }
    void expAd2(){ 	//=+, -
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA) {
            opAd();
            expMul();
            expAd2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL ) {
            // lambda
        }
        else {
            error(OPASIGNSUMA, OPRESTA, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL);
        }
    }
    void expMul(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expUn();
        expMul2();
    }
    void expMul2(){ 	//*, /
        if (lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            opMul();
            expUn();
            expMul2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA) {
            // lambda
        }
        else {
            error(OPMULT, OPDIVENT, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA);
        }
    }
    void expUn(){  //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT) {
            opUnario();
            expUn();
        }
         else if(lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            operando();
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
        }
    }
    void opIgual(){ 	//==, !=
        if (lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO) {
            match(lookahead.getTokenName());
        }
        else {
            error(OPIGUAL, OPDISTINTO);
        }
    }
    void opCompuesto(){ 	//=<, >, <=, <
        if (lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL) {
            match(lookahead.getTokenName());
        }
        else {
            error(OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL);
        }
    }
    void opAd(){  //	=+, -
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA) {
            match(lookahead.getTokenName());
        }
        else {
            error(OPASIGNSUMA, OPRESTA);
        }
    }
    void opUnario(){ 	//=+,-, !,++,--, (Int)
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT) {
            match(lookahead.getTokenName());
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPDISTINTO, OPINCR, OPDECR, IDCLASSINT);
        }
    }
    void opMul(){  //	*, /
        if (lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            match(lookahead.getTokenName());
        }
        else {
            error(OPMULT, OPDIVENT);
        }
    }
    void operando(){ //	nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR) {
            literal();
        }
        else if (lookahead.getTokenName() == PARABRE || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRNEW) {
            primario();
        }
        else {
            error(PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
        }
    }
    void operandoF(){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT);
        }
    }
    void literal(){ //	nil, true, false, intLiteral, StrLiteral
        if (lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR)
        {
            match(lookahead.getTokenName());
        }else{
            error(PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR);
        }
    }
    void primario(){ //	parAbre, self, id, idclass, new
        if(lookahead.getTokenName() == PARABRE) {
            expresionParentizada();
        }
        else if (lookahead.getTokenName() == PRSELF) {
            accesoSelf();
        }
        else if (lookahead.getTokenName() == IDCLASS) {
            llamadaMetodoEstatico();
        }
        else if (lookahead.getTokenName() == IDMETAT && lookaheadSig.getTokenName() == PUNTO) {
            accesoVar();
        }
        else if (lookahead.getTokenName() == IDMETAT && lookaheadSig.getTokenName() == PARABRE) {
            llamadaMetodo();
        }
        else if (lookahead.getTokenName() == PRNEW) {
            llamadaConclassor();
        }
        else {
            error(PARABRE, PRSELF, IDCLASS, IDMETAT, PRNEW);
        }
    }
    void expresionParentizada(){ //	parAbre
        match(PARABRE);
        expresion();
        match(PARCIERRA);
        expresionParentizadaF();
    }
    void expresionParentizadaF(){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void accesoSelf(){ //	self
        match(PRSELF);
        accesoSelfF();
    }
    void accesoSelfF(){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void accesoVar (){ //	id
        match(IDMETAT);
        accesoVarF1();
    }
    void accesoVarF1(){ //	punto, corAbre
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            expresion();
            match(CORCIERRA);
            accesoVarF2();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            // lambda
        } else {
            error(PUNTO, CORABRE, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void accesoVarF2(){ 	//punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void llamadaMetodo(){ //	id
        match(IDMETAT);
        argumentosActuales();
        llamadaMetodoF();
    }
    void llamadaMetodoF(){ 	//punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void llamadaMetodoEstatico(){ //	idclass
        match(IDCLASS);
        match(PUNTO);
        llamadaMetodo();
        llamadaMetodoEstaticoF();
    }
    void llamadaMetodoEstaticoF(){ //	punto
        if(lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void llamadaConclassor (){ //	new
        match(PRNEW);
        llamadaConclassorF1();
    }
    void llamadaConclassorF1(){ //	idclass, Str, Bool, Int
        if (lookahead.getTokenName() == IDCLASS) {
            match(IDCLASS);
            argumentosActuales();
            llamadaConclassorF2();
        }
        else if (lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == IDCLASSBOOL || lookahead.getTokenName() == IDCLASSSTR) {
            tipoPrimitivo();
            match(CORABRE);
            expresion();
            match(CORCIERRA);
        }
        else {
            error(IDCLASS, IDCLASSINT, IDCLASSBOOL, IDCLASSSTR);
        }
    }
    void llamadaConclassorF2(){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void argumentosActuales(){ //	parAbre
        match(PARABRE);
        argumentosActualesF();
    }
    void argumentosActualesF(){ //	parCierra, =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        //if =+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        if (lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPNOT || lookahead.getTokenName() == OPINCR || lookahead.getTokenName() == OPDECR || lookahead.getTokenName() == IDCLASSINT || lookahead.getTokenName() == PRNIL || lookahead.getTokenName() == PRTRUE || lookahead.getTokenName() == PRFALSE || lookahead.getTokenName() == LITINT || lookahead.getTokenName() == LITSTR || lookahead.getTokenName() == PARABRE ||lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == IDCLASS || lookahead.getTokenName() == PRNEW) {
            listaExpresiones();
            match(PARCIERRA);
        }
        else if (lookahead.getTokenName() == PARCIERRA) {
            match(PARCIERRA);
        }
        else {
            error(OPASIGNSUMA, OPRESTA, OPNOT, OPINCR, OPDECR, IDCLASSINT, PRNIL, PRTRUE, PRFALSE, LITINT, LITSTR, PARABRE, PRSELF, IDMETAT, IDCLASS, PRNEW, PARCIERRA);
        }
    }
    void listaExpresiones(){ //	=+,-, !,++,--, (Int), nil, true, false, intLiteral, StrLiteral, parAbre, self, id, idclass, new
        expresion();
        listaExpresionesF();
    }
    void listaExpresionesF(){ //	coma
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
    }
    void encadenado(){ //	punto
        match(PUNTO);
        encadenadoF();
    }
    void encadenadoF(){  //	id
        if (lookahead.getTokenName() == IDMETAT && lookaheadSig.getTokenName() == PARABRE) {
            llamadaMetodoEncadenado();
        }
        else if (lookahead.getTokenName() == IDMETAT && (lookaheadSig.getTokenName() == PUNTO || lookaheadSig.getTokenName() == CORABRE || lookaheadSig.getTokenName() == OPASIGNSUMA || lookaheadSig.getTokenName() == OPRESTA || lookaheadSig.getTokenName() == OPNOT || lookaheadSig.getTokenName() == OPINCR || lookaheadSig.getTokenName() == OPDECR || lookaheadSig.getTokenName() == IDCLASSINT || lookaheadSig.getTokenName() == PRNIL || lookaheadSig.getTokenName() == PRTRUE || lookaheadSig.getTokenName() == PRFALSE || lookaheadSig.getTokenName() == LITINT || lookaheadSig.getTokenName() == LITSTR || lookaheadSig.getTokenName() == PARABRE ||lookaheadSig.getTokenName() == PRSELF || lookaheadSig.getTokenName() == IDMETAT || lookaheadSig.getTokenName() == IDCLASS || lookaheadSig.getTokenName() == PRNEW) ) {
            accesoVariableEncadenado();
        }
        else {
            error(IDMETAT);
        }
    }
    void llamadaMetodoEncadenado(){ //	id
        match(IDMETAT);
        argumentosActuales();
        llamadaMetodoEncadenadoF();
    }
    void llamadaMetodoEncadenadoF(){ //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        }
        else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT ) {
            // lambda
        }
        else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void accesoVariableEncadenado(){ //	id
        match(IDMETAT);
        accesoVariableEncadenadoF1();
    }
    void accesoVariableEncadenadoF1() { //	punto, corAbre
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        } else if (lookahead.getTokenName() == CORABRE) {
            match(CORABRE);
            expresion();
            match(CORCIERRA);
            accesoVariableEncadenadoF2();
        } else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            // lambda
        }
        else {
            error(PUNTO, CORABRE, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }
    void accesoVariableEncadenadoF2() { //	punto
        if (lookahead.getTokenName() == PUNTO) {
            encadenado();
        } else if (lookahead.getTokenName() == PUNTOYCOMA || lookahead.getTokenName() == PARCIERRA || lookahead.getTokenName() == IDMETAT || lookahead.getTokenName() == PRSELF || lookahead.getTokenName() == CORCIERRA || lookahead.getTokenName() == COMA || lookahead.getTokenName() == OPOR || lookahead.getTokenName() == OPAND || lookahead.getTokenName() == OPIGUAL || lookahead.getTokenName() == OPDISTINTO || lookahead.getTokenName() == OPMAYOR || lookahead.getTokenName() == OPMAYORIGUAL || lookahead.getTokenName() == OPMENOR || lookahead.getTokenName() == OPMENORIGUAL || lookahead.getTokenName() == OPASIGNSUMA || lookahead.getTokenName() == OPRESTA || lookahead.getTokenName() == OPMULT || lookahead.getTokenName() == OPDIVENT) {
            // lambda
        } else {
            error(PUNTO, PUNTOYCOMA, PARCIERRA, IDMETAT, PRSELF, CORCIERRA, COMA, OPOR, OPAND, OPIGUAL, OPDISTINTO, OPMAYOR, OPMAYORIGUAL, OPMENOR, OPMENORIGUAL, OPASIGNSUMA, OPRESTA, OPMULT, OPDIVENT);
        }
    }

}
