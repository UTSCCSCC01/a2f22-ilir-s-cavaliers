package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue; //Warning says its unused but i used it???

/**
 * Please write your tests in this class. 
 */


 
public class AppTest {
    
    @BeforeAll
public static void init()throws IOException {
   // Runtime.getRuntime().exec("mvn exec:java");
}

@AfterAll
public static void teardown() throws IOException, InterruptedException {

}

    @Test
    @Order(1)
    public void userRegisterPass() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"name\": \"kia\", \"email\": \"kiaemail\", \"password\": \"12345\"}";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> result = client.send(request, BodyHandlers.ofString());
            //System.out.println("registerSuccess: " + response.statusCode() + response.body());
           // System.out.println(result.statusCode());
            assertEquals(200, result.statusCode());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(2) 
    public void userRegisterFail() {
        //Reason for fail is bad body (missing email/passowrd)
        //Should fail regardless of implementation
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"name\": \"Dumbname\"}";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println("registerFail: " + response.statusCode() + response.body());
            assertTrue(response.statusCode()==400);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void userLoginPass() {
        try {
            //First registers a new client
            HttpClient client = HttpClient.newHttpClient();
            String body1 = "{\"name\": \"nicole\", \"email\": \"nicoleemail\", \"password\": \"12345\"}";
            HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://0.0.0.0:8001/user/register"))
                .header("Content-Type", "application/json")
                .method("POST", BodyPublishers.ofString(body1))
                .build();
            client.send(request1, BodyHandlers.ofString());
            //check if inserted correctly
            HttpResponse<String> response1 = client.send(request1, BodyHandlers.ofString());
           // assertEquals(200, response1.statusCode());

            //now tries to log in
            String body2 = "{\"email\": \"nicolemail\", \"password\": \"12345\"}";
            HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8001/user/login"))
                .header("Content-Type", "application/json")
                .method("POST", BodyPublishers.ofString(body2))
                .build();
            HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
            //System.out.println("loginPass: " + response.statusCode() + response.body());
            assertEquals(200, response2.statusCode());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void userLoginFail() {
        try {
            //Reason for fail is bad body (missing: passowrd)
             //Should fail regardless of implementation
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"email\": \"someemail\"}";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8001/user/login"))
                .header("Content-Type", "application/json")
                .method("POST", BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println("loginFail: " + response.statusCode() + response.body());
            assertTrue(response.statusCode()==400);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
