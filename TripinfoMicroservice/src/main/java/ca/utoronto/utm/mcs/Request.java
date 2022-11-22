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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        //obtaining the parameters
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        if (body.has("uid") && body.has("radius")) {
            try {
                String uid = body.getString("uid");
                int radius = body.getInt("radius");

                if (radius < 0){
                    this.sendStatus(r, 400);
                    return;
                }//end if

                String url = "http://locationmicroservice:8000/location/nearbyDriver/%s?radius=%d";
                url = String.format(url, uid, radius);
                HttpClient http_client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

                HttpResponse<InputStream> response = http_client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                JSONObject locationRes = new JSONObject(Utils.convert(response.body()));

                //if the status code isn't 200 then something went wrong
                if (response.statusCode() != 200){
                    this.sendResponse(r, null, response.statusCode());
                    return;
                }//end if

                //the result from the location microservice
                JSONObject data = locationRes.getJSONObject("data");
                JSONArray drivers = new JSONArray();
                //iterate through all the drivers and put in the array
                Iterator<String> keys = data.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    drivers.put(key);
                }//end while

                JSONObject returnObject = new JSONObject();
                returnObject.put("data", drivers);
                returnObject.put("status", "OK");

                this.sendResponse(r, returnObject, 200);
            } catch (Exception e) { //something went wrong
                this.sendStatus(r, 500);
            }//end catch
        } else { //invalid parameters
            this.sendStatus(r, 400);
        }//end else
    }
}
