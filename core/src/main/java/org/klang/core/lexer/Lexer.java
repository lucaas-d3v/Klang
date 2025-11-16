package org.klang.core.lexer;

import java.util.ArrayList;
import java.util.List;

import org.klang.core.errors.Diagnostic;
import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticType;
import org.klang.core.errors.Note;
import org.klang.core.errors.Span;

public class Lexer {
    List<Token> tokens = new ArrayList<>();
    int position = 0, line = 1, column = 0;
    String source;
    String filePath;

    public List<Token> tokenize() {
        StringBuilder s = new StringBuilder();
        s.setLength(0);

        // Enquanto não for o fim da string
        while (!EOF()) {
            char c = source.charAt(position);
            column++;

            if (Character.isWhitespace(c)) {
                position++;

                if (isNewLine(c)) {
                    line++;
                    column = 0;
                }

                continue;
            }

            if (c == '\"') {
                s.append(c);
                s.append(readString(c));
                s.append(c);
                position++;

                tokens.add(new Token(TokenType.STRING, s.toString()));

                s.setLength(0);
                continue;
            }

            if (c == '\'') {
                s.append(c);
                s.append(readCharacter(c));
                s.append(c);
                position++;

                tokens.add(new Token(TokenType.CHARACTER, s.toString()));

                s.setLength(0);
                continue;
            }

            if (isLetter(c)) {
                s.append(c);

                while (hasNext()) {
                    position++;
                    char demaisChars = source.charAt(position);

                    s.append(demaisChars);
                }

                String value = s.toString();

                switch (value) {
                    case "return", "if", "otherwise", "afterall", "for", "while", "break", "continue", "public",
                            "private", "static", "protected" -> {
                        tokens.add(new Token(TokenType.KEYWORD, value));

                        break;
                    }

                    default -> {
                        tokens.add(new Token(TokenType.IDENTIFIER, value));

                        break;
                    }
                }

                s.setLength(0); // Limpar builder

                position++;
                continue;
            }

            if (isDigit(c)) {

                s.append(c);

                while (hasNext()) {
                    position++;
                    char demais = source.charAt(position);

                    s.append(demais);
                }

                String value = s.toString();

                tokens.add(new Token(TokenType.NUMBER, value));

                s.setLength(0); // Limpar builder
                position++;

                continue;
            }

            switch (c) {
                case '@' -> {
                    tokens.add(new Token(TokenType.AT));
                    break;
                }

                case ',' -> {
                    tokens.add(new Token(TokenType.COMMA));

                    break;
                }

                case '=' -> {
                    if (hasNext()) {
                        tokens.add(new Token(TokenType.DOUBLEEQUAL));
                        position++;

                        break;
                    }

                    tokens.add(new Token(TokenType.ASSIGNMENT));

                    break;
                }

                case '+' -> {
                    if (hasNext()) {
                        tokens.add(new Token(TokenType.INCREMENT));
                        position++;

                        break;
                    }

                    tokens.add(new Token(TokenType.PLUS));

                    break;
                }

                case '-' -> {
                    if (hasNext()) {
                        tokens.add(new Token(TokenType.DECREMENT));
                        position++;

                        break;
                    }

                    tokens.add(new Token(TokenType.MINUS));

                    break;
                }

                case '*' -> {
                    tokens.add(new Token(TokenType.MULTIPLY));

                    break;
                }

                case '/' -> {
                    tokens.add(new Token(TokenType.DIVISION));

                    break;
                }

                case '%' -> {
                    tokens.add(new Token(TokenType.REMAINDER));

                    break;
                }

                case ';' -> {
                    tokens.add(new Token(TokenType.SEMICOLON));

                    break;
                }

                case '(' -> {
                    tokens.add(new Token(TokenType.LPAREN));

                    break;
                }

                case ')' -> {
                    tokens.add(new Token(TokenType.RPAREN));

                    break;
                }

                case '{' -> {
                    tokens.add(new Token(TokenType.LBRACE));

                    break;
                }

                case '}' -> {
                    tokens.add(new Token(TokenType.RBRACE));

                    break;
                }

                case '[' -> {
                    tokens.add(new Token(TokenType.LBRACKET));

                    break;
                }

                case ']' -> {
                    tokens.add(new Token(TokenType.RBRACKET));

                    break;
                }

                case '>' -> {
                    if (hasNext()) {
                        tokens.add(new Token(TokenType.GTE));
                        position++;

                        break;
                    }

                    tokens.add(new Token(TokenType.GT));
                    break;
                }

                case '<' -> {
                    if (hasNext()) {
                        tokens.add(new Token(TokenType.LTE));
                        position++;

                        break;
                    }

                    tokens.add(new Token(TokenType.LT));

                    break;
                }

                case '.' -> {
                    tokens.add(new Token(TokenType.DOT));

                    break;
                }

                case ':' -> {
                    tokens.add(new Token(TokenType.COLON));

                    break;
                }

                case '!' -> {
                    tokens.add(new Token(TokenType.BANG));

                    break;
                }
                /*
                 * --- base ---
                 * 
                 * case '' -> {
                 * tokens.add(new Token(TokenType.));
                 * position++;
                 * 
                 * break;
                 * }
                 */

                default -> {
                    Span span = new Span(filePath, line, column, line, column + 1);
                    Diagnostic d = new Diagnostic(
                            DiagnosticType.LEXICAL,
                            "Caractere inesperado '" + c + "'",
                            span).addNote(new Note("Remova esse caractere ou corrija o token!"));

                    throw new DiagnosticException(d);
                }

            }

            position++;
        }

        return tokens;

    }

    private String readCharacter(char cAtual) {
        StringBuilder s = new StringBuilder();

        position++;
        char c = source.charAt(position);

        while (c != '\'') {
            if (c == '\\') {
                position++;
                if (position >= source.length()) {
                    throw new RuntimeException("String não fechada: fim de arquivo inesperado");
                }
                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                throw new RuntimeException("String não fechada: fim de arquivo inesperado");
            }

            c = source.charAt(position);
        }

        return s.toString();
    }

    private String readString(char cAtual) {
        StringBuilder s = new StringBuilder();

        position++;
        char c = source.charAt(position);

        while (c != '\"') {
            if (c == '\\') {
                position++;
                if (position >= source.length()) {
                    throw new RuntimeException("String não fechada: fim de arquivo inesperado");
                }
                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                throw new RuntimeException("String não fechada: fim de arquivo inesperado");
            }

            c = source.charAt(position);
        }

        return s.toString();
    }

    private boolean isLetter(char c) {
        boolean is = false;
        char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        for (char letter : letters) {
            if (c == letter) {
                is = true;
                break;
            }
        }

        // System.out.println("No is letter");

        return is;
    }

    private boolean isDigit(char c) {
        boolean is = false;
        char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        for (char digit : digits) {
            if (c == digit) {
                is = true;
                break;
            }
        }

        return is;
    }

    private boolean isNewLine(char c) {
        return c == '\n';

    }

    private boolean hasNext() {
        // Verifica se é o ultimo
        if (EOF()) {
            return false;
        }

        char c = source.charAt(position + 1);

        return isLetter(c) || isDigit(c) || isLogical(c);
    }

    private boolean isLogical(char c) {

        return c == '=' || c == '-' || c == '+' || c == '>';
    }

    /*
     * 
     * private boolean isSeparator(char c) {
     * return c == ',';
     * }
     * 
     * 
     * private boolean isDelimiter(char c) {
     * return c == '(' || c == ')' || c == '\'' || c == '\"';
     * }
     */

    private boolean EOF() {
        if (position >= source.length()) {
            return true;
        }

        return false;
    }

    public Lexer(String source, String filePath) {
        this.source = source;
        this.filePath = filePath;
    }

    public void testarTokenize() {
        this.source = "integer x = 10;";

        tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
