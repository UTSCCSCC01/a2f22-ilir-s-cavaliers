package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
         // check if request url isn't malformed
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) { //IDK why its 3 and not 2 but i honestly dont care
			this.sendStatus(r, 400);
			return;
		}

        try{

            //Get the request body 
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);
            String name = null;
            String email = null; 
            String password = null; //initialize to null cause i'm a good little programmer

            //Validate and check the stuff in body
            //Listen ik i didn't check the name like i did email and password but these ifs are long enough man 
            String[] field = {"name", "email", "password"};
            Class<?>[] bodyTypes = {String.class, String.class, String.class};
            if (validateFields(deserialized, field, bodyTypes)) {
                        name = deserialized.getString("name");
                        email = deserialized.getString("email");
                        password = deserialized.getString("password");
                    
                
            }  else {//400 if any fields were wrong or validation fails
                this.sendStatus(r, 400);
                return;
            }

            ResultSet result;
            boolean resultHasNext;
            result = this.dao.getUserDataFromEmail(email);
            resultHasNext = result.next();
            if (!resultHasNext) {
                int uid = this.dao.registerUser(name, email, password);
                // making the response
                JSONObject resp = new JSONObject();
                resp.put("uid", String.valueOf(uid));
                this.sendResponse(r, resp, 200); //200 on success
            } else {
                this.sendStatus(r, 403);
                return;
            }
            
        } catch (Exception e){ 
            e.printStackTrace();
              this.sendStatus(r, 500);
              return;
          }






    }
}
