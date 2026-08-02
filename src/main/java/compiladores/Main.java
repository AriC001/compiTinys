package compiladores;

import java.io.File;

import compiladores.AST.ProgramNode;
import compiladores.lexicon.Analyzer;
import compiladores.semantic.SemanticAnalyzer;
import compiladores.sintax.SintaxAnalyzer;
import compiladores.AST.NodeType;

public class Main {
    public static void main(String[] args) {
         if (args.length == 0) {
            System.out.println("Error: No file name provided as an argument.");
            // Exit the program if no argument is given
            System.exit(1);
        }
        // 2. Get the file path from the first argument
        String fileName = args[0];

        // 3. Create a File object (optional, can also use Paths directly)
        File file = new File(fileName);
        Analyzer lexiconAnalyzer = new Analyzer(file);
        SintaxAnalyzer sintaxAnalyzer = new SintaxAnalyzer(lexiconAnalyzer);
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        System.out.println("Inicio analisis");
        ProgramNode root = sintaxAnalyzer.parse();
        try {
            System.out.println("Análisis sintáctico completado: " + root);
        } catch (RuntimeException ex) {
            System.out.println("Error en el análisis");
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            analyzer.visit(root);
            System.out.println("Análisis Semantico completado");
            lexiconAnalyzer.cerrar();
        }

         // 4. Validate file existence and readability. Necesary? Dont think so
        // if (!file.exists() || !file.isFile() || !file.canRead()) {
        //     System.out.println("Error: The specified file either does not exist, is not a regular file, or cannot be read.");
        //     System.exit(1);
        // }
    }
}