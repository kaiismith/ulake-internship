package org.usth.ict.ulake.acladvance.acladvance;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
class GreetingService {

    @Inject
    RedisClient redisClient;

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<List<String>> getReactive() {
        return reactiveRedisClient.keys("*")
                .map(response -> {
                    List<String> result = new ArrayList<>();
                    for (Response r : response) {
                        result.add(r.toString());
                    }
                    return result;
                });
    }

    String get(String key) {
        return redisClient.get(key).toString();
    }

    void set(String key, String value) {
        redisClient.set((Arrays.asList(key, value)));
    }
}
