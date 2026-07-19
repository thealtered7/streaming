package com.keene.client.command;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.ClientOptions;
import com.keene.client.api.ScalarApi;
import com.keene.client.http.StreamingHttpClient;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(
    name = "generate-scalars",
    description = "Create scalars via the Scalar Service",
    mixinStandardHelpOptions = true
)
public class GenerateScalarsCommand implements Callable<Integer> {

    @Mixin
    ClientOptions options;

    @Option(
        names = "--scalar-count",
        required = true,
        description = "Number of scalars to create"
    )
    int scalarCount;

    @Override
    public Integer call() throws Exception {
        if (scalarCount <= 0) {
            System.err.println("--scalar-count must be greater than 0");
            return 1;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        ScalarApi scalarApi = new ScalarApi(new StreamingHttpClient(options.baseUrl), objectMapper);

        for (int i = 0; i < scalarCount; i++) {
            String name = "scalar-" + UUID.randomUUID();
            double value = randomScalarValue();
            scalarApi.createScalar(name, value);
        }

        System.out.println("Created " + scalarCount + " scalars");
        return 0;
    }

    private static double randomScalarValue() {
        double raw = ThreadLocalRandom.current().nextDouble(1.0, 10.0);
        return Math.round(raw * 100.0) / 100.0;
    }
}
