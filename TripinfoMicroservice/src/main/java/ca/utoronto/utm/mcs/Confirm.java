package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
    }
}
