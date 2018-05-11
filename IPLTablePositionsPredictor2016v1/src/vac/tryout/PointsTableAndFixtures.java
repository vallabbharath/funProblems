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
			//Snapshot0: pointsTable.put("SRH",	new PointsTableRow("SRH",12,8,4,0,0,16,+0.400));
			pointsTable.put("GL",	new PointsTableRow("GL",14,9,5,0,0,18,-0.374));
			pointsTable.put("SRH",	new PointsTableRow("SRH",13,8,5,0,0,16,+0.355));
			//Snapshot2: pointsTable.put("GL",	new PointsTableRow("GL",13,8,5,0,0,16,-0.479));
			pointsTable.put("RCB",	new PointsTableRow("RCB",13,7,6,0,0,14,+0.930));
			pointsTable.put("KKR",	new PointsTableRow("KKR",13,7,6,0,0,14,+0.022));
			//Snaphsot2: pointsTable.put("MI",	new PointsTableRow("MI",13,7,6,0,0,14,-0.082));
			pointsTable.put("MI",	new PointsTableRow("MI",14,7,7,0,0,14,-0.146));
			//Snapshot0: pointsTable.put("DD",	new PointsTableRow("DD",12,6,6,0,0,12,-0.125));
			pointsTable.put("DD",	new PointsTableRow("DD",13,7,6,0,0,14,-0.102));
			pointsTable.put("RPS",	new PointsTableRow("RPS",14,5,9,0,0,10,+0.013));
			pointsTable.put("KXP",	new PointsTableRow("KXP",14,4,10,0,0,8,-0.693));
			//Snapshot1: pointsTable.put("RPS",	new PointsTableRow("RPS",13,4,9,0,0,8,+0.013));
			//Snapshot1: pointsTable.put("KXP",	new PointsTableRow("KXP",13,4,9,0,0,8,-0.693));
		}
		remainingMatches = new ArrayList<Pair<String, String>>();
		//Snapshot0: remainingMatches.add(new Pair<String, String>("DD", "SRH"));
		//Snapshot1: remainingMatches.add(new Pair<String, String>("RPS", "KXP"));
		//Snapshot2: remainingMatches.add(new Pair<String, String>("GL", "MI"));
		remainingMatches.add(new Pair<String, String>("KKR", "SRH"));
		remainingMatches.add(new Pair<String, String>("DD", "RCB"));
	}
}
