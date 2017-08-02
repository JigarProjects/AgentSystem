package com.agent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Jigar on 8/1/2017.
 *
 * Standard format for communication between agents
 */
public class Model {
    private JSONObject pingRequest = new JSONObject();

    public Model(String requestor_id, String request){
        pingRequest.put("requestor_id", requestor_id);
        pingRequest.put("message",request);
    }

    public String toString(){
        return pingRequest.toJSONString();
    }

    //Utility method to convert JSON request
    public static JSONObject convertToJSON(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject)parser.parse(jsonString);
    }
}
