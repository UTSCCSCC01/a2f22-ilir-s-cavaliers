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
        try {
            String endpoint = r.getRequestURI().toString();
            String[] params = endpoint.split("/");

            if (params.length != 4) { //not the correct endpoint
                this.sendStatus(r, 400);

            } else {
                String[] allParams = params[3].split("\\?");

                if (allParams.length != 2) { //not correct number of params
                    this.sendStatus(r, 400);
                } else {
                    //getting the params
                    String driverUid = allParams[0];
                    String payload = allParams[1];

                    String[] uidParam = payload.split("=");

                    if (uidParam.length != 2 || !uidParam[0].equals("passengerUid")) { //wrongly formated params
                        this.sendStatus(r, 400);
                    } else {

                        String passengerUid = uidParam[1];

                        //getting the locations of our users
                        Result driverRes = this.dao.getUserLocationByUid(driverUid);
                        Result passengerRes = this.dao.getUserLocationByUid(passengerUid);
                        if (driverRes.hasNext() && passengerRes.hasNext()) {
                            Record driver = driverRes.next();
                            String driverLocation = driver.get("n.street").toString().replace("\"", "");
                            Record passenger = passengerRes.next();
                            String passengerLocation = passenger.get("n.street").toString().replace("\"", "");

                            //Using our DAO method to find the shortest path
                            Result pathResult = this.dao.getShortestPath(driverLocation, passengerLocation);
                            if (pathResult.hasNext()) {
                                Record pathRes = pathResult.next();

                                //Getting all the values from the Query results
                                Value streets = pathRes.get("nodes");
                                Value hasTraffic = pathRes.get("nodesTraffic");
                                Value travelTimes = pathRes.get("costs");
                                int totalTime = pathRes.get("totalCost").asInt();
                                Value routes = pathRes.get("relationships(path)");

                                JSONObject status = new JSONObject();
                                JSONObject body = new JSONObject();

                                //Creating our data object
                                ArrayList<JSONObject> fullPath = new ArrayList<>();

                                //Adding the first route since the time should be 0 for it
                                JSONObject route1 = new JSONObject();
                                route1.put("street", streets.get(0).toString().replace("\"", ""));
                                route1.put("has_traffic", hasTraffic.get(0).asBoolean());
                                route1.put("time", 0);
                                fullPath.add(route1);

                                //Loop through the rest of the data and add to our fullPath object
                                for (int i = 0; i < routes.size(); i++) {
                                    JSONObject bodyData = new JSONObject();
                                    bodyData.put("street", streets.get(i + 1).toString().replace("\"", ""));
                                    bodyData.put("has_traffic", hasTraffic.get(i + 1).asBoolean());
                                    bodyData.put("time", routes.get(i).asRelationship().get("cost").asInt());
                                    fullPath.add(bodyData);
                                }//end for

                                body.put("total_time", totalTime);
                                body.put("route", fullPath);

                                status.put("status", errorMap.get(200));
                                status.put("data", body);

                                sendResponse(r, status, 200);
                            } else {
                                sendStatus(r, 404);
                            }//end else
                        } else {
                            sendStatus(r, 404);
                        }//end else

                    }//end else

                }//end else

            }//end else

        } catch (Exception e) {
            sendStatus(r, 500);
        }//end try catch

    }//end handleGet method

}//end Navigation class
