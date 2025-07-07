package com.example.db;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoProvider {
    private static MongoClient client;

    public static MongoClient get(Vertx vertx, JsonObject config) {
        if (client == null) {
            JsonObject mongoConfig = new JsonObject()
                    .put("connection_string", config.getString("mongo_uri"))
                    .put("db_name", "Todo");  // ← hardcoded or use config.getString("mongo_db")
            client = MongoClient.createShared(vertx, mongoConfig);
        }
        return client;
    }
}
