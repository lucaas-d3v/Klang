package org.klang.core.errors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ErrorReporter {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private static final String WINE = "\u001B[38;2;127;0;31m";
    private static final String WINE_LIGHT = "\u001B[38;2;179;0;45m";
    private static final String GRAY = "\u001B[38;2;180;180;180m";
    private static final String GRAY_DARK = "\u001B[38;2;100;100;100m";

    private ErrorReporter() {
    }

    public static void report(Diagnostic d) {
        try {
            printDiagnostic(d);
        } catch (Exception e) {
            // simple fallback
            System.err.println("error: " + d.message);
        }
    }

    private static void printDiagnostic(Diagnostic d) throws IOException {
        Span span = d.primarySpan;

        // Stylish header
        System.err.println();
        System.err.println(WINE + BOLD + "Error" + RESET + GRAY + ": " + d.message + RESET);

        // Location
        if (span != null) {
            System.err.println("  " + GRAY + "at " + span.toString() + RESET);

            try {
                printSourceContext(span);
            } catch (Exception ignored) {
            }
        }

        // Notes
        for (Note n : d.notes()) {
            System.err.println();
            System.err.println("  " + GRAY_DARK + "note: " + RESET + n.message);

            if (n.span != null) {
                System.err.println("    " + GRAY + n.span.toString() + RESET);
                try {
                    printSourceContext(n.span);
                } catch (Exception ignored) {
                }
            }
        }

        System.err.println();
    }

    private static void printSourceContext(Span span) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(span.fileName));
        int lineIndex = span.startLine - 1;

        String code = (lineIndex >= 0 && lineIndex < lines.size())
                ? lines.get(lineIndex)
                : "";

        String num = String.valueOf(span.startLine);
        String prefix = GRAY_DARK + "  " + num + " | " + RESET;

        // Line of code
        System.err.println();
        System.err.println(prefix + code);

        // Pointer
        int col = Math.max(0, span.startColumn - 1);
        String pointer = " ".repeat(num.length() + 3 + col) + WINE_LIGHT + "^" + RESET;
        System.err.println(pointer);
    }
}
