package server;

import org.jetbrains.annotations.NotNull;
import server.api.ApiServlet;

/**
 * Created by svuatoslav on 10/24/16.
 */

public class Server {
    public static void main(@NotNull String[] args) throws Exception {
        ApiServlet.start();
    }
}