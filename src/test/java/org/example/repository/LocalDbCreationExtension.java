package org.example.repository;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LocalDbCreationExtension implements BeforeAllCallback, AfterAllCallback {
    protected DynamoDBProxyServer server;
    public LocalDbCreationExtension() {
        System.setProperty("sqlite4java.library.path", "native-libs");
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        this.stopUnchecked(server);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        String port = "8000";
        this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", port});
        server.start();
    }


    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }}
