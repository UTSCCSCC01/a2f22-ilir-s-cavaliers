package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost 
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        //Check if url is fucked
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        try{
            //Find and check trip id
            String tripId = splitUrl[2];
            if (!ObjectId.isValid(tripId)){
                sendStatus(r, 400); //Send 400 on invalid ID
            }

            int distance, endTime, timeElapsed;
            double totalCost;
            String[] fields = {"distance", "endTime", "timeElapsed", "totalCost"};
            Class<?>[] fieldClasses = {Integer.class, Integer.class, Integer.class, String.class};
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if(!this.validateFields(body,fields , fieldClasses)){
                this.sendStatus(r,400); //Send 400 on validation fail
                return;
            }

            distance = body.getInt("distance");
            endTime = body.getInt("endTime");
            timeElapsed = body.getInt("timeElapsed");
            totalCost = body.getDouble("totalCost");

            //try to update trip and store result
            boolean updateResult = this.dao.updateTrip(endTime, totalCost, timeElapsed, distance, new ObjectId(tripId));

            //Update returns false only if trip doesnt exist, so send 404
            if (!updateResult){
                this.sendStatus(r, 404);
                return;
            }

            //otherwise send 200 :)
            this.sendStatus(r, 200);

        }catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
