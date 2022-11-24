package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.management.Query;

import java.util.Collections;

import static com.mongodb.client.model.Filters.eq;

public class MongoDao {
	
	public MongoCollection<Document> collection;
	private final String username = "root";
	private final String password = "123456";
	private final String dbName = "trip"; //not too sure what this should be yet

	public MongoDao() {
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String uriDb = "mongodb://" + username + ":" + password + "@" + addr +":27017";

		MongoClient mongoClient = MongoClients.create(uriDb);
		MongoDatabase database = mongoClient.getDatabase(this.dbName);
		this.collection = database.getCollection(this.dbName);
	}//end MongoDb

	public JSONObject addTrip(String driver, String passenger, int startTime) throws JSONException {
		Document doc = new Document();
		doc.put("driver", driver);
		doc.put("passenger", passenger);
		doc.put("startTime", startTime);

		//inserting into the database
		try {
			this.collection.insertOne(doc);
		} catch (Exception e) {
			System.out.println("Error occurred");
		}

		//getting the id for this trip
		JSONObject result = new JSONObject(doc.toJson().toString());
		JSONObject id = new JSONObject();
		id.put("_id", result.get("_id"));

		//returning the data
		JSONObject data = new JSONObject();
		data.put("data", id);
		return data;
	}

	public Document getTrip(String id) throws JSONException {
		//getting the trip
		Document trip = null;
		try {
			trip = this.collection.find(eq("_id", new ObjectId(id))).first();
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return trip;
	}

}
