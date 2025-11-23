package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.klang.core.errors.DiagnosticException;
import org.klang.core.errors.DiagnosticPrinter;

import picocli.CommandLine;

@Command(name = "kc", description = "Klang CLI", mixinStandardHelpOptions = false, versionProvider = KVersionProvider.class, subcommands = {
        LexCommand.class,
        GenerateCompletion.class,
        HelpCommand.class,
        UpdateCommand.class
})

public class KMain implements Runnable {

    @Option(names = { "-h", "--help" }, description = "Show this help catalog")
    boolean help;

    @Option(names = { "-V", "--version" }, versionHelp = true, description = "Show Klang version")
    boolean version;

    @Override
    public void run() {
        if (help) {
            new HelpCommand().run();
            return;
        }

        if (version) {
            System.out.println(new KVersionProvider().getVersion()[0]);
            return;
        }

        new HelpCommand().run();
    }

    public static void main(String[] args) {
        try {
            int exitCode = new CommandLine(new KMain()).execute(args);
            System.exit(exitCode);
        } catch (DiagnosticException e) {
            // print all collector diagnostics instead of stacktrace
            DiagnosticPrinter p = new DiagnosticPrinter(true, true);
            p.print(e.diagnostic);
            System.exit(1);
        }

    }

}
