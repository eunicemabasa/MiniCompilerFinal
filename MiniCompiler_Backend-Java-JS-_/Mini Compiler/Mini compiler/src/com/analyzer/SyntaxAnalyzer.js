class SyntaxAnalyzer {
    static analyze(tokens) {
        let i = 0;
        while (i < tokens.length) {
            // Expect: <data_type> <identifier> [ = <value> ] ;
            if (tokens[i].getType() !== '<data_type>') return false;
            i++;
            if (i >= tokens.length || tokens[i].getType() !== '<identifier>') return false;
            i++;
            if (i < tokens.length && tokens[i].getType() === '<assignment_operator>') {
                i++;
                if (i >= tokens.length || tokens[i].getType() !== '<value>') return false;
                i++;
            }
            if (i >= tokens.length || tokens[i].getType() !== '<delimiter>') return false;
            i++;
        }
        return true;
    }
}

module.exports = SyntaxAnalyzer;
