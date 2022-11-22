package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

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
        //obtaining the parameters
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        //checking if they are valid //&& Object.keys(body).length
        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            try {
                //adding the trip to the db
                JSONObject response = this.dao.addTrip(body.getString("driver"), body.getString("passenger"), body.getInt("startTime"));
                this.sendResponse(r, response, 200);
            } catch (Exception e) { //something went wrong
                this.sendStatus(r, 500);
            }//end catch
        } else { //invalid parameters
            this.sendStatus(r, 400);
        }//end else
    }//end handlePost
}
