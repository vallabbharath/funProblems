package vac.tryout;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javafx.util.Pair;

public class TablePositionsPredictor {

    private int scenariosForInputTeamWithoutNrrTie = 0;
    private int scenariosForInputTeamWithNrrTie = 0;
    private String inputTeam = null;
    //private String inputTeam = "RCB";

    public static void main(String args[]) {
        //TablePositionsPredictor predictor = new TablePositionsPredictor();
        //Init PointsTableAndFixtures with default values. TODO: Implement for user input or testcase input.
        PointsTableAndFixtures pointsTableAndFixtures = new PointsTableAndFixtures();

        print("\nInitial Points Table:");
        print(PointsTableRow.titleRow());

        Map<String, PointsTableRow> sortedPointsTable = sortByValue(pointsTableAndFixtures.pointsTable);
        for(PointsTableRow row: sortedPointsTable.values()) {
            print(row.printableRow());
        }
        print("\n");

        TablePositionsPredictor predictor = new TablePositionsPredictor();
        predictor.predict(pointsTableAndFixtures);
    }

    private static void print(String s) {
        //System.out.println(s);
    }

    public HashMap<ArrayList<MatchResult>, TopTeams> matchResultsVsTopTeams;

    TablePositionsPredictor() {
        matchResultsVsTopTeams = new HashMap<ArrayList<MatchResult>, TopTeams>();
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
        matchResultsVsTopTeams.put(matchResults, topTeams);
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

        HashMap<String, Double> teamsVsQualifiedScenariosCountForTop4 = new HashMap<>();
        HashMap<String, Double> teamsVsQualifiedNRRContentionEqualProb = new HashMap<>();
        HashMap<String, Double> teamsVsQualifiedNRRContentionSelcectTopNRRs = new HashMap<>();
        int totalScenarios = matchResultsVsTopTeams.size();

        System.out.println("Total number of scenarios = " + totalScenarios);
        System.out.println("Qualification Scenario for team " + inputTeam + ":");
        int i = 0;
        for(Entry<ArrayList<MatchResult>, TopTeams> entry : matchResultsVsTopTeams.entrySet()) {
            i++;
            TopTeams topTeams = entry.getValue();
            ArrayList<MatchResult> matchResults = entry.getKey();

            for(String team : topTeams.sortedQualifiedTeams) {
                addOneCountToTeam(teamsVsQualifiedScenariosCountForTop4, team);
                addOneCountToTeam(teamsVsQualifiedNRRContentionEqualProb, team);
                addOneCountToTeam(teamsVsQualifiedNRRContentionSelcectTopNRRs, team);
                if(team.equals(inputTeam)) {
                    scenariosForInputTeamWithoutNrrTie++;
                    scenariosForInputTeamWithNrrTie++;
                    printScenario(matchResults, topTeams);
                }
            }

            int contendingTeamsSize = topTeams.sortedContendingTeams.size();
            Integer remainingSlots = 4-topTeams.sortedQualifiedTeams.size();
            for(int idx=0; idx<contendingTeamsSize; ) {
                String team = topTeams.sortedContendingTeams.get(idx);
                addNRRContention(teamsVsQualifiedNRRContentionEqualProb, team, contendingTeamsSize,
                                remainingSlots);
                if(idx < remainingSlots) {
                    addOneCountToTeam(teamsVsQualifiedNRRContentionSelcectTopNRRs, team);
                }
                if(team.equals(inputTeam)) {
                    scenariosForInputTeamWithNrrTie++;
                    printScenario(matchResults, topTeams);
                }
                idx++;
            }
        }
        if(inputTeam != null) {
            System.out.println("Scenarios For Input Team not considering NRR tie = " + inputTeam + " = " + scenariosForInputTeamWithoutNrrTie);
            System.out.println("All Scenarios For Input Team (considering NRR tie) = " + inputTeam + " = " + scenariosForInputTeamWithNrrTie);
        }
        System.out.println("\n\nProbability of qualification not considering teams with equal points (in Pos 4,5, etc): ");
        printPredictions(teamsVsQualifiedScenariosCountForTop4, totalScenarios);
        System.out.println("\n\nProbability of qualification, when qualifying teams with equal points (in Pos 4,5, etc) based on current NRR : ");
        printPredictions(teamsVsQualifiedNRRContentionSelcectTopNRRs, totalScenarios);
        System.out.println("\n\nProbability of qualification, when qualifying teams with equal points (in Pos 4,5, etc) by splitting probability: ");
        System.out.println("i.e, when 2 teams contend for pos 4, consider probability as 0.5, 3 teams for pos 4 as 0.33, 3 teams for pos 3,4 as 0.66 etc");
        printPredictions(teamsVsQualifiedNRRContentionEqualProb, totalScenarios);
    }

    private void printScenario(ArrayList<MatchResult> matchResultsList, TopTeams topTeams) {
        for (MatchResult result : matchResultsList) {
            String team1 = result.match.getKey();
            String team2 = result.match.getValue();
            System.out.println("\t\t" + team1 + " vs " + team2 + ".\t Won by: " + result.winTeam);
        }
        System.out.println("Top Teams are as below : " );
        System.out.println("Sorted Qualified teams : " + topTeams.sortedQualifiedTeams);
        System.out.println("Sorted Contending teams : " + topTeams.sortedContendingTeams);
    }

    private void printPredictions(HashMap<String, Double> teamsVsQualified, int totalScenarios) {
        Set<Entry<String, Double>> set = teamsVsQualified.entrySet();
        List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        for(Map.Entry<String, Double> entry:list) {
            System.out.println("Team " + entry.getKey() + " = " + roundTo3Decimal(entry.getValue(), totalScenarios));
        }
    }

    private String roundTo3Decimal(Double value, int totalScenarios) {
        Double v = value / totalScenarios * 100;
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(v);
    }

    private void addOneCountToTeam(HashMap<String, Double> map, String team) {
        Double count = map.get(team);
        if(count == null) {
            map.put(team, 1d);
        } else {
            map.put(team, count+1d);
        }
    }

    private void addNRRContention(HashMap<String, Double> map, String team, Integer nrrContendingTeams, Integer spots) {
        Double addValue = spots*1d/nrrContendingTeams;
        Double count = map.get(team);
        if(count == null) {
            map.put(team, addValue);
        } else {
            map.put(team, count+addValue);
        }
    }

    //This method shall be used if entire pointsTable needs to be printed for each combination of matchResults.
    private static void print(Map<String, PointsTableRow> pointsTable, ArrayList<MatchResult> matchResults) {

        print("The following sets of results will result in the points table given below.");
        print("\nResults:");

        for(MatchResult result : matchResults) {
            String team1 = result.match.getKey();
            String team2 = result.match.getValue();
            print(team1 + " vs " + team2 + ".\t Won by: " + result.winTeam);
        }

        print("\nPoints Table:");
        print(PointsTableRow.titleRow());

        for(PointsTableRow row: pointsTable.values()) {
            print(row.printableRow());
        }
        print("\n\n");
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

    static class MatchResult {
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

