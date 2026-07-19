package com.keene.client.http;

public class StreamingHttpException extends RuntimeException {

    private final int statusCode;
    private final String method;
    private final String path;

    public StreamingHttpException(int statusCode, String method, String path, String body) {
        super(formatMessage(statusCode, method, path, body));
        this.statusCode = statusCode;
        this.method = method;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    private static String formatMessage(int statusCode, String method, String path, String body) {
        if (body == null || body.isBlank()) {
            return method + " " + path + " failed with HTTP " + statusCode;
        }
        return method + " " + path + " failed with HTTP " + statusCode + ": " + body;
    }
}
