package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;

public class Navigation extends Endpoint {

    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     *
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
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
                parameters = allParameters[3].split("\\?passengerUid=");
            }
            catch (Exception e){
                e.printStackTrace();
                this.sendStatus(r, 400);
            }//end catch
            String passenger_uid = parameters[1];
            String driver_uid = parameters[0]; //need to check that the driver UID is of a driver

            String passengerLocation, driverLocation;

            Result passengerResult = this.dao.getUserLocationByUid(passenger_uid);
            Result driverResult = this.dao.getUserLocationByUid(driver_uid);
            if (passengerResult.hasNext() && driverResult.hasNext()) {
                passengerLocation = passengerResult.next().get("n.street").asString();
                driverLocation = driverResult.next().get("n.street").asString();
            }//end if
            else { //the result was not found, we did not find the user(s)
                this.sendStatus(r, 404);
                return;
            }//end else

            //check that the location is available
            if (passengerLocation == null || driverLocation == null) {
                this.sendStatus(r, 404);
                return;
            }//end if

            //Get the shortest path
            List<Record> shortestPathRes = this.dao.getShortestPath(passengerLocation, driverLocation).list();
            //check if we got results
            if (shortestPathRes == null || shortestPathRes.isEmpty()) {
                this.sendStatus(r, 404);
                return;
            }//end if

            //format total time received
            Double total_cost = shortestPathRes.get(0).get(0).asDouble();
            List<?> costs = shortestPathRes.get(0).get(1).asList();
            List<Double> eachCost = new ArrayList<>();
            for (Object cost : costs) { //for each cost we add it to our list
                eachCost.add((double) cost);
            }//end for

            Value path = shortestPathRes.get(0).get(2);
            JSONArray fullPath = new JSONArray();
            Double previousCost = 0.0;

            //for each node in the path add to our JSONArray
            for (int i = 0; i < path.asList().size(); i++) {
                JSONObject currentPathLocation = new JSONObject();
                currentPathLocation.put("street", path.get(i).get("name").asString());
                currentPathLocation.put("time", eachCost.get(i) - previousCost);
                currentPathLocation.put("has_traffic", path.get(i).get("has_traffic").asString());

                fullPath.put(currentPathLocation);
                previousCost = eachCost.get(i);
            }//end for

            //Create our result which we're sending
            JSONObject result = new JSONObject();
            JSONObject data = new JSONObject();

            data.put("total_time", total_cost);
            data.put("route", fullPath);
            result.put("data", data);
            result.put("status", "OK");

            this.sendResponse(r, result, 200);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }//end catch
    }
}
