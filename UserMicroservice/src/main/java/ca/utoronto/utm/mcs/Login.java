package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO        
    }
}
