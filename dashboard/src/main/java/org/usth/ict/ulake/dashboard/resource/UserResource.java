package org.usth.ict.ulake.dashboard.resource;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.user.AuthModel;
import org.usth.ict.ulake.common.service.UserService;

@Path("/user")
@Tag(name = "User")
public class UserResource {

    @Inject
    @RestClient
    UserService userSvc;

    @Inject
    LakeHttpResponse resp;

    @POST
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Login to dashboard")
    public Response login(AuthModel auth) {
        var authResp = userSvc.getToken(auth);
        return resp.build(
                   authResp.getCode(),
                   authResp.getMsg(),
                   authResp.getResp());
    }
}
