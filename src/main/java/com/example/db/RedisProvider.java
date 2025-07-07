package com.example.db;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class RedisProvider {

    private static RedisAPI redisAPI;

    public static RedisAPI get(Vertx vertx, String host) {
        if (redisAPI == null) {
            RedisOptions options = new RedisOptions().addConnectionString("redis://" + host + ":6379");
            Redis redis = Redis.createClient(vertx, options);
            redisAPI = RedisAPI.api(redis);
        }
        return redisAPI;
    }
}
