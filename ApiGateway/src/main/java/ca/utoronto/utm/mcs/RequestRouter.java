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

public class RequestRouter implements HttpHandler {
	
    /**
     * You may add and/or initialize attributes here if you 
     * need.
     */
	public RequestRouter() {

	}

	@Override
	public void handle(HttpExchange r) throws IOException {
        // TODO
	}
}
