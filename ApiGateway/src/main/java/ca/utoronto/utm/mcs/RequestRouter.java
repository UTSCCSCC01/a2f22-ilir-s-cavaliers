package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response
import java.util.HashMap;

public class RequestRouter implements HttpHandler {

	HashMap<String, String> allMicroService;
	HttpClient httpClient;

	public RequestRouter() {
		httpClient = HttpClient.newHttpClient();
		allMicroService = new HashMap<String, String>();

		allMicroService.put("location", "http://localhost:8000"); //location microservice
		allMicroService.put("user", "http://localhost:8001"); //user microservice
		allMicroService.put("trip", "http://localhost:8002"); //trip info microservice
	}//end RequestRouter constructor

	@Override
	public void handle(HttpExchange r) throws IOException {
        String [] params = r.getRequestURI().getPath().replaceFirst("/", "").split("/");
		String microservice = params[0];

		OutputStream outStream;
		if(allMicroService.containsKey(microservice)){ //if the microserivce is one that we are running we continue
			//Create the request we are sending
			HttpRequest.Builder request = HttpRequest.newBuilder();

			//creating the uri
			String uri = allMicroService.get(microservice) + r.getRequestURI().toString();
			request.uri(URI.create(uri));

			//which method is used for the body
			request.method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofByteArray(r.getRequestBody().readAllBytes()));

			//creating the final request we're going to send
			HttpRequest finalRequest = request.build();

			//send the response
			HttpResponse<byte[]> response = null;
			try {
				response = httpClient.send(finalRequest, HttpResponse.BodyHandlers.ofByteArray());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}//end try catch
			r.sendResponseHeaders(response.statusCode(), response.body().length);
			outStream = r.getResponseBody();
			outStream.write(response.body());

		}//end if
		else { //the microservice is not in our map of microservices so we send 404 error
			String response = "Not Found";
			r.sendResponseHeaders(404, response.length());
			outStream = r.getResponseBody();
			outStream.write(response.getBytes());
		}//end else

		outStream.close();
	}//end
}
