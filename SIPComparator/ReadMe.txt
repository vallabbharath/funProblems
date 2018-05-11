This SIPComparator aims at, for a scheme and period selected,
		(i) calculating Total Profit, Total Invested Amount, and total redemption amount
		(ii) compare the profit with Compound Interest Investment of 8%, 10% and 12% of same investment amount and periods.
		(iii) #TODO - calculating IRR(yet to be implemented) 
		
Inputs required:
	1. SIP amount 
    2. SIP start date 
	3. frequency mode (d - days, m - months, w - weeks), 
	4. frequency - number of units of above frequencyMode(days/months/weeks). 
			--> Denotes the frequency of investment in SIP. eg: freq = 14, mode = 'd' denotes, SIP invested every 14 days.
    5. MF code + Scheme code (of MF you want to analyse)
    6. Redemption date
	
Outputs:
	1. Total invested amount
    2. Total Redemption amount
    3. IRR
    4. Comparable Maturity @8%; @10% and @12% - CAGR
	
	
I have used the data from the amfi-portal url http://portal.amfiindia.com/NavHistoryReport_Frm.aspx

Restrictions: 
Currently we have to manually provide the MF code and scheme code, after checking the same from the url.

TODO:
Need to implement logic to fetch the scheme names listed in http://www.portal.amfiindia.com/spages/NAV0.txt, 
and allow user to select scheme-name instead of scheme-code.