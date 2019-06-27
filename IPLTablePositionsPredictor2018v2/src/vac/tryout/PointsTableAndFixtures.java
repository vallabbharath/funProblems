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
            pointsTable.put("AUS",	new PointsTableRow("AUS",7,6,1,0,0,14, +0.906));
            pointsTable.put("NZ",	new PointsTableRow("NZ", 7,5,1,0,1,11, +1.028));
            pointsTable.put("IND",	new PointsTableRow("IND", 5,4,0,0,1,9, +0.809));
            pointsTable.put("ENG",	new PointsTableRow("ENG",7,4,3,0,0,8, +1.051));
            pointsTable.put("BD",	new PointsTableRow("BD",7,3,3,0,1,7, -0.133));
            pointsTable.put("PAK",	new PointsTableRow("PAK",7,3,3,0,1, 7, -0.976));
            pointsTable.put("SL",	new PointsTableRow("SL",6,2,2,0,0,6, -1.119));
            pointsTable.put("WI",	new PointsTableRow("WI", 6,1,4,0,1,3, +0.19));
            pointsTable.put("SA",	new PointsTableRow("SA",7,1,5,0,1, 3, -0.324));
            pointsTable.put("AFG",	new PointsTableRow("AFG",7,0,7,0,0,0, -1.634));
        }
        remainingMatches = new ArrayList<Pair<String, String>>();
        remainingMatches.add(new Pair<String, String>("IND","WI"));
        remainingMatches.add(new Pair<String, String>("SA","SL"));

        remainingMatches.add(new Pair<String, String>("AFG","PAK"));
        remainingMatches.add(new Pair<String, String>("AUS","NZ"));
        remainingMatches.add(new Pair<String, String>("ENG","IND"));
        remainingMatches.add(new Pair<String, String>("SL","WI"));
        remainingMatches.add(new Pair<String, String>("BD","IND"));

        remainingMatches.add(new Pair<String, String>("ENG","NZ"));
        remainingMatches.add(new Pair<String, String>("AFG","WI"));
        remainingMatches.add(new Pair<String, String>("BD","PAK"));
        remainingMatches.add(new Pair<String, String>("IND","SL"));
        remainingMatches.add(new Pair<String, String>("AUS","SA"));
    }
}
