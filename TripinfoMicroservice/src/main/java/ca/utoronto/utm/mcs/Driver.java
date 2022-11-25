package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray; 
import org.json.JSONException;
import org.json.JSONObject;

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
        try{
            //First check the URL
            String[] splitUrl = r.getRequestURI().getPath().split("/");
            if (splitUrl.length != 4) { //Going off of the examples I'm guessing this should be 4  ¯\_(ツ)_/¯
                this.sendStatus(r, 400);
                return;
            }

            //Get the uid from our parameters -> (/:uid)
            String uid = splitUrl[3];

            JSONArray result;
            result = this.dao.getUserTrips(uid, "driver");
            if(0 == result.length()){ //If we don't get any user trips return 404 not found
                this.sendStatus(r, 404);
                return;
            }
            JSONObject var = new JSONObject();
            JSONObject trips = new JSONObject();
            trips.put("trips", result);
            var.put("data", trips);
            this.sendResponse(r, var, 200); //200 on success
            return;
        }catch(Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
