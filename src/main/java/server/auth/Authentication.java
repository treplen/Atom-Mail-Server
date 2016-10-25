package server.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.api.profile;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Path("/auth")
public class Authentication {
    private static final Logger log = LogManager.getLogger(Authentication.class);
    private static ConcurrentHashMap<Long, String> credentials;
    private static ConcurrentHashMap<Long, Long> tokens;
    private static ConcurrentHashMap<Long, Long> tokensReversed;

    // curl -i
    //      -X POST
    //      -H "Content-Type: application/x-www-form-urlencoded"
    //      -H "Host: {IP}:8080"
    //      -d "login={}&password={}"
    // "{IP}:8080/auth/register"
    @POST
    @Path("register")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response register(@FormParam("login") String user,
                             @FormParam("password") String password) {

        if (user == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Long ID;
        if((ID=profile.newPlayer(user))==0L)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        if (credentials.putIfAbsent(ID, password) != null) {
            log.error("ID duplicate encountered");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        log.info("New user '{}' registered", user);
        return Response.ok("User " + user + " registered.").build();
    }

    static {
        credentials = new ConcurrentHashMap<>();
        credentials.put(1L, "admin");
        tokens = new ConcurrentHashMap<>();
        tokens.put(1L, 1L);
        tokensReversed = new ConcurrentHashMap<>();
        tokensReversed.put(1L, 1L);
    }

    // curl -X POST
    //      -H "Content-Type: application/x-www-form-urlencoded"
    //      -H "Host: localhost:8080"
    //      -d "login=admin&password=admin"
    // "http://localhost:8080/auth/login"
    @POST
    @Path("login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response authenticateUser(@FormParam("login") String user,
                                     @FormParam("password") String password) {

        if (user == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            // Authenticate the user using the credentials provided
            if (!authenticate(user, password)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            // Issue a token for the user
            long token = issueToken(user);
            log.info("User '{}' logged in", user);

            // Return the token on the response
            return Response.ok(Long.toString(token)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    //curl -i
    //     -X POST
    //     -H "Authorization: Bearer {token}"
    //     -H "Content-Type: application/x-www-form-urlencoded"
    //     -H "Host: localhost:8080"
    // "localhost:8080/auth/logout"

    @Authorized
    @POST
    @Path("logout")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response removeUser(ContainerRequestContext requestContext) {

        try {

            Long token = Long.parseLong(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION).substring("Bearer".length()).trim());
            Long ID = tokensReversed.get(token);
            tokens.remove(ID);
            tokensReversed.remove(token);
            log.info("User ID: '{}' logged out", ID);

            // Return the token on the response
            return Response.ok("User ID: " + ID + " logged out.").build();

        } catch (Exception e) {
            log.error("Token tables are not synchronized");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean authenticate(String user, String password) throws Exception {
        return password.equals(credentials.get(profile.getID(user)));
    }

    public static Long getID(Long token)
    {
        return tokensReversed.get(token);
    }

    private Long issueToken(String user) {
        Long ID=profile.getID(user);
        Long token = tokens.get(ID);
        if (token != null) {
            return token;
        }
        if (ID==0L)
            ID=profile.newPlayer(user);
        token = ThreadLocalRandom.current().nextLong();
        tokens.put(ID, token);
        tokensReversed.put(token, ID);
        return token;
    }

    static public List<Long> getLoggedUsers()
    {
        List<Long> res =new ArrayList<>();
        tokensReversed.forEachValue(1,ID -> res.add(ID));
        return res;
    }

    static void validateToken(String rawToken) throws Exception {
        Long token = Long.parseLong(rawToken);
        if (!tokensReversed.containsKey(token)) {
            throw new Exception("Token validation exception");
        }
        log.info("Correct token from '{}'", tokensReversed.get(token));
    }
}
