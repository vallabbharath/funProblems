IPLTablePositionsPredictor is aimed at predicting the final table standings, given only few IPL matches are remaining :) :)

To run and get results, run main method from TablePositionsPredictor.java

If you want to change the pointsTable or remainingMatches, or teams involved, change it in constructor of PointsTableAndFixtures.java.

Alternatively you could use the overridden constructor of PointsTableAndFixtures(int, Hashmap, int, ArrayList) to pass the data from main method of TablePositionsPredictor.java or add some logic to parse the data from a file or console input.



Input/Output:
The input folder contains the list of input Points table and matches to go. 
To Run a particular input, copy that file from input folder (eg: input/PointsTableAndFixtures.java.bkp_2ToGo) and paste it as src/vac/tryout/PointsTableAndFixtures.java and run your program.