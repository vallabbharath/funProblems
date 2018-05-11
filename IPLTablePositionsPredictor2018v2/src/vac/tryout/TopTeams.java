package vac.tryout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains all the top teams that has equal or greater points than the team numbered 4 in points table.
 * This class's hashCode() is aimed at returning the same value for two or more same sets of final top teams in same order.
 * eg: {A-18, E-18, H-18, F-14, C-14} and {A-18, H-18, E-16, F-14, C-14} should return same value for hashCode(), 
 * because A, E, H are qualified and F and C are fighting for the 4th spot in both cases.
 * 
 * The way I intend to achieve this is by calling hashCode() method of final string arrived by 
 * appending strings of ${sorted list by alphabetical order in qualified teams} followed by 
 * strings of ${sorted list by alphabetical order in contending teams}.
 * Eg: Both of the above example provides same hashcode because both will call "AEHCF".hashCode();
 * 
 * 
 * @author Vallab
 *
 */
public class TopTeams {
	
	public ArrayList<String> sortedQualifiedTeams;
	public ArrayList<String> sortedContendingTeams;

	public TopTeams(ArrayList<String> qualifiedTeams, ArrayList<String> contendingTeams) { //, HashMap<String,Integer> teamVsPoints) {
		this.sortedQualifiedTeams = new ArrayList<String>(qualifiedTeams);
		this.sortedContendingTeams = new ArrayList<String>(contendingTeams);
	}
	
	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		
		for(String team : sortedQualifiedTeams) {
			/*Appending space between teams to adhere to safety of Huffman-like coding,
			 * i.e, two or more different combinations will not result in same string.
			 * eg: team1: ABC, team2: DEF, team3: AB, team4: CDEF
			 * here ${team1+team2} and ${team3+team4} results in same output "ABCDEF" and hence won't work well without the space.  */
			sb.append(team + " ");
		}
		sb.append(" ");
		for(String team : sortedContendingTeams) {
			sb.append(team + " ");
		}
		String str = sb.toString();
		/*System.out.println("TopTeams String : " + str);
		System.out.println("HashCode : " + str.hashCode());
		*/
		return str.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj){
		
		if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TopTeams other = (TopTeams) obj;
        if(this.hashCode() == other.hashCode()) {
        	return true;
        }
		
		return false;
	}
}
