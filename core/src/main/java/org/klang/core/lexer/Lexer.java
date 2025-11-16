package org.klang.core.lexer;

import java.util.ArrayList;
import java.util.HashMap;
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
    HashMap<String, TokenType> tokensTypeByString = new HashMap<>();
    HashMap<Character, TokenType> tokensTypeByChar = new HashMap<>();

    public List<Token> tokenize() {
        StringBuilder s = new StringBuilder();

        while (!EOF()) {
            s.setLength(0); // Limpar builder
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

                continue;
            }

            if (c == '\'') {
                s.append(c);
                s.append(readCharacter(c));
                s.append(c);
                position++;

                tokens.add(new Token(TokenType.CHARACTER, s.toString()));

                continue;
            }

            if (Character.isLetter(c)) {
                s.append(c);

                while (hasNext()) {
                    position++;
                    char demaisChars = source.charAt(position);

                    s.append(demaisChars);
                }

                String value = s.toString();

                TokenType tokenType = tokensTypeByString.getOrDefault(value, TokenType.IDENTIFIER);
                tokens.add(new Token(tokenType, value));

                position++;
                continue;
            }

            if (Character.isDigit(c)) {

                s.append(c);

                while (hasNext()) {
                    position++;
                    char demais = source.charAt(position);

                    s.append(demais);
                }

                String value = s.toString();

                tokens.add(new Token(TokenType.NUMBER, value));

                position++;

                continue;
            }

            TokenType tokenType = tokensTypeByChar.getOrDefault(c, null);

            if (tokenType == null) {
                Span span = new Span(filePath, line, column, line, column + 1);
                Diagnostic d = new Diagnostic(
                        DiagnosticType.LEXICAL,
                        "Caractere inesperado '" + c + "'",
                        span).addNote(new Note("Remova esse caractere ou corrija o token!"));

                throw new DiagnosticException(d);
            }

            switch (c) {
                case '=' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.DOUBLEEQUAL));
                        position++;

                        break;
                    }
                    tokens.add(new Token(tokenType));

                    break;
                }

                case '+' -> {
                    if (getNext() == '+') {
                        tokens.add(new Token(TokenType.INCREMENT));
                        position++;

                        break;
                    }
                    tokens.add(new Token(tokenType));

                    break;
                }

                case '-' -> {
                    if (getNext() == '-') {
                        tokens.add(new Token(TokenType.DECREMENT));
                        position++;

                        break;
                    }

                    if (getNext() == '>') {
                        tokens.add(new Token(TokenType.ARROW));
                        position++;

                        break;

                    }

                    tokens.add(new Token(tokenType));

                    break;
                }

                case '>' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.GTE));
                        position++;

                        break;
                    }

                    tokens.add(new Token(tokenType));
                    break;
                }

                case '<' -> {
                    if (getNext() == '=') {
                        tokens.add(new Token(TokenType.LTE));
                        position++;

                        break;
                    }
                    tokens.add(new Token(tokenType));

                    break;
                }

            }

            tokens.add(new Token(tokenType));
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
                    Span span = new Span(filePath, line, column, line, column + 1);
                    Diagnostic d = new Diagnostic(
                            DiagnosticType.LEXICAL,
                            "String não fechada: fim de arquivo inesperado",
                            span).addNote(new Note("Esperado \'"));

                    throw new DiagnosticException(d);
                }
                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                Span span = new Span(filePath, line, column, line, column + 1);
                Diagnostic d = new Diagnostic(
                        DiagnosticType.LEXICAL,
                        "String não fechada: fim de arquivo inesperado",
                        span).addNote(new Note("Esperado \'"));

                throw new DiagnosticException(d);
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
                    Span span = new Span(filePath, line, column, line, column + 1);
                    Diagnostic d = new Diagnostic(
                            DiagnosticType.LEXICAL,
                            "String não fechada: fim de arquivo inesperado",
                            span).addNote(new Note("Esperado \""));

                    throw new DiagnosticException(d);
                }
                char escaped = source.charAt(position);
                s.append('\\').append(escaped);
            } else {
                s.append(c);
            }

            position++;

            if (position >= source.length()) {
                Span span = new Span(filePath, line, column, line, column + 1);
                Diagnostic d = new Diagnostic(
                        DiagnosticType.LEXICAL,
                        "String não fechada: fim de arquivo inesperado",
                        span).addNote(new Note("Esperado \""));

                throw new DiagnosticException(d);
            }

            c = source.charAt(position);
        }

        return s.toString();
    }

    private boolean isNewLine(char c) {
        return c == '\n';

    }

    private boolean hasNext() {
        if (EOF()) {
            return false;
        }

        char c = source.charAt(position + 1);
        return Character.isLetter(c) || Character.isDigit(c) || isLogicalChar(c);
    }

    private char getNext() {
        if (EOF()) {
            return '\0';
        }

        return source.charAt(position + 1);
    }

    private boolean isLogicalChar(char c) {
        return c == '=' || c == '-' || c == '+' || c == '>';
    }

    private boolean EOF() {
        return position >= source.length();
    }

    private void initialzerhashMapTokensTypes() {
        tokensTypeByString.put("return", TokenType.KEYWORD);
        tokensTypeByString.put("if", TokenType.KEYWORD);
        tokensTypeByString.put("otherwise", TokenType.KEYWORD);
        tokensTypeByString.put("afterall", TokenType.KEYWORD);
        tokensTypeByString.put("for", TokenType.KEYWORD);
        tokensTypeByString.put("while", TokenType.KEYWORD);
        tokensTypeByString.put("break", TokenType.KEYWORD);
        tokensTypeByString.put("continue", TokenType.KEYWORD);
        tokensTypeByString.put("public", TokenType.KEYWORD);
        tokensTypeByString.put("private", TokenType.KEYWORD);
        tokensTypeByString.put("static", TokenType.KEYWORD);
        tokensTypeByString.put("protected", TokenType.KEYWORD);
        tokensTypeByChar.put('@', TokenType.AT);
        tokensTypeByChar.put(',', TokenType.COMMA);
        tokensTypeByChar.put('+', TokenType.PLUS);
        tokensTypeByChar.put('=', TokenType.ASSIGNMENT);
        tokensTypeByChar.put('*', TokenType.MULTIPLY);
        tokensTypeByChar.put('/', TokenType.DIVISION);
        tokensTypeByChar.put('-', TokenType.MINUS);
        tokensTypeByChar.put('%', TokenType.REMAINDER);
        tokensTypeByChar.put(';', TokenType.SEMICOLON);
        tokensTypeByChar.put('(', TokenType.LPAREN);
        tokensTypeByChar.put(')', TokenType.RPAREN);
        tokensTypeByChar.put('{', TokenType.LBRACE);
        tokensTypeByChar.put('}', TokenType.RBRACE);
        tokensTypeByChar.put('>', TokenType.GT);
        tokensTypeByChar.put('<', TokenType.LT);
        tokensTypeByChar.put('.', TokenType.DOT);
        tokensTypeByChar.put(':', TokenType.COLON);
        tokensTypeByChar.put('!', TokenType.BANG);
        tokensTypeByChar.put('[', TokenType.LBRACKET);
        tokensTypeByChar.put(']', TokenType.RBRACKET);

    }

    public Lexer(String source, String filePath) {
        this.source = source;
        this.filePath = filePath;

        initialzerhashMapTokensTypes();
    }

    public void testTokenize() {
        this.source = "integer x = 10;";

        tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
