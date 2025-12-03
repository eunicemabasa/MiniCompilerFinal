package com;

import com.analyzer.LexicalAnalyzer;
import com.analyzer.SemanticAnalyzer;
import com.analyzer.SyntaxAnalyzer;
import com.model.Token;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class SwingCompilerUI {

    private static ArrayList<Token> tokens;

    private static final JTextArea codeArea = new JTextArea();
    private static final JTextArea resultArea = new JTextArea();

    private static final JButton lexicalBtn =
            createCurvyButton("Lexical<br>Analysis", new Color(220, 20, 60));

    private static final JButton syntaxBtn =
            createCurvyButton("Syntax<br>Analysis", new Color(255, 215, 0));

    private static final JButton semanticBtn =
            createCurvyButton("Semantic<br>Analysis", new Color(0, 230, 118));

    public static void main(String[] args) {
        // GUI Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(SwingCompilerUI::createGUI);
    }

    private static Border createGlowBorder(String title) {
        Color glowColor = new Color(100, 200, 255);
        Border line = BorderFactory.createLineBorder(glowColor, 2);
        Border titleBorder = BorderFactory.createTitledBorder(
                line, title, 0, 0, new Font("Arial", Font.BOLD, 14), glowColor
        );
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titleBorder
        );
    }

    private static JButton createCurvyButton(String text, Color bg) {
        JButton btn = new JButton("<html><center><b>" + text + "</b></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.BLACK);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 70));

        return btn;
    }

    private static void createGUI() {

        JFrame f = new JFrame("MiniCompiler - Dark Theme");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1200, 750);

        JPanel buttons = new JPanel(new GridLayout(5, 1, 12, 18));
        buttons.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buttons.setBackground(Color.BLACK);

        JButton openBtn = createCurvyButton("Open<br>File", new Color(32, 190, 180));
        JButton clearBtn = createCurvyButton("Clear", new Color(237, 42, 133));

        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);

        buttons.add(openBtn);
        buttons.add(lexicalBtn);
        buttons.add(syntaxBtn);
        buttons.add(semanticBtn);
        buttons.add(clearBtn);

        // Output Area
        resultArea.setEditable(false);
        resultArea.setForeground(Color.CYAN);
        resultArea.setBackground(Color.BLACK);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        resultArea.setBorder(createGlowBorder(" Result Output "));

        // Input Area
        codeArea.setForeground(Color.WHITE);
        codeArea.setBackground(Color.BLACK);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        codeArea.setBorder(createGlowBorder(" Source Code "));
        codeArea.setCaretColor(Color.WHITE);

        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(resultArea),
                new JScrollPane(codeArea)
        );
        rightSplit.setDividerLocation(320);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttons, rightSplit);
        mainSplit.setDividerLocation(240);

        f.add(mainSplit);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // BUTTON ACTIONS
        openBtn.addActionListener(e -> openFile(f));
        clearBtn.addActionListener(e -> {
            codeArea.setText("");
            resultArea.setText("");
            tokens = null;
            resetButtons();
        });

        lexicalBtn.addActionListener(e -> runLexical());
        syntaxBtn.addActionListener(e -> runSyntax());
        semanticBtn.addActionListener(e -> runSemantic());

        codeArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            private void update() {
                lexicalBtn.setEnabled(!codeArea.getText().trim().isEmpty());
            }
        });
    }

    private static void openFile(JFrame frame) {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                codeArea.setText(Files.readString(fc.getSelectedFile().toPath()));
                resultArea.setText("File opened: " + fc.getSelectedFile().getName() + "\n\n");
                resetButtons();
            } catch (Exception ex) {
                resultArea.append("ERROR: " + ex.getMessage() + "\n");
            }
        }
    }

    private static void runLexical() {
        resultArea.append("\n=== Running Lexical Analysis ===\n\n");

        try {
            tokens = LexicalAnalyzer.tokenize(codeArea.getText());

            if (!LexicalAnalyzer.isValidLexically(tokens)) {
                resultArea.append("❌ Lexical analysis FAILED!\n");
                resultArea.append("Unknown tokens found in the code.\n\n");
                return;
            }

            resultArea.append("✓ Lexical Analysis Completed.\n");
            resultArea.append("Tokens generated: " + tokens.size() + "\n\n");
            syntaxBtn.setEnabled(true);

        } catch (Exception ex) {
            resultArea.append("Lexical Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void runSyntax() {
        resultArea.append("\n=== Running Syntax Analysis ===\n\n");

        try {
            if (!SyntaxAnalyzer.analyze(tokens)) {
                resultArea.append("❌ Syntax analysis FAILED!\n");
                resultArea.append("Invalid syntax structure detected.\n\n");
                return;
            }

            resultArea.append("✓ Syntax Analysis Completed.\n\n");
            semanticBtn.setEnabled(true);

        } catch (Exception ex) {
            resultArea.append("Syntax Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void runSemantic() {
        resultArea.append("\n=== Running Semantic Analysis ===\n\n");

        try {
            if (!SemanticAnalyzer.analyze(tokens)) {
                resultArea.append("❌ Semantic analysis FAILED!\n");
                resultArea.append("Type mismatch or duplicate variable detected.\n\n");
                return;
            }

            resultArea.append("✓ Semantic Analysis Completed.\n\n");
            resultArea.append("🎉 ALL ANALYSES PASSED! COMPILATION SUCCESSFUL! 🎉\n\n");

        } catch (Exception ex) {
            resultArea.append("Semantic Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void resetButtons() {
        lexicalBtn.setEnabled(!codeArea.getText().trim().isEmpty());
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);
    }
}