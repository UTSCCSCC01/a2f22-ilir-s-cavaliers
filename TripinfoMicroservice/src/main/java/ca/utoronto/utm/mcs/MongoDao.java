package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection. 
        // Use Dotenv like in the DAOs of the other microservices.
	}

	// *** implement database operations here *** //

}
