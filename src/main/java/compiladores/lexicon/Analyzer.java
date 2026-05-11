package compiladores.lexicon;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Analyzer {
    public File file;
    private BufferedReader reader;
    private String bufferLinea = null;
    private int indiceLinea = 0;
    public int posicionActual = 0;
    public int posicionLineaActual = 0;
    public int posicionColumnaActual = 0;
    private Map<String, TokenType> reservedWords = new HashMap<>();

    public Analyzer(File file) {
        this.file = file;
        try {
            this.reader = new BufferedReader(new FileReader(file));
            cargarSiguienteLinea();
        } catch (IOException e) {
            throw new RuntimeException("Error al abrir el archivo: " + e.getMessage());
        }
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

    private RuntimeException errorLexico(String mensaje) {
        return new RuntimeException(mensaje + " (linea " + posicionLineaActual + ", columna " + (posicionColumnaActual + 1) + ")");
    }

    private char mirarActual() {
        if (bufferLinea == null) {
            return (char) -1;
        }
        if (indiceLinea >= bufferLinea.length()) {
            return '\n';
        }
        return bufferLinea.charAt(indiceLinea);
    }

    private char mirarSiguiente() {
        if (bufferLinea == null) {
            return (char) -1;
        }
        if (indiceLinea + 1 < bufferLinea.length()) {
            return bufferLinea.charAt(indiceLinea + 1);
        }
        return '\n';
    }

    // Cargar la siguiente línea del archivo en el buffer
    private void cargarSiguienteLinea() {
        try {
            bufferLinea = reader.readLine();
            indiceLinea = 0;
            if (bufferLinea != null) {
                posicionLineaActual++;
                posicionColumnaActual = 0;
            }
        } catch (IOException e) {
            bufferLinea = null;
        }
    }

    // Obtener el siguiente carácter del buffer, cargar nueva línea si es necesario
    private char siguienteCaracter() {
        if (bufferLinea == null) {
            return (char)-1; // EOF
        }
        if (indiceLinea >= bufferLinea.length()) {
            posicionActual++;
            cargarSiguienteLinea();
            if (bufferLinea == null) {
                return (char)-1; // EOF
            }
            // Al cargar nueva línea, devolver salto de línea lógico
            return '\n';
        }
        char c = bufferLinea.charAt(indiceLinea);
        indiceLinea++;
        posicionColumnaActual++;
        posicionActual++;
        return c;
    }

    // Cerrar el BufferedReader
    public void cerrar() {
        try {
            if (reader != null) reader.close();
        } catch (IOException e) {
            // Ignorar
        }
    }

    public Token nextToken(){
        saltarEspaciosYComentarios();

        char c = mirarActual();
        if (c == (char) -1) {
            return new Token(TokenType.EOF, "", posicionLineaActual, posicionColumnaActual + 1);
        }

        int lineaInicio = posicionLineaActual;
        int columnaInicio = posicionColumnaActual + 1;

        if (Character.isDigit(c)) {
            return leerNumero(lineaInicio, columnaInicio);
        }
        if (Character.isLetter(c) || c == '_') {
            return leerToken(lineaInicio, columnaInicio);
        }
        if (c == '"') {
            siguienteCaracter(); // consumir comilla de apertura
            return leerCadena(lineaInicio, columnaInicio);
        }

        switch (c) {
            case '(':
                siguienteCaracter();
                return new Token(TokenType.PARABRE, "(", lineaInicio, columnaInicio);
            case ')':
                siguienteCaracter();
                return new Token(TokenType.PARCIERRA, ")", lineaInicio, columnaInicio);
            case '[':
                siguienteCaracter();
                return new Token(TokenType.CORABRE, "[", lineaInicio, columnaInicio);
            case ']':
                siguienteCaracter();
                return new Token(TokenType.CORCIERRA, "]", lineaInicio, columnaInicio);
            case '{':
                siguienteCaracter();
                return new Token(TokenType.LLAVEABRE, "{", lineaInicio, columnaInicio);
            case '}':
                siguienteCaracter();
                return new Token(TokenType.LLAVECIERRA, "}", lineaInicio, columnaInicio);
            case '.':
                siguienteCaracter();
                return new Token(TokenType.PUNTO, ".", lineaInicio, columnaInicio);
            case ';':
                siguienteCaracter();
                return new Token(TokenType.PUNTOYCOMA, ";", lineaInicio, columnaInicio);
            case ',':
                siguienteCaracter();
                return new Token(TokenType.COMA, ",", lineaInicio, columnaInicio);
            case ':':
                siguienteCaracter();
                return new Token(TokenType.DOSPUNTOS, ":", lineaInicio, columnaInicio);
            case '+':
                siguienteCaracter();
                if (mirarActual() == '+') {
                    siguienteCaracter();
                    return new Token(TokenType.OPINCR, "++", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPSUMA, "+", lineaInicio, columnaInicio);
            case '-':
                siguienteCaracter();
                if (mirarActual() == '-') {
                    siguienteCaracter();
                    return new Token(TokenType.OPDECR, "--", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPRESTA, "-", lineaInicio, columnaInicio);
            case '*':
                siguienteCaracter();
                return new Token(TokenType.OPMULT, "*", lineaInicio, columnaInicio);
            case '/':
                siguienteCaracter();
                return new Token(TokenType.OPDIVENT, "/", lineaInicio, columnaInicio);
            case '&':
                siguienteCaracter();
                if (mirarActual() == '&') {
                    siguienteCaracter();
                    return new Token(TokenType.OPAND, "&&", lineaInicio, columnaInicio);
                }
                throw errorLexico("Se esperaba '&&'");
            case '|':
                siguienteCaracter();
                if (mirarActual() == '|') {
                    siguienteCaracter();
                    return new Token(TokenType.OPOR, "||", lineaInicio, columnaInicio);
                }
                throw errorLexico("Se esperaba '||'");
            case '!':
                siguienteCaracter();
                if (mirarActual() == '=') {
                    siguienteCaracter();
                    return new Token(TokenType.OPDISTINTO, "!=", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPNOT, "!", lineaInicio, columnaInicio);
            case '=':
                siguienteCaracter();
                if (mirarActual() == '=') {
                    siguienteCaracter();
                    return new Token(TokenType.OPIGUAL, "==", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPASIGN, "=", lineaInicio, columnaInicio);
            case '<':
                siguienteCaracter();
                if (mirarActual() == '=') {
                    siguienteCaracter();
                    return new Token(TokenType.OPMENORIGUAL, "<=", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPMENOR, "<", lineaInicio, columnaInicio);
            case '>':
                siguienteCaracter();
                if (mirarActual() == '=') {
                    siguienteCaracter();
                    return new Token(TokenType.OPMAYORIGUAL, ">=", lineaInicio, columnaInicio);
                }
                return new Token(TokenType.OPMAYOR, ">", lineaInicio, columnaInicio);
            default:
                throw errorLexico("Caracter no reconocido: '" + c + "'");
        }
    }

    private void saltarEspaciosYComentarios() {
        while (true) {
            char c = mirarActual();

            if (c == '\uFEFF' || c == '\n' || c == ' ' || c == '\r' || c == '\t' || c == '\f' || c == '\u000B') {
                siguienteCaracter();
                continue;
            }

            if (c == '/' && mirarSiguiente() == '/') {
                siguienteCaracter(); // '/'
                siguienteCaracter(); // '/'
                while (mirarActual() != '\n' && mirarActual() != (char) -1) {
                    siguienteCaracter();
                }
                continue;
            }

            if (c == '/' && mirarSiguiente() == '*') {
                siguienteCaracter(); // '/'
                siguienteCaracter(); // '*'
                while (true) {
                    char actual = mirarActual();
                    if (actual == (char) -1) {
                        throw errorLexico("Comentario multilinea sin cerrar");
                    }
                    if (actual == '*' && mirarSiguiente() == '/') {
                        siguienteCaracter(); // '*'
                        siguienteCaracter(); // '/'
                        break;
                    }
                    siguienteCaracter();
                }
                continue;
            }

            return;
        }
    }

    private Token leerNumero(int lineaInicio, int columnaInicio) {
        StringBuilder numero = new StringBuilder();
        while (Character.isDigit(mirarActual())) {
            numero.append(siguienteCaracter());
        }
        return new Token(TokenType.LITINT, numero.toString(), lineaInicio, columnaInicio);
    }

    private Token leerToken(int lineaInicio, int columnaInicio) {
        StringBuilder token = new StringBuilder();
        while (Character.isLetterOrDigit(mirarActual()) || mirarActual() == '_') {
            token.append(siguienteCaracter());
        }
        String tokenStr = token.toString();
        if (reservedWords.containsKey(tokenStr)) {
            return new Token(reservedWords.get(tokenStr), tokenStr, lineaInicio, columnaInicio);
        }
        return new Token(TokenType.IDMETAT, tokenStr, lineaInicio, columnaInicio);
    }

    private Token leerCadena(int lineaInicio, int columnaInicio) {
        StringBuilder cadena = new StringBuilder();
        while (true) {
            char c = siguienteCaracter();
            if (c == (char) -1 || c == '\n') {
                throw errorLexico("Cadena sin cerrar");
            }
            if (c == '"') {
                break;
            }
            if (c == '\\') { // Manejar caracteres de escape
                char siguiente = siguienteCaracter();
                if (siguiente == (char) -1 || siguiente == '\n') {
                    throw errorLexico("Secuencia de escape incompleta en cadena");
                }
                switch (siguiente) {
                    case 'n': cadena.append('\n'); break;
                    case 't': cadena.append('\t'); break;
                    case 'r': cadena.append('\r'); break;
                    case '"': cadena.append('"'); break;
                    case '\\': cadena.append('\\'); break;
                    default: cadena.append(siguiente); break;
                }
            } else {
                cadena.append(c);
            }
        }
        return new Token(TokenType.LITSTR, cadena.toString(), lineaInicio, columnaInicio);
    }
}
