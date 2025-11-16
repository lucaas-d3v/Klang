package org.klang.core.lexer;

public enum TokenType {

    // @
    AT,

    IDENTIFIER, NUMBER, STRING, CHARACTER, ASSIGNMENT, RETURN, KEYWORD,

    // Operações
    PLUS, INCREMENT, MINUS, DECREMENT, MULTIPLY, DIVISION, POWER, REMAINDER,

    // Delimitadores {} -> breaces [] -> brackets
    LPAREN, RPAREN, COMMA, LBRACE, RBRACE, LBRACKET, RBRACKET, SEMICOLON, COLON, DOT,

    BANG, LT, GT, LTE, GTE, DOUBLEEQUAL, NOTEQUAL, ARROW,
}

/*
 * LPAREN (
 * RPAREN )
 * LBRACE {
 * RBRACE }
 * LBRACKET [
 * RBRACKET ]
 * COMMA ,
 * SEMICOLON ;
 * COLON :
 * DOT .
 * PLUS +
 * MINUS -
 * STAR *
 * SLASH /
 * PERCENT %
 * EQUAL =
 * BANG !
 * LT <
 * GT >
 * LTE <=
 * GTE >=
 * DOUBLEEQUAL ==
 * NOTEQUAL !=
 * ARROW ->
 * 
 */
