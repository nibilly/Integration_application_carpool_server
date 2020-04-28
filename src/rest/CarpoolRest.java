package rest;

import javax.ws.rs.*;
import java.io.*;
import java.net.URL;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/")
public class CarpoolRest {

    @GET
    public String getCarpoolByDay(@QueryParam("date") String date){
        String res = "Probleme";

        String command = "curl -X GET http://localhost:8080/Agenda/api/event -H \"Accept:text/json\""; // ' --data foo1=bar1&foo2=bar2'
        try {
            Process process = Runtime.getRuntime().exec(command);
            String jsonString = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))
                    .lines().collect(Collectors.joining("\n"));
            // res = jsonString;
            JSONObject obj = new JSONObject(jsonString);
            String text = obj.getJSONObject("event").getString("text");
            res = text;
            /*JSONArray arr = obj.getJSONArray("posts");
            for (int i = 0; i < arr.length(); i++)
            {
                String post_id = arr.getJSONObject(i).getString("post_id");
            }*/
        } catch (IOException e) {
            res = e.getMessage();
        }
        return res;
    }
}
