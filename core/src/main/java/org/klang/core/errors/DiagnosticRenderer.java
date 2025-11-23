package org.klang.core.errors;

public final class DiagnosticRenderer {
    private DiagnosticRenderer() {
    }

    public static void printTerminal(Diagnostic d) {
        new DiagnosticPrinter(true, true).print(d);
    }

    /** Print without file context (message only, spans without file) */
    public static void printSummary(Diagnostic d) {
        new DiagnosticPrinter(true, false).print(d);
    }
}
