package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
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

    
    public String tripId;

    private static HttpResponse<String> sendRequest(String endpoint, String method, String body) throws InterruptedException, IOException, IOException {
        
        String API_URL = "http://localhost:8002";
        if(endpoint.charAt(0) == 'h'){
            API_URL = "";
        }//end if
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
        sendRequest("/location/user", "PUT", user.toString());

        //add their location
        JSONObject userStreet = new JSONObject();
        userStreet.put("latitude", 0.0);
        userStreet.put("longitude", 0.0);
        userStreet.put("street", street);
        sendRequest("/location/"+uid, "PATCH", userStreet.toString());
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
        //System.setOut(System.err);
        System.out.println(tripId);
        assertEquals(HttpURLConnection.HTTP_OK, result.statusCode());
    }//end tripConfirmPass
    
    @Test
    @Order(2)
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripRequestBody = new JSONObject();
        tripRequestBody.put("driver", "456");
        tripRequestBody.put("passenger", "123");

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm/", "POST", tripRequestBody.toString());
        assertEquals(400, confirmRes.statusCode());
    }//end tripConfirmPass

    @Test
    @Order(3)
    public void driverTimePass() throws JSONException, IOException, InterruptedException { //this will fail since navigation is giving a starnge error
        createRouteTrip("RoadOne", "RoadTwo", 30);
        JSONObject tripRequestBody = new JSONObject();

        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/" + tripId, "GET", tripRequestBody.toString());
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

    @Test
    @Order(7)
    public void patchTripPass() throws JSONException, IOException, InterruptedException {
        JSONObject tripPatchBody = new JSONObject();
        //I need distance, endTime, timeElapsed, totalCost 
        tripPatchBody.put("distance", 69);
        tripPatchBody.put("endTime", 69696969);
        tripPatchBody.put("timeElapsed", "00:69:00");
        tripPatchBody.put("timeCost", 69.69);

        HttpResponse<String> confirmRes = sendRequest("/trip/63803ebce21dca79c6437253", "PATCH", tripPatchBody.toString());
        assertEquals(200, confirmRes.statusCode());

    }

    @Test
    @Order(8)
    public void patchTripFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripPatchBody = new JSONObject();
        //I need distance, endTime, timeElapsed, totalCost 
        //Should fail cause incorrect body
        tripPatchBody.put("Fired from twitter", 69);
        tripPatchBody.put("endTime", 69696969);
        tripPatchBody.put("timeElapsed", "00:69:00");
        tripPatchBody.put("timeCost", 69.69);

        HttpResponse<String> confirmRes = sendRequest("/trip/"+tripId, "PATCH", tripPatchBody.toString());
        assertEquals(400, confirmRes.statusCode());

    }

    //So heads up, these trips have users that dont exist (Tests 9-12)
    //the driver and passenger 
    @Test
    @Order(9)
    public void tripsForPassengerPass() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("driver", "mr.white")
                .put("passenger", "jesse")
                .put("startTime", 345);
        HttpResponse<String> res = sendRequest("/trip/confirm", "POST", req.toString());


        res = sendRequest("/trip/passenger/jesse", "GET", req.toString());
        JSONArray trips = new JSONObject(res.body()).getJSONObject("data").getJSONArray("trips");
        

        assertTrue(res.statusCode()==HttpURLConnection.HTTP_OK);
    }

    @Test
    @Order(10)
    public void tripsForPassengerFail() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("passenger", "L")
                .put("startTime", 345);
        HttpResponse<String> res = sendRequest("/trip/confirm", "POST", req.toString());


        res = sendRequest("/trip/passenger/L", "GET", req.toString());


        assertTrue(res.statusCode()==HttpURLConnection.HTTP_NOT_FOUND);
    }

    @Test
    @Order(11)
    public void tripsForDriverPass() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("driver", "lannister")
                .put("passenger", "stark")
                .put("startTime", 345);
        HttpResponse<String> res = sendRequest("/trip/confirm", "POST", req.toString());


        res = sendRequest("/trip/driver/lannister", "GET", req.toString());
        JSONArray trips = new JSONObject(res.body()).getJSONObject("data").getJSONArray("trips");
        

        assertTrue(res.statusCode()==HttpURLConnection.HTTP_OK);
    }

    @Test   //should fail, missing passenger
    @Order(12)
    public void tripsForDriverFail() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("driver", "snow")
                .put("startTime", 345);
        HttpResponse<String> res = sendRequest("/trip/confirm/", "POST", req.toString());


        res = sendRequest("/trip/driver/snow", "GET", req.toString());

        assertTrue(res.statusCode()==HttpURLConnection.HTTP_NOT_FOUND);
    }




}//end AppTest
