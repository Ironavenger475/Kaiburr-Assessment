import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.*;

public class ServerAPI {
    private final Gson gson = new Gson();
    private final ServerDAO serverDAO = new ServerDAO();

    public void setupEndpoints() {
        get("/servers", this::getAllServers, gson::toJson);
        get("/servers/:id", this::getServerById, gson::toJson);
        put("/servers", this::addServer, gson::toJson);
        delete("/servers/:id", this::deleteServerById, gson::toJson);
        get("/servers/findByName/:name", this::getServersByName, gson::toJson);
    }

    private List<Server> getAllServers(Request req, Response res) {
        res.type("application/json");
        return serverDAO.getAllServers();
    }

    private Server getServerById(Request req, Response res) {
        String id = req.params(":id");
        Server server = serverDAO.getServerById(id);
        if (server == null) {
            res.status(404);
        }
        res.type("application/json");
        return server;
    }

    private Server addServer(Request req, Response res) {
        String body = req.body();
        Server server
