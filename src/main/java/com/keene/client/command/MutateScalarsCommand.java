package com.keene.client.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.ClientOptions;
import com.keene.client.api.ScalarApi;
import com.keene.client.http.StreamingHttpClient;
import com.keene.streaming.core.models.Scalar;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(
    name = "mutate-scalars",
    description = "Update existing scalars via the Scalar Service",
    mixinStandardHelpOptions = true
)
public class MutateScalarsCommand implements Callable<Integer> {

    @Mixin
    ClientOptions options;

    @Option(
        names = "--scalar-count",
        required = true,
        description = "Number of scalar updates to perform"
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

        List<Scalar> scalars = scalarApi.listScalars();
        if (scalars.isEmpty()) {
            System.err.println("No scalars found. Run generate-scalars first.");
            return 1;
        }

        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < scalarCount; i++) {
            Scalar scalar = randomScalar(scalars);
            double value = randomScalarValue();
            scalarApi.updateScalar(scalar.getId(), scalar.getName(), value);
            ids.add(scalar.getId());
        }

        System.out.println("Mutated " + scalarCount + " scalars");
        System.out.println("Ids: " + ids);
        System.out.println("Ids size: " + ids.size());
        return 0;
    }

    private static double randomScalarValue() {
        double raw = ThreadLocalRandom.current().nextDouble(1.0, 10.0);
        return Math.round(raw * 100.0) / 100.0;
    }

    private static Scalar randomScalar(List<Scalar> scalars) {
        return scalars.get(ThreadLocalRandom.current().nextInt(scalars.size()));
    }
}
