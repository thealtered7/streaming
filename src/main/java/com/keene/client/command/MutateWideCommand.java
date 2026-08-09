package com.keene.client.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    name = "mutate-wide",
    description = "Update existing wide rows via the Wide Service",
    mixinStandardHelpOptions = true
)
public class MutateWideCommand implements Callable<Integer> {

    @Mixin
    ClientOptions options;

    @Option(
        names = "--wide-count",
        required = true,
        description = "Number of wide updates to perform"
    )
    int wideCount;

    @Override
    public Integer call() throws Exception {
        if (wideCount <= 0) {
            System.err.println("--wide-count must be greater than 0");
            return 1;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        WideApi wideApi = new WideApi(new StreamingHttpClient(options.baseUrl), objectMapper);

        List<Wide> wides = wideApi.listWides();
        if (wides.isEmpty()) {
            System.err.println("No wide rows found. Run generate-wide first.");
            return 1;
        }

        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < wideCount; i++) {
            Wide existing = randomWide(wides);
            Wide update = WideRandomValues.randomWide();
            wideApi.updateWide(existing.getId(), update);
            ids.add(existing.getId());
        }
        
        System.out.println("Mutated " + wideCount + " wide rows");
        System.out.println("Ids: " + ids);
        System.out.println("Ids size: " + ids.size());
        return 0;
    }

    private static Wide randomWide(List<Wide> wides) {
        return wides.get(ThreadLocalRandom.current().nextInt(wides.size()));
    }
}
