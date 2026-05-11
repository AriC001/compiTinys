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
        // Aquí se implementará el reconocimiento de tokens usando el buffer
        // Ejemplo de avance de caracteres:
        char c = siguienteCaracter();
        c = esEspacioComentario(c);
        // Lógica de reconocimiento de tokens irá aquí
        if(Character.isDigit(c)){
            return leerNumero(c);
        }
        if (Character.isLetter(c) || c == '_') {
            return leerToken(c);
        }
        if (c == '"' ) {
            return leerCadena(c);
        }
        switch (c) {
            case '(': return new Token(TokenType.PARABRE, "(", posicionLineaActual, posicionColumnaActual);
            case ')': return new Token(TokenType.PARCIERRA, ")", posicionLineaActual, posicionColumnaActual);
            case '[': return new Token(TokenType.CORABRE, "[", posicionLineaActual, posicionColumnaActual);
            case ']': return new Token(TokenType.CORCIERRA, "]", posicionLineaActual, posicionColumnaActual);
            case '{': return new Token(TokenType.LLAVEABRE, "{", posicionLineaActual, posicionColumnaActual);
            case '}': return new Token(TokenType.LLAVECIERRA, "}", posicionLineaActual, posicionColumnaActual);
            case '.': return new Token(TokenType.PUNTO, ".", posicionLineaActual, posicionColumnaActual);
            case ';': return new Token(TokenType.PUNTOYCOMA, ";", posicionLineaActual, posicionColumnaActual);
            case ',': return new Token(TokenType.COMA, ",", posicionLineaActual, posicionColumnaActual);
            case ':': return new Token(TokenType.DOSPUNTOS, ":", posicionLineaActual, posicionColumnaActual);
            // operadores y otros símbolos
            case '+': return new Token(TokenType.OPSUMA, "+", posicionLineaActual, posicionColumnaActual);
            case '-': return new Token(TokenType.OPRESTA, "-", posicionLineaActual, posicionColumnaActual);
            case '*': return new Token(TokenType.OPMULT, "*", posicionLineaActual, posicionColumnaActual);
            case '/': return new Token(TokenType.OPDIVENT, "/", posicionLineaActual, posicionColumnaActual);
            case '&': return new Token(TokenType.OPAND, "&", posicionLineaActual, posicionColumnaActual);
            case '|': return new Token(TokenType.OPAND, "|", posicionLineaActual, posicionColumnaActual);
            case '!': return new Token(TokenType.OPINCR, "!", posicionLineaActual, posicionColumnaActual);
        }

        /*if (c == (char)-1) {
            return new Token(TokenType.EOF, "", posicionLineaActual, posicionColumnaActual);
        }*/
        // Si no se reconoce el carácter, se puede lanzar un error o devolver un token de error
        return new Token(TokenType.EOF, "", posicionLineaActual, posicionColumnaActual);
    }

    private char esEspacioComentario(char c) {
        boolean comentario = true;
        while (comentario) {

            if (c == '\n' || c == ' ' || c == '\r' || c == '\t' || c == '\f' || c == '\u000B') {
                c = siguienteCaracter();
                continue;
            }

            // Comentario de linea //
            if (c == '/' && mirarSiguiente() == '/') {
                c = siguienteNoComentario(1); // Comentario 1 linea
                continue;
            }

            // Comentario multilinea /* ... */
            if (c == '/' && mirarSiguiente() == '*') {
                c = siguienteNoComentario(2); // Comentario multilinea
                continue;
            }
            comentario = false;
            break; // no es whitespace ni comentario
        }
        return c;
    }

    private char mirarSiguiente() {
        if (bufferLinea == null) {
            return (char)-1; // EOF
        }
        if (indiceLinea >= bufferLinea.length()) {
            return '\n'; // Salto de línea lógico al final de la línea
        }
        return bufferLinea.charAt(indiceLinea+1);
    }

    private char siguienteNoComentario(int tipoComentario) {
        char c = siguienteCaracter();
        if (tipoComentario == 1) { // Comentario de línea
            while (c != '\n' && c != (char)-1) {
                c = siguienteCaracter();
            }
        } else if (tipoComentario == 2) { // Comentario multilinea
            boolean comentarioCerrado = false;
            while (!comentarioCerrado) {
                if (c == (char)-1) {
                    break; // EOF
                }
                if (c == '*' && mirarSiguiente() == '/') {
                    siguienteCaracter(); // Consumir '*'
                    siguienteCaracter(); // Consumir '/'
                    comentarioCerrado = true;
                    break;
                }
                c = siguienteCaracter();
            }
        }
        return c;
    }

    private Token leerNumero(char c) {
        StringBuilder numero = new StringBuilder();
        while (Character.isDigit(c)) {
            numero.append(c);
            c = siguienteCaracter();
        }
        // Aquí se podría validar el número y crear un token LITINT
        return new Token(TokenType.LITINT, numero.toString(), posicionLineaActual, posicionColumnaActual - numero.length());
    }

    private Token leerToken(char c) {
        StringBuilder token = new StringBuilder();
        while (Character.isLetterOrDigit(c) || c == '_') {
            token.append(c);
            c = siguienteCaracter();
        }
        String tokenStr = token.toString();
        // Verificar si es una palabra reservada
        if (reservedWords.containsKey(tokenStr)) {
            return new Token(reservedWords.get(tokenStr), tokenStr, posicionLineaActual, posicionColumnaActual - tokenStr.length());
        }
        // Si no es una palabra reservada, es un identificador
        return new Token(TokenType.IDMETAT, tokenStr, posicionLineaActual, posicionColumnaActual - tokenStr.length());
    }

    private Token leerCadena(char c) {
        StringBuilder cadena = new StringBuilder();
        while (true) {
            c = siguienteCaracter();
            if (c == '"' || c == (char)-1) {
                break; // Fin del literal de cadena
            }
            if (c == '\\') { // Manejar caracteres de escape
                char siguiente = siguienteCaracter();
                switch (siguiente) {
                    case 'n': cadena.append('\n'); break;
                    case 't': cadena.append('\t'); break;
                    case 'r': cadena.append('\r'); break;
                    case '"': cadena.append('"'); break;
                    case '\\': cadena.append('\\'); break;
                    default: cadena.append(siguiente); break; // Caracter no reconocido, se agrega tal cual
                }
            } else {
                cadena.append(c);
            }
        }
        return new Token(TokenType.LITSTR, cadena.toString(), posicionLineaActual, posicionColumnaActual - cadena.length() - 2);
    }
}
