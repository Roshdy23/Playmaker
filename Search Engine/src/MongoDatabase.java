import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoDatabase {
    private MongoClientURI clientURI;
    private MongoClient mongoClient;
    private  com.mongodb.client.MongoDatabase mongoDatabase;

    public MongoDatabase(){
        clientURI = new MongoClientURI("mongodb+srv://msayedelbohy:8MXR9t5np1blblhr@playmaker.zsurogn.mongodb.net/");
        mongoClient = new MongoClient(clientURI);
        mongoDatabase = mongoClient.getDatabase("Playmaker");
        System.out.println("Database connected");
    }

    public com.mongodb.client.MongoDatabase getDatabase(){ return mongoDatabase; }
    public MongoCollection<Document> getCollection(String collection){ return mongoDatabase.getCollection(collection); }

}
