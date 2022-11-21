package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
    }
}
