package CompilerTPL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;d
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniCompiler {

    private BufferedReader fileReader;
    private JTextArea codeTextArea;
    private JTextArea resultTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniCompiler().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Mini Compiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        codeTextArea = new JTextArea();
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);

        JButton openFileButton = new JButton("Open File");
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        JButton lexicalAnalysisButton = new JButton("Lexical Analysis");
        lexicalAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLexicalAnalysis();
            }
        });

        JButton syntaxAnalysisButton = new JButton("Syntax Analysis");
        syntaxAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSyntaxAnalysis();
            }
        });

        JButton semanticAnalysisButton = new JButton("Semantic Analysis");
        semanticAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSemanticAnalysis();
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCode();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(openFileButton);
        buttonPanel.add(lexicalAnalysisButton);
        buttonPanel.add(syntaxAnalysisButton);
        buttonPanel.add(semanticAnalysisButton);
        buttonPanel.add(clearButton);

        frame.setLayout(new BorderLayout());
        frame.add(codeScrollPane, BorderLayout.CENTER);
        frame.add(resultScrollPane, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Close the previous file reader if it exists
            closeFileReader();
            displayFileContents(selectedFile);
        }
    }

    private void displayFileContents(File file) {
        JFrame fileFrame = new JFrame("File Contents");
        JTextArea fileContentsTextArea = new JTextArea();

        try {
            // Use the fileReader to read the file
            fileReader = new BufferedReader(new FileReader(file));
            StringBuilder fileContents = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
            fileContentsTextArea.setText(fileContents.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the file reader if there was an exception
            closeFileReader();
        }

        JScrollPane fileContentsScrollPane = new JScrollPane(fileContentsTextArea);

        fileFrame.setLayout(new BorderLayout());
        fileFrame.add(fileContentsScrollPane, BorderLayout.CENTER);
        fileFrame.setSize(400, 300);
        fileFrame.setVisible(true);
    }

    private void closeFileReader() {
        if (fileReader != null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileReader = null; // Set it to null to indicate that it's closed
        }
    }

    private void performLexicalAnalysis() {
        String sourceCode = codeTextArea.getText();
        String result = analyzeLexical(sourceCode);
        resultTextArea.setText(result);
    }

    private String analyzeLexical(String sourceCode) {
        // Define regular expressions for identifiers and keywords
        String identifierRegex = "[a-zA-Z_][a-zA-Z0-9_]*";
        String keywordRegex = "\\b(?:int|float|if|else|while|for|return)\\b";

        // Combine the regex patterns
        String combinedRegex = identifierRegex + "|" + keywordRegex;

        Pattern pattern = Pattern.compile(combinedRegex);
        Matcher matcher = pattern.matcher(sourceCode);

        // Prepare the result message
        StringBuilder result = new StringBuilder("Lexical Analysis Result: ");

        // Check for matches iteratively using find()
        boolean lexicalAnalysisPassed = true;
        while (matcher.find()) {
            lexicalAnalysisPassed = true;
            // Print the matched token for diagnostic purposes
            String matchedToken = matcher.group();
            System.out.println("Matched Token: " + matchedToken);
        }

        if (lexicalAnalysisPassed) {
            result.append("Passed");
        } else {
            result.append("Failed" );
        }

        return result.toString();
    }

    private void performSyntaxAnalysis() {
        String sourceCode = codeTextArea.getText();
        String result = analyzeSyntax(sourceCode);
        resultTextArea.setText(result);
    }

    private String analyzeSyntax(String sourceCode) {
        int openBraceCount = 0;
        int closeBraceCount = 0;

        // Loop through each character in the source code
        for (char character : sourceCode.toCharArray()) {
            if (character == '{') {
                openBraceCount++;
            } else if (character == '}') {
                closeBraceCount++;
            }
        }

        // Check if the number of open and close braces are equal
        boolean syntaxAnalysisPassed = openBraceCount == closeBraceCount;

        // Prepare the result message
        StringBuilder result = new StringBuilder("Syntax Analysis Result: ");

        if (syntaxAnalysisPassed) {
            result.append("Passed");
        } else {
            result.append("Failed - Unbalanced braces");
        }

        return result.toString();
    }

    private void performSemanticAnalysis() {
        String sourceCode = codeTextArea.getText();
        String result = analyzeSemantic(sourceCode);
        resultTextArea.setText(result);
    }

    private String analyzeSemantic(String sourceCode) {
        // Example: Check if a variable is declared before its usage
        String[] lines = sourceCode.split("\n");
        boolean semanticAnalysisPassed = true;

        for (String line : lines) {
            // Split each line into words
            String[] words = line.split("\\s+");

            for (int i = 0; i < words.length; i++) {
                // Check for variable usage
                if (words[i].matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    String variableName = words[i];

                    // Check if the variable is declared
                    boolean variableDeclared = false;
                    for (int j = 0; j < i; j++) {
                        if (words[j].equals("int") || words[j].equals("float")) {
                            variableDeclared = true;
                            break;
                        }
                    }

                    if (!variableDeclared) {
                        resultTextArea.setText("Semantic Analysis Result: Failed - Variable " + variableName + " used before declaration");
                        return resultTextArea.getText();
                    }
                }
            }
        }

        // If the loop completes without returning, semantic analysis passed
        return "Semantic Analysis Result: Passed";
    }

    private void clearCode() {
        codeTextArea.setText("");
        resultTextArea.setText("");

        // Close the file reader when clearing the code
        closeFileReader();
    }
}
