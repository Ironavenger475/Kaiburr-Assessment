import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

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
