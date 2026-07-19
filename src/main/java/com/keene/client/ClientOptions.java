package com.keene.client;

import picocli.CommandLine.Option;

public class ClientOptions {

    @Option(
        names = "--base-url",
        defaultValue = "http://localhost:8080",
        description = "Base URL of the streaming app (default: ${DEFAULT-VALUE})"
    )
    public String baseUrl;
}
