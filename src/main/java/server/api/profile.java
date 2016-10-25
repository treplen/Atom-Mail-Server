package server.api;

import model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import server.auth.Authentication;
import server.auth.Authorized;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by svuatoslav on 10/24/16.
 */
@Path("/profile")
public class profile {
    private static final Logger log = LogManager.getLogger(profile.class);
    private static ConcurrentHashMap<Long,Player> players;
    private static ConcurrentHashMap<String,Long> IDs;

    static {
        Player admin = new Player("admin");
        IDs=new ConcurrentHashMap<>();
        IDs.put("admin",1L);
        players=new ConcurrentHashMap<>();
        players.put(1L,admin);
    }

    @Authorized
    @POST
    @Path("name")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response rename(ContainerRequestContext requestContext, @FormParam("name") String name) {
        log.info(name);
        if((IDs.get(name))!=null)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        String oldName;
        Long ID = Authentication.getID(Long.parseLong(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION).substring("Bearer".length()).trim()));
        log.info(ID);
        Player player = players.get(ID);
        log.info(player);
        oldName=player.getName();
        log.info(oldName);
        player.rename(name);
        IDs.remove(oldName);
        IDs.put(name,ID);
        log.info("User "+ ID + " renamed to " +name);
        return Response.ok("Name changed to " + name).build();
    }

    public static Long newPlayer(@NotNull String name)
    {
        if (IDs.get(name)!=null)
            return 0L;
        Player player=new Player(name);
        Long ID = ThreadLocalRandom.current().nextLong();
        IDs.put(name,ID);
        players.put(ID,player);
        return ID;
    }

    public static Long getID(@NotNull String name)
    {
        return IDs.get(name);
    }

    static Player getPlayer(Long ID)
    {
        return players.get(ID);
    }

}
