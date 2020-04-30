package rest;

import javax.ws.rs.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import object.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/")
public class CarpoolRest {
    private int getUserId(String firstName, String lastName){
        int res = -1;
        UserManagement userManagement = new UserManagement();
        User user = userManagement.getUsers().stream()
                .filter(user1 -> firstName.equals(user1.getFirstName()) && lastName.equals(user1.getLastName()))
                .findAny()
                .orElse(null);
        if (user != null){
            res = user.getId();
        }
        return res;
    }

    private void insertNewEvent(String startDate, String finishDate, String name, int userId){
        Agenda agenda = UserManagement.getUserById(userId).getMonAgenda();
        startDate = startDate.toUpperCase();
        finishDate = finishDate.toUpperCase();
        String finalStartDate = startDate;
        String finalFinishDate = finishDate;
        if(agenda.getEvents().stream().noneMatch(event -> (event.getStartDate().compareTo(finalStartDate) == 0)
                && (event.getFinishDate().compareTo(finalFinishDate) == 0) && (event.getName().compareTo(name) == 0))){
            String jourDate = startDate.substring(0, 10);
            String heureDebut = startDate.substring(11, 16);
            String heureFin = finishDate.substring(11, 16);
            agenda.newEvent(new Event(startDate, finishDate, jourDate, heureDebut, heureFin, name));
        }
    }

    private void createUsers(String date){
        String command = "curl -X GET http://localhost:8080/Agenda/api/user/event/day?date="+ date +" -H \"Accept:application/json\""; // ' --data foo1=bar1&foo2=bar2'
        try {
            Process process = Runtime.getRuntime().exec(command);
            String jsonString = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject obj = new JSONObject(jsonString);
            JSONArray users = obj.getJSONArray("users");
            for (int i = 0; i < users.length(); i++){
                JSONObject userJson = users.getJSONObject(i);
                JSONObject userDetailJson = userJson.getJSONObject("user");
                String firstName = userDetailJson.getString("firstName");
                String lastName = userDetailJson.getString("lastName");
                if(getUserId(firstName, lastName) == -1) {
                    UserManagement.createUser(firstName, lastName);
                }
                int userId = getUserId(firstName, lastName);
                JSONArray events = userJson.getJSONArray("events");
                for (int j = 0; j < events.length(); j++){
                    JSONObject event = events.getJSONObject(j);
                    String startDate = event.getString("startDate");
                    String finishDate = event.getString("finishDate");
                    String name = event.getString("name");
                    insertNewEvent(startDate, finishDate, name, userId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // mission abort : infinite loop or synchrone request which block the program.
    private void sendMessages(Carpool carpool) {
        /*int adminId = 5;
        for(User user: carpool.getUsersCarpool()) {
            List<User> usersExept = carpool.getUsersCarpool().stream()
                    .filter(user1 -> user1.getId() != user.getId()).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            for(User user1: usersExept){
                stringBuilder.append(user1.getFirstName()).append(" ").append(user1.getLastName()).append(" ");
            }
            String content = stringBuilder.toString()+"peut/peuvent faire du covoiturage avec toi le "+carpool.getDay()+" en partant à " + carpool.getHeureDepart() + " et revenant à " + carpool.getHeureRetour();
            String command = "curl -X GET http://localhost:8080/Message/api/user/" + adminId + "/message --data receiverFirstName="+user.getFirstName()+"&receiverLastName="+user.getLastName() +"&content="+content; // ' --data foo1=bar1&foo2=bar2'
            try {
                Process process = Runtime.getRuntime().exec(command);
                String result = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))
                        .lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @GET
    public String getCarpoolByDay(@QueryParam("date") String date){
        StringBuilder stringBuilder = new StringBuilder();
        createUsers(date);
        List<Carpool> carpools = Carpool.findMatches(date.substring(0,10));
        for(Carpool carpool: carpools) {
            StringBuilder stringBuilder1 = new StringBuilder();
            for (int i = 0; i < carpool.getUsersCarpool().size(); i++) {
                stringBuilder1.append("-").append(carpool.getUsersCarpool().get(i).getFirstName());
            }
            stringBuilder.append(stringBuilder1.toString()).append(" peuvent voyager ensemble le ").append(carpool.getDay())
                    .append(" en partant à ").append(carpool.getHeureDepart()).append(" et revenant à ")
                    .append(carpool.getHeureRetour()).append("<br>");
            sendMessages(carpool);
        }
        return stringBuilder.toString();
    }
}
