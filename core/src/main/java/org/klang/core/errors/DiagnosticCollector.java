package org.klang.core.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiagnosticCollector {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void report(Diagnostic d) {
        diagnostics.add(d);
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.type == DiagnosticType.LEXICAL
                || d.type == DiagnosticType.SYNTAX
                || d.type == DiagnosticType.SEMANTIC
                || d.type == DiagnosticType.TYPE
                || d.type == DiagnosticType.ERROR);
    }

    public List<Diagnostic> all() {
        return Collections.unmodifiableList(diagnostics);
    }

    /**
     * Throws DiagnosticException on the first error. The CLI can print all.
     */
    public void throwIfErrors() {
        if (hasErrors()) {
            throw new DiagnosticException(diagnostics.get(0));
        }
    }
}
