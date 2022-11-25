package ca.utoronto.utm.mcs;

/**
 * Everything you need in order to send and recieve httprequests to
 * other microservices is given here. Do not use anything else to send
 * and/or recieve http requests from other microservices. Any other
 * imports are fine.
 */

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        
        //obtaining the parameters
        String[] allParameters = r.getRequestURI().toString().split("/");
        if (allParameters.length != 4 || allParameters[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }//end if

        try {
            //adding the trip to the db
            Document responseDoc = this.dao.getTrip(allParameters[3]);
            if (responseDoc == null) { //the trip is not found
                this.sendStatus(r, 404);
                return;
            }//end if

            //our response object
            JSONObject responseObject = new JSONObject(responseDoc.toJson().toString());

            //sending an http request to our location microservice in navigation
            String url = "http://locationmicroservice:8000/location/navigation/%s?passengerUid=%s";
            url = String.format(url, responseObject.getString("driver"), responseObject.getString("passenger"));
            HttpClient http_client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<InputStream> toLocation = http_client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            //if we receive nothing something went wrong
            if (toLocation == null) {
                this.sendStatus(r, 500);
                return;
            }//end if

            //if the response is not as wanted
            if (toLocation.statusCode() != 200) {
                this.sendResponse(r, null, toLocation.statusCode());
                return;
            }//end if

            //the result from the location microservice
            JSONObject locationResponseObject = new JSONObject(Utils.convert(toLocation.body()));
            JSONObject data = locationResponseObject.getJSONObject("data");
            int totalTime = data.getInt("total_time");

            //returning
            JSONObject arrivalTime = new JSONObject();
            arrivalTime.put("arrival_time", totalTime);

            JSONObject returnObject = new JSONObject();
            returnObject.put("data", arrivalTime);
            returnObject.put("status", "OK");

            this.sendResponse(r, returnObject, 200);
        } catch (Exception e) { //something went wrong
            this.sendStatus(r, 500);
        }//end catch
        
    }
}
