package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        // TODO
    }
}
