package com.analyzer;

import com.model.Token;
import java.util.ArrayList;
import java.util.HashMap;

public class SemanticAnalyzer {
    public static boolean analyze(ArrayList<Token> tokens) {
        HashMap<String, String> variables = new HashMap<>();
        int i = 0;
        while (i < tokens.size()) {
            String dataType = tokens.get(i).getLexeme();
            i++;
            String identifier = tokens.get(i).getLexeme();
            i++;
            String value = null;
            if (i < tokens.size() && tokens.get(i).getType().equals("<assignment_operator>")) {
                i++;
                value = tokens.get(i).getLexeme();
                i++;
            }
            i++; // skip ;

            // Check for duplicate identifiers
            if (variables.containsKey(identifier)) return false;

            // Check type-value compatibility
            if (value != null) {
                if (dataType.equals("int") && !value.matches("^[0-9]+$")) return false;
                if ((dataType.equals("double") || dataType.equals("float")) && !value.matches("^[0-9]+\\.[0-9]+$")) return false;
                if (dataType.equals("String") && !(value.startsWith("\"") && value.endsWith("\""))) return false;
                if (dataType.equals("char") && !(value.startsWith("'") && value.endsWith("'"))) return false;
                if (dataType.equals("boolean") && !(value.equals("true") || value.equals("false"))) return false;
                // Add more checks as needed for other types
            }

            variables.put(identifier, dataType);
        }
        return true;
    }
}