package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;
import java.sql.ResultSet;

import org.json.*;

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
        // check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) { //IDK why its 3 and not 2 but i honestly dont care
			this.sendStatus(r, 400);
			return;
		}

        //We're not given paramaters so im not checking :)
        

        //Now try to log in i guess
        try{
            //Get the request body 
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);
            String email = null; 
            String password = null; //initialize to null cause i'm a good little programmer

            //Validate and check the stuff in body
            String[] field = {"email", "password"};
            Class<?>[] bodyTypes = {String.class, String.class};
            if (validateFields(deserialized, field, bodyTypes)) {
                if (deserialized.has("email") && deserialized.has("password")){
                    if(deserialized.get("email").getClass() == String.class 
                    && deserialized.get("password").getClass() == String.class){
                        email = deserialized.getString("email");
                        password = deserialized.getString("password");
                    }
                }
            }  else {
                this.sendStatus(r, 400); //Validation or incorrect entry returns 400
                return;
            }

            //Actually loggin on
            ResultSet result;
            boolean resultHasNext;
            result = this.dao.getUserDataFromEmail(email);
            resultHasNext = result.next();
            if (resultHasNext) {
                // making the response
                JSONObject resp = new JSONObject();
                if (!result.getString("password").equals(password)) {
                    this.sendStatus(r, 401);
                    return;
                }
                resp.put("uid", String.valueOf(result.getInt("uid")));
                this.sendResponse(r, resp, 200);    //Return 200 on success
            } else {
                this.sendStatus(r, 404);    //If user not found return 404
                return;
            }
            
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        //TBH i have no idea how this validate fields business works,
        //But we're gonna himbo our way through this one I guess

        
        
    }
}
