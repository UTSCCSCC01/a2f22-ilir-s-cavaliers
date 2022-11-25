package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        
        //Send 400 on bad url
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4){
            this.sendStatus(r, 400);
            return;
        }

        try{
            //Get user id
            String uid = splitUrl[3];
            //Store all passenger trips in array
            JSONArray pTrips = this.dao.getUserTrips(uid, "passenger");
            //If no passengers found, (empty array), send 404
            if (0 == pTrips.length()){
                this.sendStatus(r, 404);
                return;
            }

            JSONObject var = new JSONObject();
            JSONObject trips = new JSONObject();
            trips.put("trips", pTrips);
            var.put("data", trips);
            this.sendResponse(r, var, 200);
         }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
         }
         
    }
    
}
