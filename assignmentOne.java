package assignmentOne;

import java.io.*;
import java.util.*;
import java.time.*;

/**
 * @author cyrus
 * Main class for Programming Project 1.
 */
class assignmentOne {
	//Global variables to count divide-and-conquer loops/recursions for performance assessment.
	//Declared globally to track accurately across recursion calls.
	public static double globalRecursionCounter = 0;
	public static double globalOuterCounter = 0;
	public static double globalInnerCounter = 0;
	public static double globalBruteInnerCounter = 0;
	public static double globalBruteOuterCounter = 0;
	
	/**
	 * static variable to track swap ops for performance measurement
	 */
	static int iterations = 0;
	/**
	 *  static variable to track comparision ops for performance measurement
	 */
	static int comparisons = 0;
	
	//table of results
	static List<String> resultsTable = new ArrayList<String>();
	
	/**
	 * @author cyrus
	 * 
	 * Class Point implements a 2D point with x and y coordinates.
	 * 
	 */
	public static class Point  {
		int x;
		int y;
		
		/**
		 * Constructor takes initial x and y coordinates as parameters
		 * @param x integer value of the x coordinate
		 * @param y integer value of the y coordinate
		 */
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		
		/**
		 * Method finds the distance between this point and an arbitrary Point p
		 * passed as a paramter.
		 * @param p an arbitrary point to find the distance to.
		 * @return the distance between this and Point p.
		 */
		public double findDistance (Point p) {
			return (Math.sqrt(Math.pow((this.x - p.x), 2) + Math.pow((this.y - p.y), 2)));
		}
		
		/**
		 * Method returns a pretty string representation of the point in the format (x, y).
		 * @return string (x, y)
		 */
		public String printPoint() {
			return "(" + this.x + ", " + this.y + ") "; 
		}
		
		/**
		 * Method returns a pretty string representation of the point in the format (x, y).
		 * @return string (x, y) 
		 */
		@Override
	    public String toString(){
	        return "(" + this.x + ", " + this.y + ") ";
	    }
		
		//public int compareTo(Point other) {
		//	return Integer.compare(this.x, other.x);
		//}
	}
	
	
	/**
	 * @author cyrus
	 * Helper class Pair joins 2 Points as a "Pair." Members include both Points
	 * in the pair and the distance between the 2 Points.
	 */
	public static class Pair {
		// 2 Points
		Point p1;
		Point p2;
		
		//Distance between the 2 Points.
		double distance;
		
		/**
		 * Empty constructor. Not used.
		 */
		Pair() {
			//Do nothing.
		}
		
		/**
		 * Class constructor initializes the pair with 2 points and calculates the distance.
		 * @param p Point 1
		 * @param q Point 2
		 */
		Pair (Point p, Point q) {
			this.p1 = p;
			this.p2 = q;
			this.distance = (Math.sqrt(Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2)));
		}
		
		/**
		 * Method returns a string representation of the Pair 
		 * in the format (x1, y1), (x2, y2).
		 * @return String representation in the format (x1, y1), (x2, y2).
		 */
		@Override
	    public String toString(){
	        return "(" + this.p1.x + ", " + this.p1.y + "), (" + this.p2.x + ", " + this.p2.y + ")";
	    }
		
		/**
		 * Method evaluates whether the Pair occurs in a list of Pairs.
		 * Implemented to satisfy the requirement to calculate the closest
		 * m pairs.
		 * @param blacklist the list of Pairs to evaluate inclusion in 
		 * @return true if the Pair occurs in blacklist, false otherwise.
		 */
		public boolean checkBlacklist (List <Pair> blacklist) {
			
			for(Pair p : blacklist) {
				if (this.p1.x == p.p1.x && this.p1.y == p.p1.y && this.p2.x == p.p2.x && this.p2.y == p.p2.y) {
					return true;
				}
				
			}
			return false;
		}
	}
	
	
	/**
	 * Method reads points from a file.
	 * <p>
	 * The file must be formatted such that:
	 * 1. Each point is on a separate line.
	 * 2. The x and y coordinates are separated by a space.
	 * <p>
	 * Warning: this method does not do any file input validation.
	 * @param fileName path to file.
	 * @return a List of Points containing all points read from the file.
	 */
	public static List<Point> readPoints (String fileName) {
		
		//Declare List of Points to be returned.
		List<Point> p = new ArrayList<Point>();
		
		//Open the file pointed to by fileName
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			
			//String to hold one line of the file.
			String line;
			
			//While lines remain...
			while ((line = br.readLine()) != null) {
				//Split the line on the space.
				String[] splitStr = line.split("\\s+");
				
				//Create a point x = integer left of space, y = integer rightof space.
				Point addPoint = new Point(Integer.parseInt(splitStr[0]), Integer.parseInt(splitStr[1]));
				//add the point to the list of points to be returned.
				p.add(addPoint);
				
			}
			br.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		//Return list of Points read from the file.
		return p;
		
	}
	
	/**
	 * Method sorts a list of Points based on their x coordinates.
	 * @param in the list of Points to be sorted
	 * @return the list of Points sorted by x-coordinate 
	 */
	public static List<Point> sortX (List<Point> in) {
		//Declare the returnvariable
		List <Point> out = new ArrayList<>(in);
		
		//Sort the List collection with the parent class method Collections.sort()
		//Feed sort() a comparator that compares p1.x, p2.x
		Collections.sort(out, new Comparator<Point>() {
		    @Override
		    public int compare(Point p1, Point p2) {
		        return Double.compare(p1.x, p2.x);
		    }
		});
		
		return out;
	}
	
	/**
	 * Method sorts a list of Points based on their y-coordinates.
	 * @param in the list of Points to be sorted
	 * @return the list of Points sorted by y-coordinate.
	 */
	public static List<Point> sortY (List<Point> in) {
		//See sortX. This method is identical with the exception of the comparator,
		//which looks at y instead of x.
		List <Point> out = new ArrayList<>(in);
		Collections.sort(out, new Comparator<Point>() {
		    @Override
		    public int compare(Point p1, Point p2) {
		        return Double.compare(p1.y, p2.y);
		    }
		});
		
		return out;
	}
	
	/**
	 * Method implements a brute force algorithm for finding the minimum distance between points.
	 * Two For loops are used to calculate every distance between every point.
	 * The inner For loop compares the most recently calculated distance to the least distance
	 * observed so far. The least distance is updated if it is greater than the most recently
	 * calculated distance.
	 * @param pointArray list of points to be evaluated.
	 * @param quiet suppress iteration stats on the console if true.
	 * @return closestPair, the closest Pair. Self-documenting!!
	 */
	public static Pair bruteDistance (List<Point> pointArray, boolean quiet) {
		//Reset global iteration counters
		globalBruteOuterCounter = 0;
		globalBruteInnerCounter = 0;
		//Declare the return variable.
		Pair closestPair = new Pair();
		
		//Track minDistance and set it to -1 to facilitate the first update.
		double minDistance = -1;
		
		//Local integers to count loop iterations.
		int globalBruteOuterCount = 0;
		int globalBruteInnerCount = 0;
		
		//The following nested For loops drive the time complexity of the algorithm.
		//The outer loop iterates over all points pi in p0, p1, p2...pn.
		//The inner loop iterates over all points pj in pi, pi+1, pi+2...pn.
		//This results in time complexity O(n-squared).
		//For each point in the array...
		for (int i = 0; i < pointArray.size() - 1; i++) {
			//Increment outer loop counter
			globalBruteOuterCounter ++;
			//...look at all subsequent Points j relative to Point i
			for (int j = i + 1; j < pointArray.size(); j++) {
				//Increment inner loop counter
				globalBruteInnerCounter++;
				
				//Create a pair object between Points i and j. Distance is calculated by the constructor
				Pair p = new Pair(pointArray.get(i), pointArray.get(j));
				
				//Compare distance between points in Pair p with minDistance. Update if necessary.
				//If minDistance == -1 this is the first distance calculated, so update regardless.
				if (minDistance == -1 || p.distance < minDistance) {
					minDistance = p.distance;
					closestPair = p;
				}
			}
		}
		
		//Print stats
		if (quiet == false) {
			System.out.println("Points bf'd: " + pointArray.size());
			System.out.println("Brute Force Outer Loop Count: " + globalBruteOuterCounter);
			System.out.println("Brute Force Inner Loop Count: " + globalBruteInnerCounter);
			System.out.println("Total iterations: " + (globalBruteOuterCounter + globalBruteInnerCounter));	
		}
		
		//Return the closestPair
		return closestPair;
	}
	
	
	
	/**
	 * Method is just like bruteDistance, but observes a blacklist of Pairs.
	 * @param pointArray list of Points to be evaluated.
	 * @param quiet suppress stats if true.
	 * @param blacklist list of Pairs to be blacklisted. A Pair in the blacklist will
	 * 					be ignored when comparing distances. A Pair in the blacklist will
	 * 					never be returned.
	 * @return closestPair, the closest Pair not in the blacklist.
	 */
	public static Pair bruteDistanceBlacklist (List<Point> pointArray, boolean quiet, List<Pair> blacklist) {
		//Operates as per bruteDistance above...
		Pair closestPair = new Pair();
		double minDistance = -1;
		int outerCount = 0;
		int innerCount = 0;
		for (int i = 0; i < pointArray.size() - 1; i++) {
			outerCount ++;
			for (int j = i + 1; j < pointArray.size(); j++) {
				innerCount++;
				Pair p = new Pair(pointArray.get(i), pointArray.get(j));
				
				//...until we get here, where the if statement is FALSE if Pair p
				//is in blacklist.
				if ((minDistance == -1 || p.distance < minDistance) && !p.checkBlacklist(blacklist))  {
					minDistance = p.distance;
					closestPair = p;
				}
			}
		}
		if (quiet == false) {
			System.out.println("Points bf'd: " + pointArray.size());
			System.out.println("Brute Force Outer Loop Count: " + outerCount);
			System.out.println("Brute Force Inner Loop Count: " + innerCount);
			System.out.println("Total iterations: " + (outerCount + innerCount));	
		}
		
		return closestPair;
	}
	
	/**
	 * Method implements an O(n log n) divide-and-conquer algorithm to determine the closes pair.
	 * This algorithm is based on the Planar Case algorithm described
	 * here: https://en.wikipedia.org/wiki/Closest_pair_of_points_problem.
	 * Per this algorithm the closest Pair will be the minimum of the
	 * smallest left-side Pair, smallest right-side Pair, and the smallest Pair
	 * among the set of Pairs with points on either side of a dividing vertical.
	 * 
	 * @param pointArray list of Points to be evaluated.
	 * @return closestPair, the closestPair
	 */
	public static Pair dncDistance(List<Point> pointArray) {
		//Increment the global variable that tracks recursion
		globalRecursionCounter++;
		
		//Declare the return variable, the closest Pair found by the dnc algorithm.
		Pair closestDNC = new Pair();
		
		//sort pointArray by x- and by y-coordinates. We'll need both.
		List<Point> byX = sortX(pointArray);
		List<Point> byY = sortY(pointArray);
		
		//Bottom out the recursion.
		if (byX.size() <=3) {
			return bruteDistance(byX, true);
		}
		
		//Begin divide & conquer by splitting the x-sorted list of points in half
		int midpoint = byX.size() / 2;
		List <Point> xLeft = byX.subList(0,  midpoint);
		List <Point> xRight = byX.subList(midpoint, byX.size());

		//For the left and right halves:
		//1. Use a temp variable to keep the original array intact.
		//2. Sort the temp variable by y-coordinate
		//3. Recursively sort each half
		//left first
		List <Point> temp = new ArrayList<Point>(xLeft);
		temp = sortY(temp);
		Pair closestLeft = dncDistance(temp);
		//then right
		temp.clear();
		temp.addAll(xRight);
		temp = sortY(temp);
		Pair closestRight = dncDistance(temp);
		
		//Compare left and right results
		if (closestLeft.distance < closestRight.distance) {
			closestDNC = closestLeft;
		} else {
			closestDNC = closestRight;
		}
		
		//Find the closest Pair among the set of Pairs with one point on either side of
		//a dividing vertical.
		
		//Store the "strip" of points within a reasonable distance of a dividing vertical.
		//"Reasonable" means within the closest distance between Pairs identified so far.
		//We can ignore the rest because they won't beat our current result anyway.
		List <Point> strip = new ArrayList<Point>();
		double shortestDistance = closestDNC.distance;
		double centerX = xRight.get(0).x;
		for (Point point : byY) {
			if (Math.abs(centerX - point.x) < shortestDistance) {
				strip.add(point);
			}
		}

		
		//Calculate the distance between all Pairs in the strip, similar to bruteDistance.
		for (int i = 0; i < strip.size() - 1; i++) {
			//track the number of iterations of the outer loop between recursion calls
			globalOuterCounter++;
			Point tempPoint = strip.get(i);
			
			for (int j = i + 1; j < strip.size(); j++) {
				//track the number ofiterations of the inner loop...
				globalInnerCounter++;
				Point compPoint = strip.get(j);
				//stop if the distance is greater than the closest identified distance, we don't car
				if ((compPoint.y - tempPoint.y) >= shortestDistance) {
					break;
				} else {
					double compDistance = tempPoint.findDistance(compPoint);
					if (compDistance < closestDNC.distance) {
						closestDNC = new Pair(tempPoint, compPoint);
					}
				}
			}
		}
		
		//return the closest Pair
		return closestDNC; 
	}
	
	/**
	 * Method implements dncDistance as per above, but observes a blacklist.
	 * Implemented for the purpose of determining the closest m pairs.
	 * 
	 * @param pointArray list of Points to be evaluated.
	 * @param blacklist list of blacklisted Pairs. Blacklisted Pairs are ignored 
	 * 					when calculating distance and will never be returned.
	 * @return the closest non-blacklisted Pair.
	 */
	public static Pair dncDistanceBlacklist(List<Point> pointArray, List<Pair> blacklist) {
		//All as per dncDistance above...
		globalRecursionCounter++;
		Pair closestDNC = new Pair();
		
		List<Point> byX = sortX(pointArray);
		List<Point> byY = sortY(pointArray);
		
		if (byX.size() <=5) {
			
			//...except we call the brute force algorithm that respects the blacklist...
			return bruteDistanceBlacklist(byX, true, blacklist);
		}
		
		int midpoint = byX.size() / 2;
		
		List <Point> xLeft = byX.subList(0,  midpoint);
		List <Point> xRight = byX.subList(midpoint, byX.size());

		List <Point> temp = new ArrayList<Point>(xLeft);
		temp = sortY(temp);
		Pair closestLeft = dncDistanceBlacklist(temp, blacklist);
		
		temp.clear();
		temp.addAll(xRight);
		temp = sortY(temp);
		Pair closestRight = dncDistanceBlacklist(temp, blacklist);
		
		if (closestLeft.distance < closestRight.distance) {
			closestDNC = closestLeft;
		} else {
			closestDNC = closestRight;
		}
		
		List <Point> strip = new ArrayList<Point>();
		double shortestDistance = closestDNC.distance;
		double centerX = xRight.get(0).x;
		for (Point point : byY) {
			if (Math.abs(centerX - point.x) < shortestDistance) {
				strip.add(point);
			}
		}

		for (int i = 0; i < strip.size() - 1; i++) {
			//globalOuterCounter++;
			Point tempPoint = strip.get(i);
			
			for (int j = i + 1; j < strip.size(); j++) {
				//globalInnerCounter++;
				Point compPoint = strip.get(j);
				if ((compPoint.y - tempPoint.y) >= shortestDistance) {
					break;
				} else {
					double compDistance = tempPoint.findDistance(compPoint);
					if (compDistance < closestDNC.distance) {
						Pair tempPair = new Pair(tempPoint, compPoint);
						//...and check the blacklist when comparing strip pairs
						if (!tempPair.checkBlacklist(blacklist)) {
							closestDNC = new Pair(tempPoint, compPoint);
						}
					}
				}
			}
		}
		
		//debug
		/*if (closestDNC.p1 == null)
			System.out.println("Null.");*/
		
		return closestDNC; 
	}
	
	/**
	 * Generates a list of random Points.
	 * @param numPoints number of Points to be generated
	 * @return a list of random Points.
	 */
	/*public static List<String> generateRandomPoints(int numPoints) {
		
		Random rand = new Random();
		
		//Declare the return variable
		List<String> pointList = new ArrayList<String>();
		
		//Generate random x and y coordinates.
		//Values restricted to <= 1000 to make manual validation of distances easier.
		//Create a point with the randomly-generated coords and add it to the return list.
		for (int i = 0; i < numPoints; i++) {
			int x = rand.nextInt(1000);
			int y = rand.nextInt(1000);
			String s = x + " " + y;
			pointList.add(s);
		}
		
		//Return the list of random Points.
		return pointList;
	}*/
	
	/**
	 * Generates a list of random Points.
	 * @param numPoints number of Points to be generated
	 * @return a list of random Points.
	 */
	public static List<Point> generateRandomPoints(double numPoints) {
		
		Random rand = new Random();
		
		//Declare the return variable
		List<Point> pointList = new ArrayList<Point>();
		
		//Generate random x and y coordinates.
		//Values restricted to <= 1000 to make manual validation of distances easier.
		//Create a point with the randomly-generated coords and add it to the return list.
		for (double i = 0; i < numPoints; i++) {
			int x = rand.nextInt(1000);
			int y = rand.nextInt(1000);
			Point p = new Point(x, y);
			pointList.add(p);
		}
		
		//Return the list of random Points.
		return pointList;
	}
	
	/**
	 * Helper method writes a list of Points to a file
	 * @param pointList list of points to be written.
	 * @param outFile path to output file.
	 */
	public static void writePointsToFile (List<String> pointList, String outFile) {
		
		//Open the file and write Points to it.
		//Relies on implementation of Points.toString()
		try (FileWriter writer = new FileWriter(outFile)) {
			for(String s : pointList) {
				writer.write(s + System.lineSeparator());
			}
			writer.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	/**Simple helper function to append text to a file. Used to append results to 
	 * input files.
	 * @param filePath path to file
	 * @param textToAppend text to append
	 */
	public static void appendToFile (String filePath, String textToAppend) {
	     
	    try (BufferedWriter writer = new BufferedWriter(
	                                new FileWriter(filePath, true)  //Set true for append mode
	                            )) {
	    	writer.newLine();   //Add new line
		    writer.write(textToAppend);
		    writer.close();	
	    } catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	    
	}
	
	
	/*public static List<Pair> constructAllPairs(List<Point> pointArray) {
		List<Pair> p = new ArrayList<Pair>();
		for (Point i : pointArray) {
			for (Point j : pointArray) {
				Pair temp = new Pair(i, j);
				p.add(temp);
			}
		}
		return p;
	}*/
	
	/**
     * Write results table to a file.
     * @param outFile filename of output file
     * @param outStrings list of strings that comprise the results we want to output.
     */
	public static void writeResults (List<String> outStrings, String outFile) {
		
		//Open the file and write Points to it.
		//Relies on implementation of Points.toString()
		try (FileWriter writer = new FileWriter(outFile)) {
			for(String s : outStrings) {
				writer.write(s + System.lineSeparator());
			}
			writer.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	/**
	 * Main function accepts user input and runs tests.
	 * @param args  
	 * 				1. outfile <optional> - path to write results to.
	 * 				2. maxPoints - maximum number of random points to generate. Greater than 100,000, be prepared to wait. 
	 * 				3. m <optional> - per D-ii, calculate the closest m pairs. Defaults to 1.

	 */
	public static void main (String args[]) {
		
		resultsTable.add("Random point arrays of length 100 - 1,000,000, incrementing 10x per iteration:\n\n");
		String format = "|%1$-10s|%2$-10s|%3$-30|%4$-30s|%5$-30s|%6$-30s";
		String headerString = String.format(format, "Method", "Length(n)", "Comparisons", "Iterations", "nlogn", "n^2");
		resultsTable.add(headerString);
		
		//Check if infile and outfile are specified. If not, exit.
		if (args.length < 2) {
			System.out.println("Must specify infile and outfile at minimum. Specify m with 3rd argument optionally.");
			System.exit(0);
		}
		
		//Assign in and outfiles
		String outFile = args[0];
		double maxPoints = Double.parseDouble(args[1]);
		
		//Per part D-ii, track m for the purpose of calculating the closest m pairs.
		int m = 1;
		int randomGeneratedPoints = 0;
		if (args.length > 2) {
			m = Integer.parseInt(args[2]);
		}
		
		//Generate and write random points if specified by the user.
		/*if (args.length > 3) {
			randomGeneratedPoints = Integer.parseInt(args[3]);
			List <String> pointList = generateRandomPoints(randomGeneratedPoints);
			writePointsToFile(pointList, outFile);
		}*/
		
		//Print args for debug purposes.
		for (String val: args) {
			System.out.println(val);
		}
		
		//Read points from inFile
		//List<Point> pointArray = readPoints(inFile);
		
		
		for (double i = 100; i <= maxPoints; i = i*10) {
			List<Point> pointArray = generateRandomPoints(i);
			
			//Calculate closest pair by brute force. Display stats.
			Pair closestBrute = bruteDistance (pointArray, false);
			
			//Display the closest pair calculated by BF
			System.out.println("The minimum distance calculated by brute force: " + closestBrute.distance);
			System.out.println("Between points: " + closestBrute);
			//Append information to input file
			/*appendToFile(inFile, ("Brute Force Results " + LocalDateTime.now() +":"));
			appendToFile(inFile, ("The minimum distance calculated by brute force: " + closestBrute.distance));
			appendToFile(inFile, ("Between points: " + closestBrute));
			appendToFile(inFile, ("Points bf'd: " + pointArray.size()));
			appendToFile(inFile, ("Brute Force Outer Loop Count: " + globalBruteOuterCounter));
			appendToFile(inFile, ("Brute Force Inner Loop Count: " + globalBruteInnerCounter));
			appendToFile(inFile, ("Total iterations: " + (globalBruteOuterCounter + globalBruteInnerCounter)));*/
			String resultsString = String.format(format, "Brute", 
					Integer.toString(pointArray.size()), 
					Double.toString(globalBruteInnerCounter),
					"N/A",
					Double.toString(pointArray.size() * (Math.log(pointArray.size()) / Math.log(2))),
					Double.toString((pointArray.size() * pointArray.size())));
			resultsTable.add(resultsString);
			
			//Calculate the closes pair using a divide and conquer algorithm
			//Print the closest pair and stats and write to file;
			Pair dnc = dncDistance(pointArray);
			
			//appendToFile(inFile, ("DNC Results " + LocalDateTime.now() +":"));
			System.out.println("The minimum distance calculated by divide and conquer: ");
			//appendToFile(inFile, ("The minimum distance calculated by divide and conquer: "));
			System.out.println(dnc + " " + dnc.distance);
			//appendToFile(inFile, (dnc + " " + dnc.distance));
			System.out.println("This took " + globalRecursionCounter + " recursive calls, and " + (globalOuterCounter + globalInnerCounter) + " iterations of the final comparison loop.");
			//appendToFile(inFile, ("This took " + globalRecursionCounter + " recursive calls, and " + (globalOuterCounter + globalInnerCounter) + " iterations of the final comparison loop."));
			
			resultsString = String.format(format, "DNC", 
					Integer.toString(pointArray.size()), 
					Double.toString(globalInnerCounter),
					Double.toString(globalRecursionCounter),
					Double.toString(pointArray.size() * (Math.log(pointArray.size()) / Math.log(2))),
					Double.toString((pointArray.size() * pointArray.size())));
			resultsTable.add(resultsString);
			
			//If specified by the user, calculate the closest m Pairs by adding
			//each successive closest pair to a blacklist. Blacklisted pairs passed to
			//brute force and dnc methods that respect the blacklist will be ignored, resulting in
			//the next closest pair being returned. Each successive closest pair is added to a list of
			//closest pairs until m is fully decremented.
			if (m > 1 && m < pointArray.size()) {
				List <Pair> closestPairs = new ArrayList<Pair>();
				List <Point> cloneArray = new ArrayList<Point>(pointArray);
				

				for (int a = 0; a < m; a++) {
					Pair closestPair = dncDistanceBlacklist(cloneArray, closestPairs);
					closestPairs.add(closestPair);
				}
				
				//Print results and write to infile
				
				System.out.println("The closest m = " + m + " pairs calculated by DNC are:");
				resultsTable.add("The closest m = " + m + " pairs calculated by DNC are:");
				//appendToFile(inFile, (("The closest m = " + m + " pairs calculated by DNC are:")));
				for (Pair p : closestPairs) {
					System.out.println(p + " " + p.distance);
					//appendToFile(inFile, (p + " " + p.distance));
					resultsTable.add(p + " " + p.distance);
				}
			}		

		}
		writeResults(resultsTable, outFile);
	}
}