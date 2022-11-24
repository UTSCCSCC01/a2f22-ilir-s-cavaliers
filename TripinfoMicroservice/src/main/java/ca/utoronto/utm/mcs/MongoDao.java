package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.management.Query;

import java.util.ArrayList;
import java.util.Collections;

import static com.mongodb.client.model.Filters.eq;

public class MongoDao {
	
	public MongoCollection<Document> collection;
	private final String username = "root";
	private final String password = "123456";
	private final String dbName = "trips";

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

	public JSONArray getUserTrips(String uid, String type) throws JSONException {
		//Filter for given type
        FindIterable<Document> data = this.collection.find(Filters.eq(type,uid));
        try {
            JSONArray result = new JSONArray();
			//iterate through trips
            for (Document doc : data) {
                doc.put("_id",doc.getObjectId("_id").toString());
                doc.remove(type);
                result.put(doc);
            }
            return result;
        } catch (Exception e) {
            throw e;
        }
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

	public boolean updateTrip(int endTime, double totalCost, int timeElapsed, int distance, ObjectId id){
		Document doc = new Document();
		doc.put("distance", distance);
		doc.put("endTime", endTime);
		doc.put("timeElapsed", timeElapsed);
		doc.put("totalCost", totalCost);
		try{
			///If the trips doesnt exist return false
			boolean exist = this.tripExists(id);
			if(!exist){
				return false;
			}
		}catch (Exception e) {
			throw e;
		}
		try {
			this.collection.updateOne(Filters.eq("_id", id), new Document("$set", doc));
			return true;	//return true on successful update
		}catch (Exception e) {
			throw e;
		}
	}

	public boolean tripExists(ObjectId id){
		//Filter for the id
		FindIterable<Document> data = this.collection.find(Filters.eq("_id",id));
		//iterate through the trips found
			try {
				JSONArray result = new JSONArray();
				for (Document doc : data) {
					doc.put("_id", doc.getObjectId("_id").toString());
					result.put(doc);
				}
				//if no trips are found (empty array) return FALSE
				if(result.length() == 0){
					return false;
				}
				//otherwise TRUE
				return true;
			}catch (Exception e) {
				throw e;
			}
		}


	public ArrayList<String> tripInfo(ObjectId id) {
		//Filter for id
		FindIterable<Document> data = this.collection.find(Filters.eq("_id", id));
		ArrayList<String> result = new ArrayList<>();
		//iterate through trips
		try {
			for (Document doc : data) {
				result.add(doc.getString("driver"));
				result.add(doc.getString("passenger"));
			}
			return result;
		}catch (Exception e) {
			throw e;
		}
	}

	public void deleteAll(){
		this.collection.deleteMany(new Document());
		return;
	}

}
