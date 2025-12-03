class SemanticAnalyzer {
    static analyze(tokens) {
        const variables = new Map();
        let i = 0;
        while (i < tokens.length) {
            const dataType = tokens[i].getLexeme();
            i++;
            const identifier = tokens[i].getLexeme();
            i++;
            let value = null;
            if (i < tokens.length && tokens[i].getType() === '<assignment_operator>') {
                i++;
                value = tokens[i].getLexeme();
                i++;
            }
            i++; // skip ;

            // Check for duplicate identifiers
            if (variables.has(identifier)) return false;

            // Check type-value compatibility
            if (value !== null) {
                if (dataType === 'int' && !/^[0-9]+$/.test(value)) return false;
                if (dataType === 'double' && !/^[0-9]+\.[0-9]+$/.test(value)) return false;
                if (dataType === 'String' && !(value.startsWith('"') && value.endsWith('"'))) return false;
                if (dataType === 'char' && !(value.startsWith("'") && value.endsWith("'"))) return false;
                // Add more checks as needed for other types
            }

            variables.set(identifier, dataType);
        }
        return true;
    }
}

module.exports = SemanticAnalyzer;
