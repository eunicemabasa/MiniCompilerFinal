const fs = require('fs');
const readline = require('readline');
const LexicalAnalyzer = require('./LexicalAnalyzer');
const SyntaxAnalyzer = require('./SyntaxAnalyzer');
const SemanticAnalyzer = require('./SemanticAnalyzer');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function promptForFile() {
    rl.question('Enter the path file containing Java variable declarations: ', (filePath) => {
        try {
            const content = fs.readFileSync(filePath, 'utf8');
            console.log('File contents:');
            console.log(content);
            console.log();

            const tokens = LexicalAnalyzer.tokenize(content);

            if (!LexicalAnalyzer.isValidLexically(tokens)) {
                console.log('Lexical analysis phase FAILED! Try again, pls :)');
                promptForFile(); // Loop back
                return;
            }
            console.log('Lexical analysis phase PASSED :D');

            if (!SyntaxAnalyzer.analyze(tokens)) {
                console.log('SYNTAX ERROR! try again :)');
                promptForFile(); // Loop back
                return;
            }
            console.log('Syntax Analysis PASSED! :D');

            if (!SemanticAnalyzer.analyze(tokens)) {
                console.log('Semantic Analysis FAILED! Try again :)');
                promptForFile(); // Loop back
                return;
            }
            console.log('Semantic Analysis PASSED! :D');
            console.log('All analyses passed! Compilation successful.');
            rl.close();

        } catch (e) {
            console.log('Error reading file. Please check the path and try again.');
            promptForFile(); // Loop back
        }
    });
}

promptForFile();
