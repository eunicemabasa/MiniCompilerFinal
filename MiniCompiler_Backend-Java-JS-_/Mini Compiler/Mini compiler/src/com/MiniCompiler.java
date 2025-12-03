package com;
import com.analyzer.LexicalAnalyzer;
import com.analyzer.SemanticAnalyzer;
import com.analyzer.SyntaxAnalyzer;
import com.model.Token;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MiniCompiler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the path to the .txt file containing Java variable declarations:");
            String filePath = scanner.nextLine();

            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                System.out.println("File contents:");
                System.out.println(content);
                System.out.println();

                ArrayList<Token> tokens = LexicalAnalyzer.tokenize(content);

                if (!LexicalAnalyzer.isValidLexically(tokens)) {
                    System.out.println("Lexical analysis phase FAILED! Try again, pls :)");
                    continue; // Loop back
                }
                System.out.println("Lexical analysis phase PASSED :D");

                if (!SyntaxAnalyzer.analyze(tokens)) {
                    System.out.println("SYNTAX ERROR! try again :)");
                    continue; // Loop back
                }
                System.out.println("Syntax Analysis PASSED! :D");

                if (!SemanticAnalyzer.analyze(tokens)) {
                    System.out.println("Semantic Analysis FAILED! Try again :)");
                    continue; // Loop back
                }
                System.out.println("Semantic Analysis PASSED! :D");
                System.out.println("All analyses passed! Compilation successful.");
                break; // Exit loop on success

            } catch (IOException e) {
                System.out.println("Error reading file. Please check the path and try again.");
                continue; // Loop back
            }
        }
        scanner.close();
    }
}