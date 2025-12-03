package com.analyzer;

import com.model.Token;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    public static ArrayList<Token> tokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        String[] lines = input.split("\n");
        for (String line : lines) {
            line = line.trim();
            int i = 0;
            while (i < line.length()) {
                char c = line.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (c == ';') {
                    tokens.add(new Token(";", "<delimiter>"));
                    i++;
                    continue;
                }
                if (c == '=') {
                    tokens.add(new Token("=", "<assignment_operator>"));
                    i++;
                    continue;
                }
                if (c == '"') {
                    StringBuilder str = new StringBuilder();
                    str.append(c);
                    i++;
                    while (i < line.length() && line.charAt(i) != '"') {
                        str.append(line.charAt(i));
                        i++;
                    }
                    if (i < line.length()) {
                        str.append(line.charAt(i));
                        i++;
                    }
                    tokens.add(new Token(str.toString(), "<value>"));
                    continue;
                }
                if (c == '\'') {
                    StringBuilder chr = new StringBuilder();
                    chr.append(c);
                    i++;
                    while (i < line.length() && line.charAt(i) != '\'') {
                        chr.append(line.charAt(i));
                        i++;
                    }
                    if (i < line.length()) {
                        chr.append(line.charAt(i));
                        i++;
                    }
                    tokens.add(new Token(chr.toString(), "<value>"));
                    continue;
                }
                StringBuilder token = new StringBuilder();
                while (i < line.length() && !Character.isWhitespace(line.charAt(i))
                        && line.charAt(i) != '=' && line.charAt(i) != ';'
                        && line.charAt(i) != '"' && line.charAt(i) != '\'') {
                    token.append(line.charAt(i));
                    i++;
                }
                if (token.length() > 0) {
                    String lexeme = token.toString();
                    String type;
                    if (isDataType(lexeme)) {
                        type = "<data_type>";
                    } else if (isValue(lexeme)) {
                        type = "<value>";
                    } else if (isIdentifier(lexeme)) {
                        type = "<identifier>";
                    } else {
                        type = "<unknown>";
                    }
                    tokens.add(new Token(lexeme, type));
                }
            }
        }
        return tokens;
    }

    private static boolean isDataType(String lexeme) {
        return lexeme.equals("int") || lexeme.equals("float") || lexeme.equals("double")
                || lexeme.equals("char") || lexeme.equals("boolean") || lexeme.equals("String")
                || lexeme.equals("long") || lexeme.equals("short") || lexeme.equals("byte");
    }

    private static boolean isValue(String lexeme) {
        // String literals
        if (lexeme.startsWith("\"") && lexeme.endsWith("\"") && lexeme.length() >= 2) return true;
        // Char literals
        if (lexeme.startsWith("'") && lexeme.endsWith("'") && lexeme.length() >= 2) return true;
        // Boolean literals
        if (lexeme.equals("true") || lexeme.equals("false")) return true;
        // Integer literals
        if (Pattern.matches("^[0-9]+$", lexeme)) return true;
        // Floating point literals
        if (Pattern.matches("^[0-9]+\\.[0-9]+$", lexeme)) return true;
        return false;
    }

    private static boolean isIdentifier(String lexeme) {
        if (lexeme == null || lexeme.isEmpty()) return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
        Matcher matcher = pattern.matcher(lexeme);
        return matcher.matches();
    }

    public static boolean isValidLexically(ArrayList<Token> tokens) {
        for (Token t : tokens) {
            if (t.getType().equals("<unknown>")) return false;
        }
        return true;
    }
}