package com.example.config;

import io.vertx.core.Vertx;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AppConfig {

    public static void loadConfig(Vertx vertx, String filePath, java.util.function.Consumer<Promise<JsonObject>> handler) {
        Promise<JsonObject> promise = Promise.promise();

        vertx.executeBlocking(future -> {
            try {
                String content = Files.readString(Paths.get(filePath));
                JsonObject config = new JsonObject(content);
                future.complete(config);
            } catch (Exception e) {
                future.fail("Failed to read config: " + e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
                promise.complete((JsonObject) res.result());
            } else {
                promise.fail(res.cause());
            }
            handler.accept(promise);
        });
    }
}
