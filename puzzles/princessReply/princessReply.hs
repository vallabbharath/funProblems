-- Refer qnPrincessReply.txt in the same folder for the actual puzzle question.

-- Usage 
-- (1) "getAnswer" outputs the first possible number as per the below solver logic. (of form f1f2)
-- (2) "princessReply" outputs the princess's reply for any given x. Example princessReply 1342 = 34

--Credits : Arunram --> github.com/arunatma
		-- for coding the API for princessReply. I only wrote the solver logic below :-)	

--Solution: Logic Below.
{- 
let f may be sequence of numbers (or we shall call sequence of functions or operations)
	and f is between 3 and 7 (operations 1x2 can't be part of the sequence as it can only be last operation of the recursion call)
		
let f++x = f(x) = y, we need to find f & x such that, fx = y, i.e, f(x) = fx. no wait, but f can be applied ultimately only on numbers of form 1x2  

so f++1x2 = f(x) = y, we need to find f and x such that f(x) = f1x2.. 

Substitute x with f again.. So we need to find a number f such that, f(f) = f1f2...
Then f1f2 will be the number that will get output f1f2 from princess reply. 
-}

rule1 :: String -> String
rule1 [] = []
rule1 (x:xs)
    | x == '2' = reverse xs
    | otherwise = []
    
rule2 :: String -> String
rule2 xs = xs ++ xs

rule3 :: String -> String
rule3 = reverse

-- erasure rule applied only if atleast 2 digits available after taking out first digit '5' 
-- i.e, min 3 digits.
rule4 :: String -> String
rule4 (x:y:z:xs) = y:z:xs
rule4 xs = xs

rule5 :: Char -> String -> String
rule5 a xs = a : xs
    
princessReply :: String -> String
princessReply [] = []
princessReply (x:xs)
    | x == '1' = rule1 $ reverse xs
    | x == '3' = rule2 $ princessReply xs
    | x == '4' = rule3 $ princessReply xs
    | x == '5' = rule4 $ princessReply xs
    | x == '6' = rule5 '1' $ princessReply xs
    | x == '7' = rule5 '2' $ princessReply xs
    | otherwise = ""
	
-- First possible value for f in the given Integer range, such that f1f2 yields f1f2 as reply from the Princess
firstValidReply :: [Integer] -> String
firstValidReply xs
	| null xs = ""
	--f1f2 == f1f2
	| princessReply x == x = x
	| otherwise = firstValidReply (tail xs)
		where x = buildFullString (show $head xs) 
	
--For a given f, this function outputs x, where x=f1f2
buildFullString :: String -> String
buildFullString f = (f++"1"++f++"2")

getAnswer :: String
getAnswer = firstValidReply [1..]
