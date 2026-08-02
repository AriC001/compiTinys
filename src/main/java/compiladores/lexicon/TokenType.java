package compiladores.lexicon;

// Alfabeto = {a-z,A-Z,0-9,_,.,(,),[,],{,},;,=,+,-,*,/,<,>,!,¡,¿,?,&,|,",\}

import compiladores.semantic.SemanticAnalyzer;

public enum TokenType {
    // Identificadores
    IDMETAT, // (a-z)(a-z,A-Z,0-9)*
    IDCLASS, // (A-Z)(a-z,A-Z,0-9)*
    IDCLASSINT, // Int 
    IDCLASSSTR, // Str 
    IDCLASSBOOL, // Bool 
    IDCLASSARRAY, // Array 
    IDCLASSIO, // IO 
    IDCLASSOBJECT, // Object 
    IDCLASSITERATOR, // Iterator 

    // Literales
    LITINT, // (0-9)(0-9)*
    LITSTR, // "(a-z,A-Z,0-9,_,.,(,),[,],{,},;,=,+,-,*,/,<,>,!,¡,¿,?,&,|,",\)*"
    LITBOOL, // true | false
    LITNIL, // nil

    // Simbolos del código
    PARABRE,  // (
    PARCIERRA, // )
    CORABRE, // [
    CORCIERRA, // ]
    LLAVEABRE, // {
    LLAVECIERRA, // }
    PUNTO, // .
    PUNTOYCOMA, // ;
    COMA,  // ,
    DOSPUNTOS, // :
    EOF, // End of file

    // Palabras reservadas
    PRCLASS, // class 
    PRIMPL, // impl 
    PRIF, // if 
    PRELSE, // else 
    PRTRUE, // true 
    PRFALSE, // false 
    PRNIL, // nil 
    PRRET, // ret 
    PRWHILE, // while 
    PRFOR, // for 
    PRNEW, // new 
    PRFN, // fn 
    PRST, // st 
    PRPUB, // pub 
    PRSELF, // self 
    PRDIV, // div 
    PRIN, // in 
    PRVOID, // void
    PSTART, // start

    // Operaciones
    OPSUMA, // +
    OPRESTA, // - 
    OPMULT, // * 
    OPDIVENT, // / 
    OPINCR, // ++
    OPDECR, // -- 
    OPAND, // &&
    OPOR, // ||
    OPNOT, // !
    OPMENOR, // <
    OPMENORIGUAL, // <=
    OPIGUAL, // ==
    OPMAYOR, // >
    OPMAYORIGUAL, // >=
    OPDISTINTO, // !=
    OPASIGN, // =
    OPASIGNSUMA // =+
    ;

}
