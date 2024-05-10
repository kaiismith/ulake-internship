package org.usth.ict.ulake.reauth;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RegisterRestClient(baseUri = "https://oauth2.googleapis.com")
public interface TokenExchangeService {

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    TokenResponse exchangeCodeForToken(@FormParam("code") String code,
                                       @FormParam("client_id") String clientId,
                                       @FormParam("client_secret") String clientSecret,
                                       @FormParam("redirect_uri") String redirectUri,
                                       @FormParam("grant_type") String grantType,
                                       @FormParam("response_type") String responseType,
                                       @FormParam("scope") String scope,
                                       @FormParam("approval_prompt") String approvalPrompt,
                                       @FormParam("access_type") String accessType);
}