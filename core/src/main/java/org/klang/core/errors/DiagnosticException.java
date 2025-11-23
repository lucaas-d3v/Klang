package org.klang.core.errors;

public final class DiagnosticException extends RuntimeException {
    public final Diagnostic diagnostic;

    public DiagnosticException(Diagnostic diagnostic) {
        super(diagnostic.message);
        this.diagnostic = diagnostic;
    }

    public DiagnosticException(Diagnostic diagnostic, Throwable cause) {
        super(diagnostic.message, cause);
        this.diagnostic = diagnostic;
    }
}
