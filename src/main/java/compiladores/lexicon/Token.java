package compiladores.lexicon;

public class Token {
    private TokenType tokenName;
    private String lexeme;
    private int lineCode;
    private int columnCode;

    public Token(TokenType tokenName, String lexeme, int lineCode, int columnCode) {
        this.tokenName = tokenName;
        this.lexeme = lexeme;
        this.lineCode = lineCode;
        this.columnCode = columnCode;
    }


    public String getTokenName() {
        return tokenName.name();
    }

    public void setTokenType(TokenType tokenName) {
        this.tokenName = tokenName;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public void addLexeme(String newChar) {
        this.lexeme += newChar;
    }


    public int getLineCode() {
        return lineCode;
    }

    public void setLineCode(int lineCode) {
        this.lineCode = lineCode;
    }

    public void incrementLineCode(){
        this.lineCode += 1;
    }

    public int getColumnCode() {
        return lineCode;
    }

    public void setColumnCode(int lineaCodigo) {
        this.lineCode = lineaCodigo;
    }

    public void incrementColumnCode(){
        this.columnCode += 1;
    }

    @Override
    public String toString() {
        return tokenName.name() + " | " + lexeme + " | Line: " + lineCode + " | Column: " + columnCode;
    }

}
