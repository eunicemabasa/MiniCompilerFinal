package com.analyzer;

import com.model.Token;
import java.util.ArrayList;

public class SyntaxAnalyzer {
    public static boolean analyze(ArrayList<Token> tokens) {
        int i = 0;
        while (i < tokens.size()) {
            // Expect: <data_type> <identifier> [ = <value> ] ;
            if (!tokens.get(i).getType().equals("<data_type>")) return false;
            i++;
            if (i >= tokens.size() || !tokens.get(i).getType().equals("<identifier>")) return false;
            i++;
            if (i < tokens.size() && tokens.get(i).getType().equals("<assignment_operator>")) {
                i++;
                if (i >= tokens.size() || !tokens.get(i).getType().equals("<value>")) return false;
                i++;
            }
            if (i >= tokens.size() || !tokens.get(i).getType().equals("<delimiter>")) return false;
            i++;
        }
        return true;
    }
}