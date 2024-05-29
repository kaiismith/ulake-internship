package org.usth.ict.ulake.acladvance.acladvance;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @Inject
    GreetingService greetingService;

    @GET
    @Path("/cache/{key}")
    public String getCache(@PathParam("key") String key) {
        return greetingService.get(key);
    }

    @GET
    @Path("/cache/reactive")
    @Produces(MediaType.APPLICATION_JSON)
//    public Uni<List<String>> getReactive() {
//        return greetingService.getReactive();
//    }

    public void getReactive(@Suspended AsyncResponse asyncResponse) {
        long startTime = System.nanoTime();
        greetingService.getReactive()
                .subscribe().with(
                        result -> {
                            long endTime = System.nanoTime();
                            System.out.println("Fetch time: " + (endTime - startTime) / 1_000_000 + " ms");
                            asyncResponse.resume(Response.ok(result).build());
                        },
                        failure -> asyncResponse.resume(Response.serverError().entity(failure.getMessage()).build())
                );
    }

    @POST
    @Path("/cache")
    public PersonCache putCache(PersonCache personCache) {
        greetingService.set(personCache.key, personCache.value);
        return personCache;
    }
}
