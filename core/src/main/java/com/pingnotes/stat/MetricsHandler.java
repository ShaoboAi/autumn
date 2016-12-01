package com.pingnotes.stat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

/**
 * Created by shaobo on 11/9/16.
 */
public class MetricsHandler implements HttpHandler {
    private CollectorRegistry registry;

    public MetricsHandler() {
        this(CollectorRegistry.defaultRegistry);
    }

    /**
     * Construct a MetricsServlet for the given registry.
     */
    public MetricsHandler(CollectorRegistry registry) {
        this.registry = registry;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

        Writer writer = new PrintWriter(new OutputStreamWriter(httpExchange.getResponseBody()));
        TextFormat.write004(writer, registry.metricFamilySamples());
        writer.flush();
        writer.close();
    }
}
