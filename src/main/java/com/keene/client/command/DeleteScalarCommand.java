package com.keene.client.command;

import java.util.List;
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
    name = "delete-scalar",
    description = "Delete a scalar by id, or a random scalar when no id is given",
    mixinStandardHelpOptions = true
)
public class DeleteScalarCommand implements Callable<Integer> {

    private static final int RANDOM_DELETE_OFFSET = 100;

    @Mixin
    ClientOptions options;

    @Option(
        names = "--id",
        description = "Id of the scalar to delete. If omitted, a random scalar is deleted."
    )
    Long id;

    @Override
    public Integer call() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        ScalarApi scalarApi = new ScalarApi(new StreamingHttpClient(options.baseUrl), objectMapper);

        if (id != null) {
            scalarApi.deleteScalar(id);
            System.out.println("Deleted scalar " + id);
            return 0;
        }

        List<Scalar> scalars = scalarApi.listScalars(RANDOM_DELETE_OFFSET);
        if (scalars.isEmpty()) {
            System.out.println("No scalars found at offset " + RANDOM_DELETE_OFFSET);
            return 0;
        }

        Scalar scalar = randomScalar(scalars);
        scalarApi.deleteScalar(scalar.getId());
        System.out.println("Deleted scalar " + scalar.getId());
        return 0;
    }

    private static Scalar randomScalar(List<Scalar> scalars) {
        return scalars.get(ThreadLocalRandom.current().nextInt(scalars.size()));
    }
}
