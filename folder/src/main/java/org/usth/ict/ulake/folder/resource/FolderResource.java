package org.usth.ict.ulake.folder.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.folder.model.UserFolder;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/folder")
@Tag(name = "Folder")
@Produces(MediaType.APPLICATION_JSON)
public class FolderResource {
    @Inject
    FolderRepository repo;

    @Inject
    LakeHttpResponse response;

    @GET
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all folders")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one folder")
    public Response one(
        @PathParam("id")
        @Parameter(description = "Folder id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @GET
    @Path("/root")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List root folders")
    public Response root() {
        return response.build(200, null, repo.listRoot());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new folder")
    public Response post(
        @RequestBody(description = "Folder to save") UserFolder entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a folder information")
    public Response update(
        @PathParam("id")
        @Parameter(description = "Folder id to update")
        Long id,
        @RequestBody(description = "New folder information")
        UserFolder data) {
        UserFolder entity = repo.findById(id);

        if (!Utils.isEmpty(data.name)) entity.name = data.name;
        if (data.ownerId != null) entity.ownerId = data.ownerId;

        if (data.parent != null && data.parent.id != null) {
            entity.parent = repo.findById(data.parent.id);
        }

        repo.persist(entity);
        return response.build(200, null, entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a folder")
    public Response delete (
        @PathParam("id")
        @Parameter(description = "Folder id to delete") Long id) {
        UserFolder entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}
