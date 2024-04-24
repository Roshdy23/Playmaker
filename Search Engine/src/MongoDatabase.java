import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

public class MongoDatabase {
    private MongoClientURI clientURI;
    private MongoClient mongoClient;
    private  com.mongodb.client.MongoDatabase mongoDatabase;

    public void MongoDatabase(){
        clientURI = new MongoClientURI("mongodb+srv://msayedelbohy:8MXR9t5np1blblhr@playmaker.zsurogn.mongodb.net/");
        mongoClient = new MongoClient(clientURI);
        mongoDatabase = mongoClient.getDatabase("Playmaker");
    }

    public com.mongodb.client.MongoDatabase getDatabase(){ return mongoDatabase; }
    public MongoCollection getCollection(String collection){ return mongoDatabase.getCollection(collection); }

}
