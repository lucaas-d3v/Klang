package org.klang.core.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a diagnosis (message + spans + notes).
 * Designed to be built fluidly.
 */
public final class Diagnostic {
    public final DiagnosticType type;
    public final String message;
    public final Span primarySpan; // can be null
    private final List<Span> secondarySpans;
    private final List<Note> notes;

    public Diagnostic(DiagnosticType type, String message, Span primarySpan,
            List<Span> secondarySpans, List<Note> notes) {
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
        this.primarySpan = primarySpan;
        this.secondarySpans = secondarySpans == null ? List.of() : List.copyOf(secondarySpans);
        this.notes = notes == null ? List.of() : List.copyOf(notes);
    }

    // Builder helpers
    public static Builder builder(DiagnosticType type, String message) {
        return new Builder(type, message);
    }

    public List<Span> secondarySpans() {
        return Collections.unmodifiableList(secondarySpans);
    }

    public List<Note> notes() {
        return Collections.unmodifiableList(notes);
    }

    public boolean hasPrimarySpan() {
        return primarySpan != null;
    }

    public static final class Builder {
        private final DiagnosticType type;
        private final String message;
        private Span primary;
        private final List<Span> secondaries = new ArrayList<>();
        private final List<Note> notes = new ArrayList<>();

        public Builder(DiagnosticType type, String message) {
            this.type = type;
            this.message = message;
        }

        public Builder primary(Span s) {
            this.primary = s;
            return this;
        }

        public Builder addSecondary(Span s) {
            if (s != null)
                secondaries.add(s);
            return this;
        }

        public Builder addNote(Note n) {
            if (n != null)
                notes.add(n);
            return this;
        }

        public Diagnostic build() {
            return new Diagnostic(type, message, primary, secondaries, notes);
        }
    }
}