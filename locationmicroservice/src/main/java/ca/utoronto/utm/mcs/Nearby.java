package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {

        //Get the parameters
        String[] allParameters = r.getRequestURI().toString().split("/");
        if (allParameters.length != 4 || allParameters[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }//end if

        try {
            String [] parameters = new String[2];
            try{
                parameters = allParameters[3].split("\\?radius=");
            }
            catch (Exception e){
                e.printStackTrace();
                this.sendStatus(r, 400);
            }//end catch

            //If time check that passenger is a passenger and not a driver
            String passenger = parameters[0];
            int radius = Integer.parseInt(parameters[1]);

            System.out.println(radius + passenger);

            //if the radius is invalid
            if (radius < 0 ){
                this.sendStatus(r, 400);
                return;
            }//end if

            Result passengerLocationRes = this.dao.getUserLocationByUid(passenger);
            if (!passengerLocationRes.hasNext()){
                this.sendStatus(r, 404);
                return;
            }//end if

            Result driversRes = this.dao.getDriverWithinRadius(passenger, (double) radius);
            if (!driversRes.hasNext()){ //if there are no drivers we cannot find them
                this.sendStatus(r, 404);
                return;
            }//end if

            JSONObject data = new JSONObject();
            while(driversRes.hasNext()){ //add all the drivers to our result
                Value curDriver = driversRes.next().get(0);
                JSONObject curDriverJSON = new JSONObject();

                //adding info to our JSON object
                curDriverJSON.put("longitude", curDriver.get("longitude").asFloat());
                curDriverJSON.put("latitude", curDriver.get("latitude").asFloat());
                curDriverJSON.put("street", curDriver.get("street").asString());
                data.put(curDriver.get("uid").asString(), curDriverJSON);
            }//end while

            JSONObject result = new JSONObject();
            result.put("data", data);
            result.put("status", "OK");
            this.sendResponse(r, result, 200);
        }catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }//end catch
    }
}
