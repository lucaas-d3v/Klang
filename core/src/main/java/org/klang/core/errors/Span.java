package org.klang.core.errors;

public final class Span {
    public final String fileName; // can be null
    public final int startLine; // 1-based
    public final int startColumn; // 1-based
    public final int endLine;
    public final int endColumn;

    public Span(String fileName, int startLine, int startColumn, int endLine, int endColumn) {
        this.fileName = fileName; // can be null for non-file spans
        if (startLine < 1)
            throw new IllegalArgumentException("startLine must be >= 1");
        if (startColumn < 1)
            throw new IllegalArgumentException("startColumn must be >= 1");
        if (endLine < startLine)
            throw new IllegalArgumentException("endLine >= startLine");
        if (endLine == startLine && endColumn < startColumn)
            throw new IllegalArgumentException("endColumn >= startColumn when same line");

        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public boolean isSingleLine() {
        return startLine == endLine;
    }

    public int length() {
        return isSingleLine() ? endColumn - startColumn + 1 : -1;
    }

    /**
     * toString only reveals file:line:col if fileName != null.
     */
    @Override
    public String toString() {
        if (fileName == null) {
            if (isSingleLine()) {
                return String.format("line %d, col %d", startLine, startColumn);
            }
            return String.format("line %d:%d-%d:%d", startLine, startColumn, endLine, endColumn);
        }
        if (isSingleLine()) {
            return String.format("%s:%d:%d", fileName, startLine, startColumn);
        }
        return String.format("%s:%d:%d-%d:%d", fileName, startLine, startColumn, endLine, endColumn);
    }
}