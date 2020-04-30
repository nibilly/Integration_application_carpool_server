package object;

import java.util.ArrayList;
import java.util.List;

public class Carpool {
    List<User> usersCarpool = new ArrayList<>();
    String day;
    String heureDepart;
    String heureRetour;
    String dateTotal;


    public void showCarpool() {
        String users = "";
        for(int i = 0; i < usersCarpool.size(); i++){
            users = users + "-" + usersCarpool.get(i).getFirstName();
        }
        System.out.println(users + " can travel together on " + day + " leaving at " + heureDepart + " and retourning home at " + heureRetour);
    }


    public static List<Carpool> findMatches(String date){
        int nbUsers = UserManagement.listUser.size();
        List<Carpool> listCarpool = new ArrayList<>();
        List<List<Event>> listEventsDays = new ArrayList<>();

        for (int i = 0; i < UserManagement.listUser.size(); i++){
            Agenda agenda = UserManagement.listUser.get(i).getMonAgenda();
            List<Event> eventsDayOfUser;
            eventsDayOfUser = agenda.getEventsByDay(date);
            listEventsDays.add(eventsDayOfUser);
        }
        for (int i = 0; i < listEventsDays.size(); i++){
            User user = UserManagement.listUser.get(i);
            for (int j = 0; j < listEventsDays.get(i).size(); j++){
                Event event = listEventsDays.get(i).get(j);
                Carpool carpool = new Carpool(date, event.heurDebut, event.heurFin);
                carpool.usersCarpool.add(user);
                boolean otherUser = false;

                for (int k = i + 1; k < listEventsDays.size(); k ++){
                    for (int l = 0; l < listEventsDays.get(k).size(); l++){
                        Event eventCompare = listEventsDays.get(k).get(l);
                        if (event.heurDebut.equals(eventCompare.heurDebut) && event.heurFin.equals(eventCompare.heurFin)){
                            carpool.usersCarpool.add(UserManagement.listUser.get(k));
                            otherUser = true;
                            listEventsDays.get(k).remove(l);
                            /*listEventsDays.get(k).get(l).setHeureDebut(Float.toString(System.currentTimeMillis()));
                            listEventsDays.get(k).get(l).setHeureFin(Float.toString(System.currentTimeMillis()));*/
                        }
                    }
                }
                if (otherUser){
                    listCarpool.add(carpool);
                }
            }
        }
        return listCarpool;
    }



    public Carpool(String day, String heureDepart, String heureRetour) {
        this.usersCarpool = new ArrayList<>();
        this.day = day;
        this.heureDepart = heureDepart;
        this.heureRetour = heureRetour;
        this.dateTotal = "trajet du " + day + " départ à " + heureDepart + " et retour à " + heureRetour;
    }

    public List<User> getUsersCarpool() {
        return usersCarpool;
    }

    public void setUsersCarpool(List<User> usersCarpool) {
        this.usersCarpool = usersCarpool;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(String heureDepart) {
        this.heureDepart = heureDepart;
    }

    public String getHeureRetour() {
        return heureRetour;
    }

    public void setHeureRetour(String heureRetour) {
        this.heureRetour = heureRetour;
    }

    public String getDateTotal() {
        return dateTotal;
    }

    public void setDateTotal(String dateTotal) {
        this.dateTotal = dateTotal;
    }
}
