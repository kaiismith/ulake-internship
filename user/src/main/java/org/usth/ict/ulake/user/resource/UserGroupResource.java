package org.usth.ict.ulake.user.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserGroup;
import org.usth.ict.ulake.user.persistence.UserGroupRepository;
import org.usth.ict.ulake.user.persistence.UserRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Path("/user/group")
@Tag(name = "User Groups")
@Produces(MediaType.APPLICATION_JSON)
public class UserGroupResource {
    private static final Logger log = LoggerFactory.getLogger(UserGroupResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserGroupRepository repo;

    @Inject
    UserRepository repoUser;

    @GET
    @Operation(summary = "List all user groups")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one user group info")
    public Response one(@PathParam("id") @Parameter(description = "User group id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Create a new user group")
    public Response post(@RequestBody(description = "New user group info to save") UserGroup entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Update an existing user group")
    public Response update(@PathParam("id") @Parameter(description = "User id to update") Long id,
                           @RequestBody(description = "New user group info to update") UserGroup newEntity) {
        UserGroup entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;

        if (newEntity.users != null && newEntity.users.size() > 0) {
            Set<User> attachedUsers = new HashSet<>();
            for (User user : newEntity.users) {
                if (user.id != null && user.id > 0) {
                    attachedUsers.add(repoUser.findById(user.id));
                }
            }
            entity.users = attachedUsers;
        }
        repo.persist(entity);
        return response.build(200);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    @Operation(summary = "Delete one user group")
    public Response delete(@PathParam("id") @Parameter(description = "User group id to delete") Long id) {
        UserGroup entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "User", "Admin" })
    public Response stats() {
        HashMap<String, Integer> ret = new HashMap<>();
        ret.put("groups", (int) repo.count());
        return response.build(200, "", ret);
    }
}
