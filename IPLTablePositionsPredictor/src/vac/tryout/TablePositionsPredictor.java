package vac.tryout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javafx.util.Pair;

public class TablePositionsPredictor {

	public static void main(String args[]) {
		//TablePositionsPredictor predictor = new TablePositionsPredictor();
		//Init PointsTableAndFixtures with default values. TODO: Implement for user input or testcase input.
		PointsTableAndFixtures pointsTableAndFixtures = new PointsTableAndFixtures(); 
		TablePositionsPredictor.predict(pointsTableAndFixtures);
	}
	
	private static void predict(PointsTableAndFixtures pointsTableAndFixtures) {
		ArrayList<Pair<String, String>> remainingMatches = new ArrayList<>(pointsTableAndFixtures.remainingMatches);
		HashMap<String, PointsTableRow> pointsTable = new HashMap<>(pointsTableAndFixtures.pointsTable);
		
		ArrayList<MatchResult> matchResults = new ArrayList<MatchResult>(pointsTableAndFixtures.remainingNoOfMatches); 
		predict(remainingMatches, pointsTable, matchResults);
	}
	
	private static void predict(ArrayList<Pair<String, String>> remainingMatches, 
			HashMap<String, PointsTableRow> pointsTable, ArrayList<MatchResult> matchResults) {
		
		if(remainingMatches.size() == 0) {
			//Print the result and return.
			
			System.out.println("The following sets of results will result in the points table given below.");
			System.out.println("\nResults:");
			
			for(MatchResult result : matchResults) {
				String team1 = result.match.getKey();
				String team2 = result.match.getValue();
				System.out.println(team1 + " vs " + team2 + ".\t Won by: " + result.winTeam);
			}
			
			System.out.println("\nPoints Table:");
			System.out.println(PointsTableRow.titleRow());
			Map<String, PointsTableRow> sortedPointsTable = sortByValue(pointsTable);
			for(PointsTableRow row: sortedPointsTable.values()) {
				System.out.println(row.printableRow());
			}
			System.out.println("\n\n");
			return;
		}
		
		//Create a copy of remainingMatches, pointsTable and matchResults
		ArrayList<Pair<String, String>> newRemainingMatches = new ArrayList<>(remainingMatches);
		HashMap<String, PointsTableRow> newPointsTable = new HashMap<>(pointsTable);
		ArrayList<MatchResult> newMatchResultsTeam1Wins = new ArrayList<MatchResult>(matchResults);
		ArrayList<MatchResult> newMatchResultsTeam2Wins = new ArrayList<MatchResult>(matchResults);
		
		//Remove the first match in remainingMatches. Get corresponding tableRows for team1 and team2.
		Pair<String,String> match = newRemainingMatches.remove(0);
		String team1 = match.getKey();
		String team2 = match.getValue();
		PointsTableRow team1TableRow = newPointsTable.get(team1);
		PointsTableRow team2TableRow = newPointsTable.get(team2);
		
		//If team1WonMatch
		PointsTableRow team1TableRowAfterWin = addWin(team1TableRow);
		PointsTableRow team2TableRowAfterLoss = addLoss(team2TableRow);
		newPointsTable.put(team1, team1TableRowAfterWin);
		newPointsTable.put(team2, team2TableRowAfterLoss);
		newMatchResultsTeam1Wins.add(new MatchResult(match, team1));
		predict(newRemainingMatches, newPointsTable, newMatchResultsTeam1Wins);
		
		//If team2WonMatch
		PointsTableRow team1TableRowAfterLoss = addLoss(team1TableRow);
		PointsTableRow team2TableRowAfterWin = addWin(team2TableRow);
		newPointsTable.put(team1, team1TableRowAfterLoss);
		newPointsTable.put(team2, team2TableRowAfterWin);
		newMatchResultsTeam2Wins.add(new MatchResult(match, team2));
		predict(newRemainingMatches, newPointsTable, newMatchResultsTeam2Wins);
	}

	private static PointsTableRow addLoss(PointsTableRow tableRow) {
		PointsTableRow newTableRow = new PointsTableRow(tableRow);
		newTableRow.matches = newTableRow.matches + 1;
		newTableRow.lost = newTableRow.lost + 1;
		return newTableRow;
	}

	private static PointsTableRow addWin(PointsTableRow tableRow) {
		PointsTableRow newTableRow = new PointsTableRow(tableRow);
		newTableRow.matches = newTableRow.matches + 1;
		newTableRow.won = newTableRow.won + 1;
		newTableRow.points = newTableRow.points + 2;
		return newTableRow;
	}
	
	private static class MatchResult {
		Pair<String, String> match;
		String winTeam;
		
		public MatchResult(Pair<String, String> match, String winTeam) {
			this.match = match;
			this.winTeam = winTeam;
		}
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
	{
	    Map<K, V> result = new LinkedHashMap<>();
	    Stream<Map.Entry<K, V>> st = map.entrySet().stream();
	
	    st.sorted( Map.Entry.comparingByValue() )
	        .forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );
	
	    return result;
	}
}

