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
 
public class AppTest {

    final static String API_URL = "http://localhost:8000";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String body) throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL + endpoint)).method(method, HttpRequest.BodyPublishers.ofString(body)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }//end sendRequest

    public void createUser(String uid, boolean isDriver, String street) throws JSONException, IOException, InterruptedException {
        //create a user
        JSONObject user = new JSONObject();
        user.put("uid", uid);
        user.put("is_driver", isDriver);
        sendRequest("/location/user", "PUT", user.toString());

        //add their location
        JSONObject userStreet = new JSONObject();
        userStreet.put("latitude", 0.0);
        userStreet.put("longitude", 0.0);
        userStreet.put("street", street);
        sendRequest("/location/" + uid, "PATCH", userStreet.toString());
    }//end createUser

    public void createRoute(String road1, String road2) throws JSONException, InterruptedException, IOException{
        //add a road
        JSONObject road1Neo = new JSONObject();
        road1Neo.put("roadName", road1);
        road1Neo.put("hasTraffic", false);
        sendRequest("/location/road", "PUT", road1Neo.toString());

        //add a road
        JSONObject road2Neo = new JSONObject();
        road2Neo.put("roadName", road2);
        road2Neo.put("hasTraffic", true);
        sendRequest("/location/road", "PUT", road2Neo.toString());

        // Create a route
        JSONObject route = new JSONObject();
        route.put("roadName1", road1);
        route.put("roadName2", road2);
        route.put("hasTraffic", true);
        route.put("time", 60);
        sendRequest("/location/hasRoute", "POST", route.toString());
    }//end createRoute

    @Test
    @Order(1)
    public void getNearbyDriverPass() throws IOException, InterruptedException, JSONException {
        createUser("123", false, "RoadOne");
        createUser("456", true, "RoadOne");

        String uriEndpoint = String.format("/location/nearbyDriver/%s?radius=%d", "123", 5);
        JSONObject obj = new JSONObject();
        HttpResponse<String> result = sendRequest(uriEndpoint, "GET", obj.toString());
        assertEquals(HttpURLConnection.HTTP_OK, result.statusCode());
    }//end getNearbyDriverPass
    @Test
    @Order(2)
    public void getNearbyDriverFail() throws IOException, InterruptedException, JSONException {
        String endpoint = String.format("/location/nearbyDriver/12?radius=1"); //no user with 12 as their uid
        JSONObject obj = new JSONObject();
        HttpResponse<String> result = sendRequest(endpoint, "GET", obj.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.statusCode());
    }//end getNearbyDriverFail

    @Test
    @Order(3) //this isn't working getting strange error in navigation
    public void getNavigationRoutePass() throws IOException, InterruptedException, JSONException {
        createRoute("RoadOne", "RoadTwo");

        createUser("111", false, "RoadOne");
        createUser("112", true, "RoadTwo");

        String endpoint = String.format("/location/navigation/111?passengerUid=112");
        JSONObject obj = new JSONObject();
        HttpResponse<String> result = sendRequest(endpoint, "GET", obj.toString());
        assertEquals(HttpURLConnection.HTTP_OK, result.statusCode());
    }
    @Test
    @Order(4)
    public void getNavigationRouteFail() throws IOException, InterruptedException, JSONException {
        String endpoint = String.format("/location/navigation/1?passengerUid=123"); //no driver uid
        JSONObject obj = new JSONObject();
        HttpResponse<String> result = sendRequest(endpoint, "GET", obj.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.statusCode());
    }//end getNavigationRouteFail
}
