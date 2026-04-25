package compiladores.lexicon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {
    public File file;
    public int posicionActual = 0;
    public int posicionLineaActual = 0;
    public int posicionColumnaActual = 0;
    private Map<String, TokenType> reservedWords = new HashMap<>();


    public Analyzer(File file) {
        this.file = file;
        // Initialize reserved words
        reservedWords.put("if", TokenType.PRIF);
        reservedWords.put("else", TokenType.PRELSE);
        reservedWords.put("class", TokenType.PRCLASS);
        reservedWords.put("impl", TokenType.PRIMPL);
        reservedWords.put("true", TokenType.PRTRUE);
        reservedWords.put("false", TokenType.PRFALSE);
        reservedWords.put("nil", TokenType.PRNIL);
        reservedWords.put("ret", TokenType.PRRET);
        reservedWords.put("while", TokenType.PRWHILE);
        reservedWords.put("for", TokenType.PRFOR);
        reservedWords.put("new", TokenType.PRNEW);
        reservedWords.put("fn", TokenType.PRFN);
        reservedWords.put("st", TokenType.PRST);
        reservedWords.put("pub", TokenType.PRPUB);
        reservedWords.put("self", TokenType.PRSELF);
        reservedWords.put("div", TokenType.PRDIV);
        reservedWords.put("in", TokenType.PRIN);
        reservedWords.put("void", TokenType.PRVOID);

        reservedWords.put("Str", TokenType.IDCLASSSTR);
        reservedWords.put("Bool", TokenType.IDCLASSBOOL);
        reservedWords.put("Array", TokenType.IDCLASSARRAY);
        reservedWords.put("IO", TokenType.IDCLASSIO);
        reservedWords.put("Object", TokenType.IDCLASSOBJECT);
        reservedWords.put("Iterator", TokenType.IDCLASSITERATOR);
        reservedWords.put("Int", TokenType.IDCLASSINT);
    }

    public void nextToken(){

    }
}
