package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.klang.core.errors.Diagnostic;
import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticFormatter;
import org.klang.core.errors.DiagnosticType;
import org.klang.core.errors.Note;
import org.klang.core.errors.Span;
import org.klang.core.lexer.Lexer;

@Command(name = "lex", description = "Show file tokens")
public class LexCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    private final DiagnosticFormatter formatter = new DiagnosticFormatter();

    @Override
    public void run() {
        try {
            Path path = file.toPath();

            if (!path.endsWith(".k")) {
                Diagnostic d = new Diagnostic(
                        DiagnosticType.INFO,
                        "The file is not a .k file.",
                        new Span(path.toString(), 1, 1, 1, 1))
                        .addNote(new Note("You probably gave incorrect information."));

                // Print the formatted error before throwing

                throw new DiagnosticException(d);
            }

            String source = Files.readString(path);

            Lexer lexer = new Lexer(source, file.getPath());
            lexer.tokenize().forEach(System.out::println);

            // Provavelmente na sua Main ou CLI
        } catch (DiagnosticException e) {
            formatter.print(e.diagnostic);

        } catch (Exception e) {
            // erro inesperado → mostrar stacktrace (para debug)
            e.printStackTrace();
            System.exit(2);
        }
    }
}
