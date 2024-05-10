package org.usth.ict.ulake.reauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Map;

@Path("/auth")
public class AuthResource {

    @Inject
    SecurityIdentity identity;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    @Inject
    @RestClient
    TokenExchangeService tokenExchangeService;

    @Inject
    JsonWebToken jwt;

    @Context
    HttpServletRequest request;

    @GET
    @Path("/login")
    public Response redirectToAuthServer() {
        UriBuilder authUriBuilder = UriBuilder.fromPath(authServerUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080/auth/callback")
                .queryParam("prompt", "consent")
                .queryParam("access_type", "offline");
        return Response.seeOther(authUriBuilder.build()).build();
    }

    @GET
    @Path("/callback")
    @Authenticated
    public Response callback(@QueryParam("code") String code) {

        System.out.println(code);

        if (code == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (identity.isAnonymous()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        TokenResponse tokenResponse = null;

        // Exchange authorization code for tokens
        try {
            tokenResponse = tokenExchangeService.exchangeCodeForToken(code,
                    clientId,
                    clientSecret,
                    "http://localhost:8080/auth/callback",
                    "authorization_code",
                    "code",
                    "email profile openid https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
                    "force",
                    "offline");
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to exchange code for tokens.").build();
        }

        if (tokenResponse == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Use tokens as needed
        String accessToken = tokenResponse.getAccessToken();
        String idToken = tokenResponse.getIdToken();
        String refreshToken = tokenResponse.getRefreshToken();

        HttpSession session = request.getSession(true);
        session.setAttribute("idToken", idToken);

//        JsonObject idTokenClaims = jwt.getClaim(Claims.raw_token.name());
//        String applicationData = null;
//        if (idTokenClaims != null) {
//            applicationData = idTokenClaims.getString("application_data");
//        }
//
//        // Use application data as needed
//        if (applicationData != null) {
//            System.out.println("Application Data: " + applicationData);
//        } else {
//            System.out.println("Application Data not found in ID token.");
//        }

        String applicationData = null;
        try {
            DecodedJWT jwt = JWT.decode(idToken);

            String header = jwt.getHeader();
            System.out.println("Header: " + header);

            // Extract and print the payload/body
            String payload = jwt.getPayload();
            System.out.println("Payload: " + payload);

            Map<String, Claim> claims = jwt.getClaims();
            for (Map.Entry<String, Claim> entry : claims.entrySet()) {
                String claimName = entry.getKey();
                Claim claimValue = entry.getValue();
                System.out.println(claimName + ": " + claimValue.asString());
            }

            // Extract and print the signature
            String signature = jwt.getSignature();
            System.out.println("Signature: " + signature);

//            Claim applicationDataClaim = jwt.getClaim("application_data");
//            if (!applicationDataClaim.isNull()) {
//                applicationData = applicationDataClaim.asString();
//            }
        } catch (JWTDecodeException e) {
            System.out.println("Error decoding ID token: " + e.getMessage());
        }

//        System.out.println(applicationData);


        // Example: Print tokens
        System.out.println("Access Token: " + accessToken);
        System.out.println("ID Token: " + idToken);
        System.out.println("Refresh Token: " + refreshToken);
        System.out.println("Expiration: " + tokenResponse.getExpiresIn());
        System.out.println("Scope: " + tokenResponse.getScope());
        System.out.println("Token Type: " + tokenResponse.getTokenType());

        // Here you can implement logic to handle tokens, such as storing them in the session
        // You can also use the identity object to get user information

//        JsonObject idTokenJson = JwtUtils.decode(idToken)

        // Call Google's userinfo endpoint to get user information
        Client client = ClientBuilder.newClient();
        Response userInfoResponse = client.target("https://www.googleapis.com/oauth2/v3/userinfo")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .get();

        if (userInfoResponse.getStatus() == Response.Status.OK.getStatusCode()) {
            // Parse and use the user information as needed
            String userInfo = userInfoResponse.readEntity(String.class);
            System.out.println("User Info: " + userInfo);


        } else {
            // Handle error response from userinfo endpoint
            System.out.println("Failed to retrieve user information from Google. Status: " + userInfoResponse.getStatus());
        }

        // Close the client
        client.close();

        return Response.ok("Authentication successful. User: " + identity.getPrincipal().getName()).build();
    }
}


//    @GET
//    @Path("/google")
//    public Response redirectToGoogle() {
//        UriBuilder authUriBuilder = UriBuilder.fromPath(authServerUrl)
//                .queryParam("response_type", "code")
//                .queryParam("client_id", clientId)
//                .queryParam("redirect_uri", "/auth/callback"); // Directly specify the callback URI
//        return Response.seeOther(authUriBuilder.build()).build();
//    }
//
//    @GET
//    @Path("/callback")
//    public Response googleCallback() {
//        // Handle callback from Google, exchange code for tokens
//        return Response.ok("Callback from Google received.").build();
//    }

//            JsonObject userInfoJson = Json.createReader(new StringReader(userInfo)).readObject();
//            String email = userInfoJson.getString("email");
//
//            HttpSession session = request.getSession(true);
//            session.setAttribute("email", email);

//            System.out.println("Email: " + email);

