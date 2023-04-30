import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class ServerDAO {
    private final MongoCollection<Document> collection;

    public ServerDAO() {
        MongoDatabase database = MongoClients.create().getDatabase("servers");
        collection = database.getCollection("servers");
    }

    public List<Server> getAllServers() {
        List<Server> servers = new ArrayList<>();
        for (Document doc : collection.find()) {
            servers.add(documentToServer(doc));
        }
        return servers;
    }

    public Server getServerById(String id) {
        Document doc = collection.find(new Document("_id", id)).first();
        if (doc == null) {
            return null;
        }
        return documentToServer(doc);
    }

    public List<Server> getServersByName(String name) {
        List<Server> servers = new ArrayList<>();
        for (Document doc : collection.find(new Document("name", new Document("$regex", name)))) {
            servers.add(documentToServer(doc));
        }
        return servers;
    }

    public void addServer(Server server) {
        collection.insertOne(serverToDocument(server));
    }

    public void deleteServerById(String id) {
        collection.deleteOne(new Document("_id", id));
    }

    private Server documentToServer(Document doc) {
        return new Server(
                doc.getString("_id"),
                doc.getString("name"),
                doc.getString("language"),
                doc.getString("framework")
        );
    }

    private Document serverToDocument(Server server) {
        return new Document("_id", server.getId())
                .append("name", server.getName())
                .append("language", server.getLanguage())
                .append("framework", server.getFramework());
    }
}

public class Server {
    private String id;
    private String name;
    private String language;
    private String framework;

    public Server(String id, String name, String language, String framework) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.framework = framework;
    }

    // getters and setters
}

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
