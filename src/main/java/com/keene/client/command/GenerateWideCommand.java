package com.keene.client.command;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keene.client.ClientOptions;
import com.keene.client.api.WideApi;
import com.keene.client.http.StreamingHttpClient;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(
    name = "generate-wide",
    description = "Create wide rows via the Wide Service",
    mixinStandardHelpOptions = true
)
public class GenerateWideCommand implements Callable<Integer> {

    @Mixin
    ClientOptions options;

    @Option(
        names = "--wide-count",
        required = true,
        description = "Number of wide rows to create"
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

        for (int i = 0; i < wideCount; i++) {
            wideApi.createWide(WideRandomValues.randomWide());
        }

        System.out.println("Created " + wideCount + " wide rows");
        return 0;
    }
}
