package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    final static String API_URL = "http://localhost:8002";
    String tripId;

    private static HttpResponse<String> sendRequest(String endpoint, String method, String body) throws InterruptedException, IOException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL + endpoint)).method(method, HttpRequest.BodyPublishers.ofString(body)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }//end sendRequest

    //Same helper functions that are in locationmicroservice AppTest
    public void createUserTrip(String uid, boolean isDriver, String street) throws JSONException, IOException, InterruptedException {
        //create a user
        JSONObject user = new JSONObject();
        user.put("uid", uid);
        user.put("is_driver", isDriver);
        sendRequest("http://localhost:8000/location/user", "PUT", user.toString());

        //add their location
        JSONObject userStreet = new JSONObject();
        userStreet.put("latitude", 0.0);
        userStreet.put("longitude", 0.0);
        userStreet.put("street", street);
        sendRequest("http://localhost:8000/location/"+uid, "PATCH", userStreet.toString());
    }//end createUser

    public void createRouteTrip(String road1, String road2, int time) throws JSONException, InterruptedException, IOException{
        //add a road
        JSONObject road1Neo = new JSONObject();
        road1Neo.put("roadName", road1);
        road1Neo.put("hasTraffic", false);
        sendRequest("http://localhost:8000/location/road", "PUT", road1Neo.toString());

        //add a road
        JSONObject road2Neo = new JSONObject();
        road2Neo.put("roadName", road2);
        road2Neo.put("hasTraffic", true);
        sendRequest("http://localhost:8000/location/road", "PUT", road2Neo.toString());

        // Create a route
        JSONObject route = new JSONObject();
        route.put("roadName1", road1);
        route.put("roadName2", road2);
        route.put("hasTraffic", true);
        route.put("time", time);
        sendRequest("http://localhost:8000/location/hasRoute", "POST", route.toString());
    }//end createRoute

    @Test
    @Order(1)
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException {
        createUserTrip("123", false, "RoadOne");
        createUserTrip("456", true, "RoadOne");

        JSONObject tripRequestBody = new JSONObject();
        tripRequestBody.put("driver", "456");
        tripRequestBody.put("passenger", "123");
        tripRequestBody.put("startTime", 2030);

        HttpResponse<String> result = sendRequest("/trip/confirm", "POST", tripRequestBody.toString());
        tripId = new JSONObject(result.body()).getJSONObject("data").getJSONObject("_id").getString("$oid");
        assertEquals(HttpURLConnection.HTTP_OK, result.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(2)
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripRequestBody = new JSONObject();
        tripRequestBody.put("driver", "456");
        tripRequestBody.put("passenger", "123");

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm/", "POST", tripRequestBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(3)
    public void driverTimePass() throws JSONException, IOException, InterruptedException { //this will fail since navigation is giving a starnge error
        createRouteTrip("RoadOne", "RoadTwo", 30);
        JSONObject tripRequestBody = new JSONObject();

        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/"+tripId, "GET", tripRequestBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(4)
    public void driverTimeFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripRequestBody = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime", "GET", tripRequestBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(5)
    public void tripRequestPass() throws JSONException, IOException, InterruptedException {
        JSONObject tripRequestBody = new JSONObject();
        tripRequestBody.put("uid", "123");
        tripRequestBody.put("radius", 5);

        HttpResponse<String> result = sendRequest("/trip/request", "POST", tripRequestBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, result.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(6)
    public void tripRequestFail() throws JSONException, IOException, InterruptedException { //this will fail since navigation is giving a starnge error
        JSONObject tripRequestBody = new JSONObject();
        tripRequestBody.put("uid", "123");

        HttpResponse<String> result = sendRequest("/trip/request", "POST", tripRequestBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, result.statusCode());
    }//end tripConfirmPass


}//end AppTest
