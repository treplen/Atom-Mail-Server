package server.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.auth.Authentication;
import server.auth.Authorized;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * Created by svuatoslav on 10/24/16.
 */
@Path("/data")
public class data {
    private static final Logger log = LogManager.getLogger(data.class);

    @Authorized
    @POST
    @Path("users")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response getUsers() {
        String res = "{\n\t\"players\": [\n";
        List<Long> IDs = Authentication.getLoggedUsers();
        for(Iterator<Long> i = IDs.iterator(); i.hasNext();)
        {
            res+=profile.getPlayer(i.next()).writeJson(2);
            if(i.hasNext())
                res+=",\n";
        }
        res+="\n\t]\n}";
        log.info("User json requested and provided");
        return Response.ok(res).build();
    }
}
