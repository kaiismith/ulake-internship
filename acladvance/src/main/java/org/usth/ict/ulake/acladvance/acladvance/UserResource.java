package org.usth.ict.ulake.acladvance.acladvance;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    EntityManager entityManager;

    @GET
    @Path("/listAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = User.listAll();
        return Response.ok(users).build();
    }

    @GET
    @Path("/listByID/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).build();
        }
        return Response.status(200).entity(user).build();
    }

    @POST
    @Transactional
    @Path("/createUser/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(@PathParam("name") String name) {
        User user = new User();
        user.setName(name);
        entityManager.persist(user);
        return Response.ok("Added user " + name + " successfully to database").build();
    }

    @DELETE
    @Path("/deleteUser/{id}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(@PathParam("id") Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        entityManager.remove(user);
        return Response.status(200, "Deleted user " + id + " successfully from database").build();
    }

    @PUT
    @Path("/updateUser/{id}/{name}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateUser(@PathParam("id") Long id, @PathParam("name") String name) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        user.setName(name);
        entityManager.persist(user);
        return Response.status(200).entity("Updated name to " + name + " user " + id + " successfully").build();
    }
}
