package com.keene.client.command;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.ClientOptions;
import com.keene.client.api.WideApi;
import com.keene.client.http.StreamingHttpClient;
import com.keene.streaming.core.models.Wide;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(
    name = "delete-wide",
    description = "Delete a wide row by id, or a random wide row when no id is given",
    mixinStandardHelpOptions = true
)
public class DeleteWideCommand implements Callable<Integer> {

    private static final int RANDOM_DELETE_OFFSET = 100;

    @Mixin
    ClientOptions options;

    @Option(
        names = "--id",
        description = "Id of the wide row to delete. If omitted, a random wide row is deleted."
    )
    Long id;

    @Override
    public Integer call() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        WideApi wideApi = new WideApi(new StreamingHttpClient(options.baseUrl), objectMapper);

        if (id != null) {
            wideApi.deleteWide(id);
            System.out.println("Deleted wide " + id);
            return 0;
        }

        List<Wide> wides = wideApi.listWides(RANDOM_DELETE_OFFSET);
        if (wides.isEmpty()) {
            System.out.println("No wide rows found at offset " + RANDOM_DELETE_OFFSET);
            return 0;
        }

        Wide wide = randomWide(wides);
        wideApi.deleteWide(wide.getId());
        System.out.println("Deleted wide " + wide.getId());
        return 0;
    }

    private static Wide randomWide(List<Wide> wides) {
        return wides.get(ThreadLocalRandom.current().nextInt(wides.size()));
    }
}
