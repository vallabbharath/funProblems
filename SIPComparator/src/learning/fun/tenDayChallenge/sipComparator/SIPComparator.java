package learning.fun.tenDayChallenge.sipComparator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("unused")
public class SIPComparator {
	
	private static String baseUrl = "http://portal.amfiindia.com/NavHistoryReport_Rpt_Po.aspx?rpt=1&frmdate=";
	
	private static HashMap<Date, HistoricNAV> readData(String frmdate, String todate, String mf,
			String scheme, String dateFormat, StringWrapper maxDate) {
		
		HashMap<Date, HistoricNAV> returnMap = new HashMap<Date, HistoricNAV>();
		
		String urlString = baseUrl + frmdate + "&todate=" + todate + 
							"&mf=" + mf + "&scm=" + scheme;
		
		URL url;
		try {
			url = new URL(urlString);
//			System.out.println(urlString);
	        URLConnection yc = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    yc.getInputStream()));
	        String inputLine;
	
	        //Read comments inside while loop, before reading the comment below.
	        //variable denoting which line after "labelNAlter" we are currently under.
	        //0 denotes we have read all 4 lines of data after previous "labelNAlter" or we are yet to reach first "labelNAlter" 
	        int dataPosition = 0;
	        Double nav = null;
	        Double repurchasePrice = null;
	        Double salePrice = null;
	        Date date = null;
	        while ((inputLine = in.readLine()) != null) {
	        	//logic to parse data and create Objects based on output from url.
	        	//Logic used here is, 4 lines after each "labelNAlter" div contains data we want.
	        	
	        	if(dataPosition == 0){
	        		if(inputLine.contains("labelNAlter")) {
        				dataPosition++;
    	        	}
	        		continue;
	        	}
	        	
	        	String requiredString =inputLine.substring(inputLine.indexOf(">")+1,inputLine.lastIndexOf("<"));
	        	switch(dataPosition) {
	        		case 1 : 
	        			nav = new Double(requiredString);
	        			break;
	        		case 2 :
	        			repurchasePrice = new Double(requiredString);
	        			break;
	        		case 3 :
	        			salePrice = new Double(requiredString);
	        			break;
	        		case 4 :
	        			date = DateUtil.getDate(requiredString, dateFormat);
	        			//Not doing date comparison, as data is always in ascending order w.r.t date
	        			maxDate.maxDate = requiredString;
	        			
	        			HistoricNAV historicNAV = new HistoricNAV(nav, repurchasePrice, salePrice, date);
	        			returnMap.put(date, historicNAV);
	        			
	        			//reset values back to null.
	        			nav = null;
	        			repurchasePrice = null;
	        			salePrice = null;
	        			date = null;
	        			break;
	        		default :
	        			throw new RuntimeException("blah. Something went wrong!");
	        	}
	        	dataPosition++;
	        	dataPosition = dataPosition % 5;
	        }
	        in.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	//frequencyMode can be one of the following.
	//'d' - days. 'w' - weeks. 'm' - months
	private static void calculate(Double sipAmount, String startDate, int frequency, char frequencyMode,
									String mf, String scheme, String redemptionDate, String dateFormat) throws ParseException {
		
		StringWrapper maxDate = new StringWrapper();
		HashMap<Date,HistoricNAV> historicData = readData(startDate, redemptionDate, mf, scheme, "dd-MMM-yyyy", maxDate);
		
		Date redemptDate = DateUtil.getDate(redemptionDate, dateFormat);
		Date processingDate = DateUtil.getDate(startDate, dateFormat);
		Date maxDateInData = DateUtil.getDate(maxDate.maxDate, dateFormat);
		//System.out.println("Max date in data = " + maxDateInData);
		
		if(maxDateInData.compareTo(redemptDate) != 0) {
			//For now throw exception saying Redemption Date is either holiday or future date.
			//TODO @Vallab. Later add logic to try for next 4 or 5 consecutive days 
						//to get next-immediate work-day, and reset redemption date to that day .
			//TODO @Vallab. Add such extra date processing, again to historicData hashmap. 
			throw new RuntimeException("Redemption Date entered is either a holiday or future date!");
		}
		if(maxDateInData.compareTo(processingDate) < 0) {
			throw new RuntimeException("Start Date entered is either a future date, or invalid period selected for the scheme!");
		}
		
		double totalUnitsPurchased = 0;
		double totalInvestedAmount = 0;
		//InvestedDays + amountsInvested will be used later to calculate the IRR.
		List<Integer> investedDays = new ArrayList<Integer>();
		
		//No need of below maintenance of amountsInvested in list, as it is always same const value in case of SIP.  
		//List<Double> amountsInvested = new ArrayList<Double>();
		
		while(processingDate.compareTo(redemptDate) <= 0) {
			switch(frequencyMode) {
				case 'd' :
					HistoricNAV historicNAV = historicData.get(processingDate);
					
					int count = 0;
					//Handled logic for getting next work-day if processingDate is holiday.
					while(historicNAV == null) {
						Date newDate = DateUtil.addDays(processingDate, 1);
						historicNAV = historicData.get(newDate);
						count++;
						
						if(count > 5) {
							throw new RuntimeException("Oopsieee.. Can't get the data you requested..! Check if the mf existed on the dates you mentioned!");
						}
					}
					
					// Now we have historicNAV and the invested date. 
					// amount of units purchased = amountInvested / historicNAV.saleprice.
					double salesPrice = historicNAV.getSalePrice();
					if(salesPrice > 0 ) {
						double unitsPurchased = sipAmount/salesPrice;
						totalUnitsPurchased += unitsPurchased;
						int dateDiff = DateUtil.getDateDiff(processingDate, redemptDate);
						investedDays.add(dateDiff);
						totalInvestedAmount += sipAmount;
					}
					processingDate = DateUtil.addDays(processingDate, frequency);
					break;
				case 'w' :
					frequencyMode = 'd';
					frequency = frequency*7;
					break;
				case 'm' :
					//Currently hard-coding monthly investment as equivalent of investing every 30 days. 
					//will do enhancement later. #TODO VALLAB. 
					frequencyMode = 'd';
					frequency = frequency*30;
					break;
				default :
					throw new RuntimeException("Operation not supported!");
			}
		}
		
		HistoricNAV redemptionDateNAV = historicData.get(redemptDate);
		
		System.out.println("Total Amount invested = " + totalInvestedAmount);
		System.out.println("Total Number of Units Purchased till date = " + totalUnitsPurchased);
		System.out.println("NAV as on redemption date = " + redemptionDateNAV.getNav());
		double redeemPrice = redemptionDateNAV.getRepurchasePrice();
		System.out.println("Redemption price as on redemption date = " + redeemPrice);
		
		double totalAmountRedeemed = totalUnitsPurchased * redeemPrice;
		System.out.println("\nTotal Amount Redeemed = " + totalAmountRedeemed);
		double profit = totalAmountRedeemed - totalInvestedAmount;
		
		double xirr = calculateXIRR(investedDays, totalAmountRedeemed, sipAmount);
		System.out.println("IRR Calculated = " + xirr + "%");
		System.out.println("Reverifying calculated IRR using reverse calculation:");
		double verifyingAmount = calcReturnsForCAGR(investedDays, xirr, sipAmount);
		System.out.println("Total Amount Redeemed with IRR = " + xirr + "% for selected investment will be Rs." + verifyingAmount);
		
		if(profit > 0) {
			System.out.println("Profit Earned = " + profit);
			System.out.println("\nComparing your profit with a compounded interest recurring deposit of 8% yearly");
			double comparableReturns = calcReturnsForCAGR(investedDays, 8.0, sipAmount);
			double comparableProfit = comparableReturns - totalInvestedAmount;
			System.out.println("Investing the same at 8% CI annually, you would have got, \n" +
					" returns = " + comparableReturns + 
					" and profit = " + comparableProfit);
			
			System.out.println("\nComparing your profit with a compounded interest recurring deposit of 10% yearly");
			comparableReturns = calcReturnsForCAGR(investedDays, 10.0, sipAmount);
			comparableProfit = comparableReturns - totalInvestedAmount;
			System.out.println("Investing the same at 10% CI annually, you would have got, \n" +
					" returns = " + comparableReturns + 
					" and profit = " + comparableProfit);
			
			System.out.println("\nComparing your profit with a compounded interest recurring deposit of 12% yearly");
			comparableReturns = calcReturnsForCAGR(investedDays, 12.0, sipAmount);
			comparableProfit = comparableReturns - totalInvestedAmount;
			System.out.println("Investing the same at 12% CI annually, you would have got, \n" +
					" returns = " + comparableReturns + 
					" and profit = " + comparableProfit);
			
		} else if (profit < 0) {
			System.out.println("Oopsee. You are unlucky.. You lost " + (-profit) + " rupees by investing");
		} else {
			System.out.println("You could have just put in the money in your bank.. You earned 0% interest investing in this SIP");
		}
	}
	
	private static double calcReturnsForCAGR(List<Integer> investedDays, double cagr, double monthlyInvestmentAmt) {
		double returns = 0;
		for(int days : investedDays) {
			returns += getCompoundedReturns(days, cagr, monthlyInvestmentAmt);
		}
		return returns;
	}
	
	/**
	 * Finds the XIRR closer to the nearest two decimal points.
	 * 
	 * @param investedDays
	 * @param returns
	 * @param monthlyInvestmentAmt
	 * @return
	 */
	private static double calculateXIRR(List<Integer> investedDays, double returns, double monthlyInvestmentAmt) {
		return calculateXIRR(investedDays, returns, monthlyInvestmentAmt, 0, 10);
	}
	
	/**
	 * Find the nearest XIRR using binary search and recursion 
	 * Need to Find some logic for calculating optimum diffRange for recursion algorithm.
	 * 
	 * @param investedDays
	 * @param actualReturns
	 * @param monthlyInvestmentAmt
	 * @param guessIRR
	 * @param guessdiffRange - diffRange to try to find out IRR.
	 * @return
	 */
	private static double calculateXIRR(List<Integer> investedDays, double actualReturns, double monthlyInvestmentAmt, double guessIRR, double guessdiffRange) {
		double calculatedReturns = calcReturnsForCAGR(investedDays, guessIRR, monthlyInvestmentAmt);
		if(calculatedReturns == actualReturns) {
			return guessIRR;
		}
		double calculatedReturnsNext = calcReturnsForCAGR(investedDays, guessIRR+0.01, monthlyInvestmentAmt);
		double calculatedReturnsPrev = calcReturnsForCAGR(investedDays, guessIRR-0.01, monthlyInvestmentAmt);
		double currentDiff = diff(calculatedReturns, actualReturns);
		double diffNext = diff(calculatedReturnsNext, actualReturns);
		double diffPrev = diff(calculatedReturnsPrev, actualReturns);
		//If diff in returns calculated from x as IRR is lesser than 
			//both diff in returns calculated from x+0.01 and x-0.01,
			//then x is the nearest double value in 2 decimals, for original IRR.
		//eg: if actualIRR = 17.18376 and program is currently at 17.18,
			//then, nextIRR = 17.19 and prevIRR = 17.17 both will yield high diff than 17.18
			//Hence, return 17.18 as it is nearest to 17.18376
		if(currentDiff <= diffNext && currentDiff <= diffPrev) {
			return guessIRR;
		} 
		//Else check, which side we need to move currentIRR to.
		else if(currentDiff > diffNext) {
			//(1) We need to increase IRR..
			//(2) Increase IRR as per diffRange.
			//(3) Now implement Binary Search between this (IRR+0) and (IRR+diffRange) 
			double midIRR = guessIRR + guessdiffRange/2;
			double boundaryIRR = guessIRR + guessdiffRange;
			double boundaryReturns = calcReturnsForCAGR(investedDays, boundaryIRR, monthlyInvestmentAmt);
			double midReturns = calcReturnsForCAGR(investedDays, midIRR, monthlyInvestmentAmt);
			double diffNew = diff(boundaryReturns, actualReturns);
			double diffMid = diff(midReturns, actualReturns);
			
			if(currentDiff > diffNew) {
				//This means, IRR is not between guessIRR and boundaryIRR 
					//but beyond boundaryIRR.. i.e, (IRR > boundaryIRR)
				//Hence, calculate IRR using same binary-search-recursion between boundaryIRR and boundaryIRR+diffRange
				//i.e, recurse using boundaryIRR as guessIRR, and diffRange as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, boundaryIRR, guessdiffRange);
			} else if(currentDiff > diffMid) {
				//This means, IRR is between midIRR and boundaryIRR
				//Hence, recurse using midIRR as guessIRR and diffRange/2 as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, midIRR, guessdiffRange/2);
			} else {
				//This means, IRR is between guessIRR and midIRR
				//Hence, recurse using guessIRR as guessIRR and diffRange/2 as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, guessIRR, guessdiffRange/2);
			}
		} else {
			//(1) We need to decrease IRR..
			//(2) Decrease IRR as per diffRange.
			//(3) Now implement Binary Search between this (IRR-diffRange) and (IRR-0)  
			double midIRR = guessIRR - guessdiffRange/2;
			double boundaryIRR = guessIRR - guessdiffRange;
			double boundaryReturns = calcReturnsForCAGR(investedDays, boundaryIRR, monthlyInvestmentAmt);
			double midReturns = calcReturnsForCAGR(investedDays, midIRR, monthlyInvestmentAmt);
			double diffNew = diff(boundaryReturns, actualReturns);
			double diffMid = diff(midReturns, actualReturns);
			
			if(currentDiff > diffNew) {
				//This means, IRR is not between boundaryIRR and guessIRR 
					//but beyond boundaryIRR.. i.e, (IRR < boundaryIRR)
				//Hence, calculate IRR using same binary-search-recursion between boundaryIRR and boundaryIRR-diffRange
				//i.e, recurse using boundaryIRR as guessIRR, and diffRange as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, boundaryIRR, guessdiffRange);
			} else if(currentDiff > diffMid) {
				//This means, IRR is between boundaryIRR and midIRR
				//Hence, recurse using midIRR as guessIRR and diffRange/2 as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, midIRR, guessdiffRange/2);
			} else {
				//This means, IRR is between midIRR and guessIRR
				//Hence, recurse using guessIRR as guessIRR and diffRange/2 as diffRange.
				return calculateXIRR(investedDays, actualReturns, monthlyInvestmentAmt, guessIRR, guessdiffRange/2);
			}
		}
	}
	
	private static double diff(double value1, double value2) {
		return value1>value2 ? (value1-value2) : (value2-value1);
	}
	
	private static double getCompoundedReturns(int days, double cagr, double investedAmount) {
		//A = P*(1 + r/n)^(nt)
		//In our case n=1 considering it is compounded annually. ==> A = P*(1 + r)^t 
		float t=days/365;
		return investedAmount * java.lang.Math.pow(1+cagr/100, t); 
	}

	public static void main(String args[]) throws ParseException {
		System.out.println("Using hard-coded inputs for now..");
	
		String frmdate="30-Jan-2013";
		String todate="05-Mar-2015";
		String dateFormat = "dd-MMM-yyyy";
		String mf="28";
		String scheme="120662";
		double sipAmount = 2000;
		int frequency = 14;
		char frequencyMode = 'd';
		/*StringWrapper maxDate = new StringWrapper();
		HashMap<Date,HistoricNAV> data = readData(frmdate, todate, mf, scheme, dateFormat, maxDate);
		for(Entry<Date,HistoricNAV> pair : data.entrySet()) {
			System.out.println(pair.getValue());
		}*/
		
		calculate(sipAmount, frmdate, frequency, frequencyMode, mf, scheme, todate, dateFormat);
	}
	
	private static class StringWrapper {
		public String maxDate = null;
	}
}
