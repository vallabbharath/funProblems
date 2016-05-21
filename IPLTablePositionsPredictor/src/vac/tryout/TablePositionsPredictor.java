package vac.tryout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javafx.util.Pair;

public class TablePositionsPredictor {
	
	public static void main(String args[]) {
		//TablePositionsPredictor predictor = new TablePositionsPredictor();
		//Init PointsTableAndFixtures with default values. TODO: Implement for user input or testcase input.
		PointsTableAndFixtures pointsTableAndFixtures = new PointsTableAndFixtures();
		
		System.out.println("\nInitial Points Table:");
		System.out.println(PointsTableRow.titleRow());
		
		Map<String, PointsTableRow> sortedPointsTable = sortByValue(pointsTableAndFixtures.pointsTable);
		for(PointsTableRow row: sortedPointsTable.values()) {
			System.out.println(row.printableRow());
		}
		System.out.println("\n");
		
		TablePositionsPredictor predictor = new TablePositionsPredictor();
		predictor.predict(pointsTableAndFixtures);
	}
	public HashMap<TopTeams, ArrayList<ArrayList<MatchResult>>> topTeamsVsMatchResults;
	
	TablePositionsPredictor() {
		topTeamsVsMatchResults = new HashMap<TopTeams, ArrayList<ArrayList<MatchResult>>>();
	}
	
	private void predict(PointsTableAndFixtures pointsTableAndFixtures) {
		ArrayList<Pair<String, String>> remainingMatches = new ArrayList<>(pointsTableAndFixtures.remainingMatches);
		HashMap<String, PointsTableRow> pointsTable = new HashMap<>(pointsTableAndFixtures.pointsTable);
		
		ArrayList<MatchResult> matchResults = new ArrayList<MatchResult>(remainingMatches.size()); 
		predict(remainingMatches, pointsTable, matchResults);
		printConsolidated();
	}
	
	private void predict(ArrayList<Pair<String, String>> remainingMatches, 
			HashMap<String, PointsTableRow> pointsTable, ArrayList<MatchResult> matchResults) {
		
		if(remainingMatches.size() == 0) {
			//Print the result and return.
			
			Map<String, PointsTableRow> sortedPointsTable = sortByValue(pointsTable);
			saveConsolidatedResults(sortedPointsTable, matchResults);
			
			print(sortedPointsTable, matchResults);
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

	
	private void saveConsolidatedResults(
			Map<String, PointsTableRow> sortedPointsTable,
			ArrayList<MatchResult> matchResults) {
		
		TopTeams topTeams = constructTopTeams(sortedPointsTable);
		ArrayList<ArrayList<MatchResult>> listOfMatchResults = topTeamsVsMatchResults.get(topTeams);
		if(listOfMatchResults == null) {
			listOfMatchResults = new ArrayList<ArrayList<MatchResult>>();
		}
		listOfMatchResults.add(matchResults);
		topTeamsVsMatchResults.put(topTeams, listOfMatchResults);
	}
	
	private static TopTeams constructTopTeams(
			Map<String, PointsTableRow> sortedPointsTable) {
		ArrayList<String> qualifiedTeams = new ArrayList<String>();
		ArrayList<String> contendingTeams = new ArrayList<String>();
		ArrayList<String> teams = new ArrayList<String>();
		
		int count = 0;
		int pointsOfPrevTeam = -1;
		for(PointsTableRow tableRow : sortedPointsTable.values()) {
			count++;
			int points = tableRow.points;
			String team = tableRow.team;
			
			if(count < 5) {
				if(pointsOfPrevTeam == points) {
					teams.add(team);
				} else {
					qualifiedTeams.addAll(teams);
					pointsOfPrevTeam = points;
					teams.clear();
					teams.add(team);
				}
			} else if(pointsOfPrevTeam == points) {
				teams.add(team);
			} else {
				if(count == 5) {
					qualifiedTeams.addAll(teams);
				} else {
					contendingTeams.addAll(teams);
				}
				teams.clear();
				teams = null;
				break;
			}
		}
		
		return new TopTeams(qualifiedTeams, contendingTeams);
	}

	private void printConsolidated() {
		
		int i = 0;
		for(Entry<TopTeams, ArrayList<ArrayList<MatchResult>>> entry : topTeamsVsMatchResults.entrySet()) {
			i++;
			TopTeams topTeams = entry.getKey();
			ArrayList<ArrayList<MatchResult>> listOfMatchResults = entry.getValue();
			System.out.println("\n");
			
			int j = 1;
			for(ArrayList<MatchResult> matchResults : listOfMatchResults) {
				System.out.println("Qualification Scenario " + i +": MatchResults Set " + j++ + ":");
				for(MatchResult result : matchResults) {
					String team1 = result.match.getKey();
					String team2 = result.match.getValue();
					System.out.println("\t\t" + team1 + " vs " + team2 + ".\t Won by: " + result.winTeam);
				}
			}
			
			System.out.println("\nQualification Scenario " + i + ":");
			System.out.println("The following teams are qualified :");
			System.out.print("\t\t");
			for(String team : topTeams.sortedQualifiedTeams) {
				System.out.print(team + ",");
			}
			System.out.println("\nThe following teams contend to enter top 4 based on NRR :");
			System.out.print("\t\t");
			for(String team : topTeams.sortedContendingTeams) {
				System.out.print(team + ",");
			}
		}
	}

	//This method shall be used if entire pointsTable needs to be printed for each combination of matchResults. 
	private static void print(
			Map<String, PointsTableRow> pointsTable,
			ArrayList<MatchResult> matchResults) {
		
		System.out.println("The following sets of results will result in the points table given below.");
		System.out.println("\nResults:");
		
		for(MatchResult result : matchResults) {
			String team1 = result.match.getKey();
			String team2 = result.match.getValue();
			System.out.println(team1 + " vs " + team2 + ".\t Won by: " + result.winTeam);
		}
		
		System.out.println("\nPoints Table:");
		System.out.println(PointsTableRow.titleRow());
		
		for(PointsTableRow row: pointsTable.values()) {
			System.out.println(row.printableRow());
		}
		System.out.println("\n\n");		
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

