package com.keene.client;

import com.keene.client.command.DeleteScalarCommand;
import com.keene.client.command.DeleteWideCommand;
import com.keene.client.command.GenerateScalarsCommand;
import com.keene.client.command.GenerateWideCommand;
import com.keene.client.command.MutateScalarsCommand;
import com.keene.client.command.MutateWideCommand;
import com.keene.client.http.StreamingHttpException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(
    name = "service-client",
    description = "CLI for GeoService, Scalar, and Wide Service HTTP APIs",
    mixinStandardHelpOptions = true,
    subcommands = {
        GenerateScalarsCommand.class,
        MutateScalarsCommand.class,
        DeleteScalarCommand.class,
        GenerateWideCommand.class,
        MutateWideCommand.class,
        DeleteWideCommand.class
    }
)
public class ServiceClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceClient.class);

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new ServiceClient());
        commandLine.setExecutionExceptionHandler((ex, cmd, parseResult) -> {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof StreamingHttpException httpEx) {
                logger.error(httpEx.getMessage());
                return httpEx.getStatusCode() >= 500 ? 2 : 1;
            }
            logger.error(cause.getMessage() != null ? cause.getMessage() : cause.toString());
            return 1;
        });
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
