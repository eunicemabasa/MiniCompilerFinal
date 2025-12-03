const Token = require('./token');

class LexicalAnalyzer {
    static tokenize(input) {
        const tokens = [];
        const lines = input.split('\n');
        for (const line of lines) {
            const trimmedLine = line.trim();
            let i = 0;
            while (i < trimmedLine.length) {
                const c = trimmedLine.charAt(i);
                if (/\s/.test(c)) {
                    i++;
                    continue;
                }
                if (c === ';') {
                    tokens.push(new Token(';', '<delimiter>'));
                    i++;
                    continue;
                }
                if (c === '=') {
                    tokens.push(new Token('=', '<assignment_operator>'));
                    i++;
                    continue;
                }
                if (c === '"') {
                    let str = '';
                    str += c;
                    i++;
                    while (i < trimmedLine.length && trimmedLine.charAt(i) !== '"') {
                        str += trimmedLine.charAt(i);
                        i++;
                    }
                    if (i < trimmedLine.length) {
                        str += trimmedLine.charAt(i);
                        i++;
                    }
                    tokens.push(new Token(str, '<value>'));
                    continue;
                }
                if (c === "'") {
                    let chr = '';
                    chr += c;
                    i++;
                    while (i < trimmedLine.length && trimmedLine.charAt(i) !== "'") {
                        chr += trimmedLine.charAt(i);
                        i++;
                    }
                    if (i < trimmedLine.length) {
                        chr += trimmedLine.charAt(i);
                        i++;
                    }
                    tokens.push(new Token(chr, '<value>'));
                    continue;
                }
                let token = '';
                while (i < trimmedLine.length && !/\s/.test(trimmedLine.charAt(i))
                        && trimmedLine.charAt(i) !== '=' && trimmedLine.charAt(i) !== ';'
                        && trimmedLine.charAt(i) !== '"' && trimmedLine.charAt(i) !== "'") {
                    token += trimmedLine.charAt(i);
                    i++;
                }
                if (token.length > 0) {
                    const lexeme = token;
                    let type;
                    if (this.isDataType(lexeme)) {
                        type = '<data_type>';
                    } else if (this.isValue(lexeme)) {
                        type = '<value>';
                    } else if (this.isIdentifier(lexeme)) {
                        type = '<identifier>';
                    } else {
                        type = '<unknown>';
                    }
                    tokens.push(new Token(lexeme, type));
                }
            }
        }
        return tokens;
    }

    static isDataType(lexeme) {
        return ['int', 'float', 'double', 'char', 'boolean', 'String', 'long', 'short', 'byte'].includes(lexeme);
    }

    static isValue(lexeme) {
        if (lexeme.startsWith('"') && lexeme.endsWith('"') && lexeme.length >= 2) return true;
        if (lexeme.startsWith("'") && lexeme.endsWith("'") && lexeme.length >= 2) return true;
        if (/^[0-9]+$/.test(lexeme)) return true;
        if (/^[0-9]+\.[0-9]+$/.test(lexeme)) return true;
        return false;
    }

    static isIdentifier(lexeme) {
        if (!lexeme || lexeme.length === 0) return false;
        return /^[a-zA-Z_][a-zA-Z0-9_]*$/.test(lexeme);
    }

    static isValidLexically(tokens) {
        for (const t of tokens) {
            if (t.getType() === '<unknown>') return false;
        }
        return true;
    }
}

module.exports = LexicalAnalyzer;
