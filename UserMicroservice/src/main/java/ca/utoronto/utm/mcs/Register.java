package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
    }
}
