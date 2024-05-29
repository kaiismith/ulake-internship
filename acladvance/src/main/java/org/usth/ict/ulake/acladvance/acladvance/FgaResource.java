package org.usth.ict.ulake.acladvance.acladvance;

import dev.openfga.sdk.errors.FgaInvalidParameterException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Path("/fga")
public class FgaResource {
    @Inject
    FgaService fgaService;

    @GET
    @Path("/write-example")
    @Produces(MediaType.TEXT_PLAIN)
    public String writeExample() {
        try {
            fgaService.writeExample();
            return "Write example executed successfully";
        } catch (ExecutionException | InterruptedException | FgaInvalidParameterException e) {
            e.printStackTrace();
            return "Error executing write example: " + e.getMessage();
        }
    }
}
