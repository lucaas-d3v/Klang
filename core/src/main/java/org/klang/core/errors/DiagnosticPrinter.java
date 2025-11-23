package org.klang.core.errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Formats and prints diagnostics with rustic aesthetics.
 * - showLocations: if false, does not print file:line:col nor the context of
 * the
 * file.
 * - useColors: force colors.
 */
public final class DiagnosticPrinter {

    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String WINE = "\033[38;2;127;0;31m";
    // private static final String WINE_LIGHT = "\033[38;2;179;0;45m";
    private static final String GRAY = "\033[38;2;209;209;209m";
    // private static final String GRAY_STEEL = "\033[38;2;46;46;46m";
    private static final String ORANGE = "\033[38;2;255;165;0m";
    private static final String BLUE = "\033[38;2;100;149;237m";

    private final boolean useColors;
    private final boolean showLocations;

    public DiagnosticPrinter(boolean useColors, boolean showLocations) {
        this.useColors = useColors;
        this.showLocations = showLocations;
    }

    public DiagnosticPrinter() {
        this(shouldUseColors(), true);
    }

    private static boolean shouldUseColors() {
        String noColor = System.getenv("NO_COLOR");
        String term = System.getenv("TERM");
        return noColor == null && term != null && !term.equals("dumb");
    }

    public void print(Diagnostic d) {
        String out = render(d);
        System.err.println(out);
    }

    public String render(Diagnostic d) {
        StringBuilder sb = new StringBuilder();
        // header
        String label = labelFor(d.type);
        String labelColor = colorFor(d.type);
        if (useColors) {
            sb.append(BOLD).append(labelColor).append(label).append(RESET).append(": ");
        } else {
            sb.append(label).append(": ");
        }
        sb.append(d.message).append("\n");

        // location line (optional)
        if (showLocations && d.primarySpan != null && d.primarySpan.fileName != null) {
            sb.append("  --> ").append(d.primarySpan.toString()).append("\n");
        } else if (showLocations && d.primarySpan != null && d.primarySpan.fileName == null) {
            // still show basic location (line/col) but without file path
            sb.append("  --> ").append(d.primarySpan.toString()).append("\n");
        }

        // source context (only if fileName present and showLocations true)
        if (showLocations && d.primarySpan != null && d.primarySpan.fileName != null) {
            try {
                appendSourceContext(sb, d.primarySpan);
            } catch (IOException e) {
                // ignore
            }
        }

        // secondary spans as notes (brief)
        for (Span s : d.secondarySpans()) {
            if (showLocations && s.fileName != null) {
                sb.append(" note: referenced at ").append(s.toString()).append("\n");
            } else {
                sb.append(" note: referenced at ").append(s.toString()).append("\n");
            }
        }

        // notes (detailed)
        for (Note n : d.notes()) {
            sb.append("  > note: ").append(n.message).append("\n");
            if (showLocations && n.hasSpan() && n.span.fileName != null) {
                try {
                    sb.append("    --> ").append(n.span.toString()).append("\n");
                    appendSourceContext(sb, n.span);
                } catch (IOException ex) {
                    // ignore
                }
            } else if (showLocations && n.hasSpan()) {
                sb.append("    --> ").append(n.span.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    private void appendSourceContext(StringBuilder sb, Span span) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(span.fileName));
        int idx = span.startLine - 1;
        String lineText = (idx >= 0 && idx < lines.size()) ? lines.get(idx) : "";

        String lineNum = String.valueOf(span.startLine);
        sb.append(" ").append(lineNum).append(" | ").append(lineText).append("\n");

        // pointer
        int pointerPos = Math.max(0, span.startColumn - 1);
        int offset = lineNum.length() + 3 + pointerPos;
        sb.append(" ".repeat(Math.max(0, offset))).append("^".repeat(Math.max(1, span.length()))).append("\n");
    }

    private String labelFor(DiagnosticType t) {
        switch (t) {
            case LEXICAL -> {
                return "LEXICAL";
            }
            case SYNTAX -> {
                return "SYNTAX";
            }
            case SEMANTIC -> {
                return "ERROR";
            }
            case WARNING -> {

                return "WARNING";
            }
            case INFO -> {

                return "INFO";
            }
            default -> {
                return "diagnostic";

            }
        }
    }

    private String colorFor(DiagnosticType t) {
        if (!useColors)
            return "";
        switch (t) {
            case LEXICAL:
            case SYNTAX:
            case SEMANTIC:
                return WINE;
            case WARNING:
                return ORANGE;
            case INFO:
                return BLUE;
            default:
                return GRAY;
        }
    }
}
