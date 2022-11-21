package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
    }
}
