package compiladores.lexicon;

public class Token {
    public String tokenName;
    public String lexeme;
    public int lineCode;
    public int columnCode;

    public Token(String tokenName, String lexeme, int lineCode, int columnCode) {
        this.tokenName = tokenName;
        this.lexeme = lexeme;
        this.lineCode = lineCode;
        this.columnCode = columnCode;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
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

}
