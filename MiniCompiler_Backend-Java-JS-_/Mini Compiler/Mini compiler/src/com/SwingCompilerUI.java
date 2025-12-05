package com;

import com.analyzer.LexicalAnalyzer;
import com.analyzer.SemanticAnalyzer;
import com.analyzer.SyntaxAnalyzer;
import com.model.Token;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class SwingCompilerUI {

    private static ArrayList<Token> tokens;
    private static final JTextArea codeArea = new JTextArea();
    private static final JTextArea resultArea = new JTextArea();
    private static final JTextArea lineNumbers = new JTextArea("1");

    // Theme state
    private static boolean isDarkTheme = true;
    private static JFrame frame;
    // UI components for theme updates
    private static JPanel header, buttonBar, labelPanel;
    private static JLabel titleLabel, themeToggle;
    private static JSeparator divider;
    private static JPanel lnPanel; // container for lineNumbers to manage padding/bg

    private static final Color GOLD = new Color(255, 225, 0);

    // Local references that need to be accessible to handlers
    private static CurvyButton openBtn;
    private static CurvyButton clearBtn;

    // Custom Curvy Button
    private static class CurvyButton extends JButton {
        private final Color baseColor;
        private boolean completed = false;

        public CurvyButton(String text, Color baseColor) {
            this.baseColor = baseColor;
            setLayout(new BorderLayout());
            JLabel label = new JLabel(
                    "<html><center><b><font color='black'>" + text + "</font></b></center></html>",
                    SwingConstants.CENTER
            );
            label.setFont(new Font("Segoe UI", Font.BOLD, 15));
            add(label, BorderLayout.CENTER);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 70));
        }

        public void setCompleted(boolean c) {
            completed = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(completed ? baseColor.darker().darker().darker() : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {}
    }

    private static final CurvyButton lexicalBtn = new CurvyButton("Lexical<br>Analysis", GOLD);
    private static final CurvyButton syntaxBtn  = new CurvyButton("Syntax<br>Analysis",  GOLD);
    private static final CurvyButton semanticBtn = new CurvyButton("Semantic<br>Analysis", GOLD);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(SwingCompilerUI::createGUI);
    }

    /**
     * Creates a compound border that consists of an outer solid yellow line and inner yellow line,
     * with padding between the inner line and the contents. We chose medium thickness (2px outer + 2px inner).
     */
    private static Border createDoubleYellowBorderWithPadding(int outer, int inner, int padding) {
        return new CompoundBorder(
                new LineBorder(GOLD, outer),
                new CompoundBorder(
                        new LineBorder(GOLD, inner),
                        new EmptyBorder(padding, padding, padding, padding)
                )
        );
    }

    private static void applyTheme() {
        if (frame == null) return;

        Color bg = isDarkTheme ? Color.BLACK : new Color(252, 252, 252);
        Color fg = isDarkTheme ? Color.WHITE : Color.BLACK;
        Color resultFg = isDarkTheme ? Color.CYAN : new Color(0, 120, 215);
        Color headerBg = isDarkTheme ? Color.BLACK : Color.WHITE;
        Color buttonBarBg = isDarkTheme ? Color.BLACK : new Color(240, 245, 250);

        codeArea.setBackground(bg);
        codeArea.setForeground(fg);
        codeArea.setCaretColor(fg);

        resultArea.setBackground(bg);
        resultArea.setForeground(resultFg);

        // Line numbers theme
        lineNumbers.setBackground(isDarkTheme ? new Color(40, 40, 40) : new Color(230, 230, 230));
        lineNumbers.setForeground(isDarkTheme ? new Color(150, 150, 150) : new Color(80, 80, 80));

        if (header != null) header.setBackground(headerBg);
        if (buttonBar != null) buttonBar.setBackground(buttonBarBg);
        if (labelPanel != null) labelPanel.setBackground(buttonBarBg);
        if (lnPanel != null) lnPanel.setBackground(isDarkTheme ? new Color(40, 40, 40) : new Color(230, 230, 230));

        if (titleLabel != null) titleLabel.setForeground(GOLD);
        if (divider != null) divider.setForeground(GOLD);
        if (themeToggle != null) {
            themeToggle.setText(isDarkTheme ? "Light" : "Dark");
            themeToggle.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
        }

        frame.getContentPane().setBackground(isDarkTheme ? new Color(30, 30, 30) : Color.WHITE);

        // Ensure the scrollpane viewports keep matching background
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private static void createGUI() {
        frame = new JFrame("Thaeu Compiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1450, 850);
        frame.setMinimumSize(new Dimension(1000, 600));

        // === HEADER ===
        header = new JPanel(new BorderLayout());
        header.setBackground(Color.BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        titleLabel = new JLabel("Thaeu Compiler");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(GOLD);
        header.add(titleLabel, BorderLayout.WEST);

        themeToggle = new JLabel("Light");
        themeToggle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        themeToggle.setForeground(Color.WHITE);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                isDarkTheme = !isDarkTheme;
                applyTheme();
            }
        });
        header.add(themeToggle, BorderLayout.EAST);

        // === DIVIDER ===
        divider = new JSeparator();
        divider.setForeground(GOLD);

        // === BUTTON BAR ===
        buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        buttonBar.setBackground(Color.BLACK);

        // assign to static fields (no shadowing)
        openBtn  = new CurvyButton("Open<br>File", GOLD);
        clearBtn = new CurvyButton("Clear",      new Color(255, 0, 0));

        lexicalBtn.setEnabled(false);
        syntaxBtn.setEnabled(false);
        semanticBtn.setEnabled(false);

        buttonBar.add(openBtn);
        buttonBar.add(lexicalBtn);
        buttonBar.add(syntaxBtn);
        buttonBar.add(semanticBtn);
        buttonBar.add(clearBtn);

        // === LABEL PANEL ===
        labelPanel = new JPanel(new GridLayout(1, 2));
        labelPanel.setBackground(Color.BLACK);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel sourceLabel = new JLabel("Source Code", SwingConstants.CENTER);
        sourceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sourceLabel.setForeground(GOLD);

        JLabel resultLabel = new JLabel("Result Output", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultLabel.setForeground(GOLD);

        labelPanel.add(sourceLabel);
        labelPanel.add(resultLabel);

        // === LINE NUMBERS SETUP ===
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("Courier", Font.PLAIN, 15));
        lineNumbers.setText("1");
        lineNumbers.setBorder(null);

        // Set code area font and margins (affects where text starts)
        codeArea.setFont(new Font("Courier", Font.PLAIN, 15));
        // internal margin inside the text area - we'll match lnPanel top padding to this
        final Insets codeMarginInsets = new Insets(10, 10, 10, 10);
        codeArea.setMargin(codeMarginInsets);

        // update line numbers when code changes
        codeArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(DocumentEvent e) {}
            private void updateLineNumbers() {
                StringBuilder sb = new StringBuilder();
                int lines = codeArea.getLineCount();
                for (int i = 1; i <= lines; i++) sb.append(i).append('\n');
                lineNumbers.setText(sb.toString());
            }
        });

        // === BUILD CODE PANEL (LEFT) WITH DOUBLE BORDER (ONLY AROUND CODE) ===
        // inner area that will host the codeArea (no border on the inner JScrollPane)
        JPanel codeInner = new JPanel(new BorderLayout());
        codeInner.setOpaque(true);
        codeInner.setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        JScrollPane codeAreaScroll = new JScrollPane(codeArea);
        codeAreaScroll.setBorder(null);
        // ensure viewport bg matches theme
        codeAreaScroll.getViewport().setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        codeInner.add(codeAreaScroll, BorderLayout.CENTER);

        // Create codeOuter which WILL have the double yellow border (2px outer + 2px inner) and internal padding
        JPanel codeOuter = new JPanel(new BorderLayout());
        codeOuter.setBorder(createDoubleYellowBorderWithPadding(2, 2, 12)); // medium thickness per your choice
        codeOuter.setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        codeOuter.add(codeInner, BorderLayout.CENTER);

        // Now create lnPanel and set top padding to align numbers with codeArea's top margin
        lnPanel = new JPanel(new BorderLayout());
        lnPanel.setOpaque(true);
        lnPanel.setBackground(isDarkTheme ? new Color(40, 40, 40) : new Color(230, 230, 230));
        // Match top padding so numbers align with codeArea text (inner padding 12 + code margin top 10 -> offset)
        lnPanel.setBorder(new EmptyBorder((12 + codeMarginInsets.top - 4), 6, 0, 6));
        lnPanel.add(lineNumbers, BorderLayout.NORTH);

        // Put everything in a JScrollPane for the code area so rowHeaderView can display lnPanel
        // This scrollpane wraps the codeOuter (which already has the double border). We must ensure this wrapper shows no extra white border.
        JScrollPane codeScrollWithRowHeader = new JScrollPane();
        codeScrollWithRowHeader.setViewportView(codeOuter);
        codeScrollWithRowHeader.setRowHeaderView(lnPanel);
        // Force the outer scrollpane itself to have no visible default border (we rely on codeOuter border)
        codeScrollWithRowHeader.setBorder(null);
        // Ensure viewport background matches
        codeScrollWithRowHeader.getViewport().setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));

        // IMPORTANT: make sure scrollbars don't introduce white edges on some LAFs
        codeScrollWithRowHeader.getVerticalScrollBar().setBackground(isDarkTheme ? Color.BLACK : Color.WHITE);
        codeScrollWithRowHeader.getHorizontalScrollBar().setBackground(isDarkTheme ? Color.BLACK : Color.WHITE);

        // === BUILD RESULT PANEL (RIGHT) WITH DOUBLE BORDER (ONLY AROUND RESULT) ===
        JPanel resultInner = new JPanel(new BorderLayout());
        resultInner.setOpaque(true);
        resultInner.setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        JScrollPane resultAreaScroll = new JScrollPane(resultArea);
        resultAreaScroll.setBorder(null);
        resultAreaScroll.getViewport().setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        resultInner.add(resultAreaScroll, BorderLayout.CENTER);

        JPanel resultOuter = new JPanel(new BorderLayout());
        resultOuter.setBorder(createDoubleYellowBorderWithPadding(2, 2, 12));
        resultOuter.setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        resultOuter.add(resultInner, BorderLayout.CENTER);

        JScrollPane resultScrollWithOuter = new JScrollPane();
        resultScrollWithOuter.setViewportView(resultOuter);
        resultScrollWithOuter.setBorder(null);
        resultScrollWithOuter.getViewport().setBackground(isDarkTheme ? Color.BLACK : new Color(252, 252, 252));
        resultScrollWithOuter.getVerticalScrollBar().setBackground(isDarkTheme ? Color.BLACK : Color.WHITE);
        resultScrollWithOuter.getHorizontalScrollBar().setBackground(isDarkTheme ? Color.BLACK : Color.WHITE);

        // === SPLIT PANE ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                codeScrollWithRowHeader, resultScrollWithOuter);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        // === FINAL LAYOUT ===
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(divider, BorderLayout.CENTER);
        topSection.add(buttonBar, BorderLayout.SOUTH);

        JPanel middleSection = new JPanel(new BorderLayout());
        middleSection.add(topSection, BorderLayout.NORTH);
        middleSection.add(labelPanel, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(middleSection, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);

        // Apply theme only AFTER everything is constructed
        applyTheme();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // === ACTIONS ===
        openBtn.addActionListener(e -> openFile(frame));
        clearBtn.addActionListener(e -> {
            codeArea.setText("");
            resultArea.setText("");
            tokens = null;
            resetButtons();
            lexicalBtn.setCompleted(false);
            syntaxBtn.setCompleted(false);
            semanticBtn.setCompleted(false);
            // reset line numbers
            lineNumbers.setText("1");
        });

        lexicalBtn.addActionListener(e -> runLexical());
        syntaxBtn.addActionListener(e -> runSyntax());
        semanticBtn.addActionListener(e -> runSemantic());

        // update lexical button enablement on typing
        codeArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateEnable(); }
            public void removeUpdate(DocumentEvent e) { updateEnable(); }
            public void changedUpdate(DocumentEvent e) {}
            private void updateEnable() {
                lexicalBtn.setEnabled(!codeArea.getText().trim().isEmpty());
            }
        });
    }

    private static void openFile(JFrame frame) {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                codeArea.setText(Files.readString(fc.getSelectedFile().toPath()));
                codeArea.setEditable(true);
                resultArea.setText("File opened: " + fc.getSelectedFile().getName() + "\n\n");
                resetButtons();
                lexicalBtn.setCompleted(false);
                syntaxBtn.setCompleted(false);
                semanticBtn.setCompleted(false);
                // update line numbers
                SwingUtilities.invokeLater(() -> {
                    StringBuilder sb = new StringBuilder();
                    int lines = codeArea.getLineCount();
                    for (int i = 1; i <= lines; i++) sb.append(i).append('\n');
                    lineNumbers.setText(sb.length() > 0 ? sb.toString() : "1");
                });
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
                resultArea.append("Lexical analysis FAILED!\nUnknown tokens found.\n\n");
                lexicalBtn.setCompleted(false);
                return;
            }
            resultArea.append("Lexical Analysis Completed.\nTokens: " + tokens.size() + "\n\n");
            syntaxBtn.setEnabled(true);
            lexicalBtn.setCompleted(true);
        } catch (Exception ex) {
            resultArea.append("Lexical Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void runSyntax() {
        resultArea.append("\n=== Running Syntax Analysis ===\n\n");
        try {
            if (!SyntaxAnalyzer.analyze(tokens)) {
                resultArea.append("Syntax analysis FAILED!\nInvalid syntax.\n\n");
                syntaxBtn.setCompleted(false);
                return;
            }
            resultArea.append("Syntax Analysis Completed.\n\n");
            semanticBtn.setEnabled(true);
            syntaxBtn.setCompleted(true);
        } catch (Exception ex) {
            resultArea.append("Syntax Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    private static void runSemantic() {
        resultArea.append("\n=== Running Semantic Analysis ===\n\n");
        try {
            if (!SemanticAnalyzer.analyze(tokens)) {
                resultArea.append("Semantic analysis FAILED!\nType mismatch or duplicate var.\n\n");
                semanticBtn.setCompleted(false);
                return;
            }
            resultArea.append("Semantic Analysis Completed.\n\n");
            resultArea.append("ALL ANALYSES PASSED! COMPILATION SUCCESSFUL!\n\n");
            semanticBtn.setCompleted(true);
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
