class Token {
    constructor(lexeme, type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    getLexeme() { return this.lexeme; }
    getType() { return this.type; }
}

module.exports = Token;
