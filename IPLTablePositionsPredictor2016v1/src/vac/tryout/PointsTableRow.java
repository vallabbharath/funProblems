package vac.tryout;

public class PointsTableRow implements Comparable<PointsTableRow> {
	
	public String team;
	public int matches;
	public int won; 
	public int lost; 
	public int tied;
	public int noResult;
	public int points;
	public double netRR;
	//TEAMS	MAT	WON	LOST	TIED	N/R	PTS	NET RR	
	public PointsTableRow(String team, int matches, int won, int lost, int tied,
			int noResult, int points, double netRR) {
		this.team = team;
		this.matches = matches;
		this.won = won;
		this.lost =lost;
		this.tied = tied;
		this.noResult = noResult;
		this.points = points;
		this.netRR = netRR;
	}
	
	public PointsTableRow(PointsTableRow tableRow) {
		this.team = tableRow.team;
		this.matches = tableRow.matches;
		this.won = tableRow.won;
		this.lost = tableRow.lost;
		this.tied = tableRow.tied;
		this.noResult = tableRow.noResult;
		this.points = tableRow.points;
		this.netRR = tableRow.netRR;
	}
	
	@Override
	public int compareTo(PointsTableRow o) {
		//This comparator intends to sort in reverse. Hence will reverse the values of +1 and -1
		
		if(this.points > o.points) {
			return -1;
		} else if(this.points < o.points) {
			return 1;
		} else if(this.netRR >= o.netRR) {
			return -1;
		}
		return 1;
	}

	public static String titleRow() {
		StringBuilder sb = new StringBuilder("Team").append("\t")
								.append("Mat").append("\t")
								.append("Won").append("\t")
								.append("Lost").append("\t")
								.append("Tied").append("\t")
								.append("NR").append("\t")
								.append("Pts").append("\t")
								.append("NRR");
		return sb.toString();
	}
	
	public String printableRow() {
		StringBuilder sb = new StringBuilder(this.team).append("\t\t")
								.append(this.matches).append("\t")
								.append(this.won).append("\t")
								.append(this.lost).append("\t\t")
								.append(this.tied).append("\t\t")
								.append(this.noResult).append("\t")
								.append(this.points).append("\t")
								.append(this.netRR);
		return sb.toString();
	}
}
