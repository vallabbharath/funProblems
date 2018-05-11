package vac.tryout;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;

public class PointsTableAndFixtures {

    public int numberOfTeams;
    //public ArrayList<String> teams;
    public HashMap<String, PointsTableRow> pointsTable;
    public ArrayList<Pair<String, String>> remainingMatches;

    public PointsTableAndFixtures(int numberOfTeams, HashMap<String, PointsTableRow> pointsTable,
                    int remainingNoOfMatches, ArrayList<Pair<String, String>> remainingMatches) {
        this.numberOfTeams = numberOfTeams;
        this.pointsTable = new HashMap<>(pointsTable);
        this.remainingMatches = new ArrayList<>(remainingMatches);
    }

    public PointsTableAndFixtures() {
        numberOfTeams = 8;
        pointsTable = new HashMap<String, PointsTableRow>(numberOfTeams);
        {
            pointsTable.put("SRH",	new PointsTableRow("SRH",11,9,2,0,0,18, +0.473));
            pointsTable.put("CSK",	new PointsTableRow("CSK",10,7,3,0,0,14, +0.421));
            pointsTable.put("KXP",	new PointsTableRow("KXP",10,6,4,0,0,12, +0.097));
            pointsTable.put("MI",	new PointsTableRow("MI", 11,5,6,0,0,10, +0.529));
            pointsTable.put("KKR",	new PointsTableRow("KKR",11,5,6,0,0,10, -0.359));
            pointsTable.put("RR",	new PointsTableRow("RR", 10,4,6,0,0, 8, -0.552));
            pointsTable.put("RCB",	new PointsTableRow("RCB",10,3,7,0,0, 6, -0.361));
            pointsTable.put("DD",	new PointsTableRow("DD", 11,3,8,0,0, 6, -0.447));
        }
        remainingMatches = new ArrayList<Pair<String, String>>();
        remainingMatches.add(new Pair<String, String>("RR","CSK"));
        remainingMatches.add(new Pair<String, String>("KXP","KKR"));
        remainingMatches.add(new Pair<String, String>("DD","RCB"));
        remainingMatches.add(new Pair<String, String>("CSK","SRH"));
        remainingMatches.add(new Pair<String, String>("MI","RR"));
        remainingMatches.add(new Pair<String, String>("KXP","RCB"));
        remainingMatches.add(new Pair<String, String>("KKR","RR"));
        remainingMatches.add(new Pair<String, String>("MI","KXP"));
        remainingMatches.add(new Pair<String, String>("RCB","SRH"));
        remainingMatches.add(new Pair<String, String>("DD","CSK"));
        remainingMatches.add(new Pair<String, String>("RR","RCB"));
        remainingMatches.add(new Pair<String, String>("SRH","KKR"));
        remainingMatches.add(new Pair<String, String>("DD","MI"));
        remainingMatches.add(new Pair<String, String>("CSK","KXP"));
    }
}
