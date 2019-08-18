package quicksort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class quicksort {

	/**
	 * static variable to track swap ops for performance measurement
	 */
	static int swaps = 0;
	/**
	 *  static variable to track comparision ops for performance measurement
	 */
	static int comparisons = 0;
	
	//table of results
	static List<String> resultsTable = new ArrayList<String>();

	/**
	 * method to find the median pivot and swap it with the last element
	 * in order to reuse our stock partition function with the benefit of
	 * median-of-3.
	 * @param inputArray array contianing values to be sorted
	 * @param low lower index
	 * @param high upper index
	 * @return
	 */
	public static int medianPivot(int inputArray[], int low, int high) {
		
		int median = medianOfThree(inputArray[0], inputArray[(high)/2], inputArray[inputArray.length -1]);
		
		//Swap the median value with the highest value (inputArray[high])
		//This way we can reuse our common partition() function instead of building one dedicated for median of 3
		if (median == inputArray[0]) {
			inputArray = swap(inputArray, 0, high);
		} else if  (median == inputArray[(high)/2]) {
			inputArray = swap(inputArray, (high) / 2, high);
		} else {
			//do nothing, pivot point is already at index high
		}
		
		return partition(inputArray, low, high-1);
	}


	/**
	 * Just like quicksort, but calls medianPartition in order to pivot on the median-of-3
	 * @param inputArray array containing values to be sorted
	 * @param low lower index
	 * @param high upper index
	 */
	public static void medianQuickSort(int inputArray[], int low, int high) {
		if (low >= high)
			return;

		if (low < high) {

			int medianPartition = medianPivot(inputArray, low, high);
			
			medianQuickSort(inputArray, low, medianPartition -1);
			medianQuickSort(inputArray, medianPartition + 1, high);

		}
	}
	
	/**
	 * Stock quicksort implementation, copied from the textbook.
	 * @param inputArray array containing values to be sorted
	 * @param low lower index
	 * @param high upper index
	 */
	public static void quickSort(int inputArray[], int low, int high) {
		if (low >= high)
			return;
		
		if (low < high) {
			int quickPartition = partition(inputArray, low, high);

			quickSort(inputArray, low, quickPartition - 1);
			quickSort(inputArray, quickPartition + 1, high);
		}
	}

	
	/**
	 * Helper function to pretty-print an array. Used to help display sorting results on screen.
	 * @param inputArray array to be printed.
	 */
	public static void printArray(int inputArray[]) {
		int n = inputArray.length;
		System.out.print("\t Printing Array : { ");
		for (int i = 0; i < n; ++i) {

			System.out.print(inputArray[i] + " ");

		}
		System.out.print("}\n");
	}

	/**
	 * Stock partitioning function, implemented from CLRS.
	 * @param inputArray array of values to be sorted
	 * @param low lower index
	 * @param high upper index
	 */
	public static int partition(int inputArray[], int low, int high) {
		int pivot = inputArray[high];
		int i = (low - 1); // index of smaller element

		for (int j = low; j < high; j++) {
			// If current element is smaller than or
			// equal to pivot
			if (inputArray[j] <= pivot) {
				i++;

				// swap inputArray[i] and inputArray[j]
				inputArray = swap(inputArray, i, j);
				swaps++;
			}
			comparisons++;
		}

		// swap inputArray[i+1] and the pivot value
		inputArray = swap(inputArray, i+1, high);
		swaps++;
		return i + 1;

	}
	
	/**
	 * Helper function to find the median among 3 integer args.
	 * @param a first integer
	 * @param b second integer
	 * @param c third integer
	 * @return the value of the median-of-3
	 */
	public static int medianOfThree(int a, int b, int c) {
	    
		//Clever trick to find median value with 2 comparisons.
		//Definitely not pasted from stack overflow...
		if ( (a - b) * (c - a) >= 0 ) // a >= b and a <= c OR a <= b and a >= c
	        return a;
	    else if ( (b - a) * (c - b) >= 0 ) // b >= a and b <= c OR b <= a and b >= c
	        return b;
	    else
	        return c;
		
		//...it was totally pasted from stack overflow...
	}
	
	/**
	 * Helper function to swap values at specified indices within an input array.
	 * @param inputArray array containing values to be swapped.
	 * @param index1 index to be swapped
	 * @param index2 index to be swapped
	 * @return inputArray with values at specified indices swapped.
	 */
	public static int[] swap(int inputArray[], int index1, int index2) {
		
		int temp = inputArray[index1]; 		//temporary storage of index1
		inputArray[index1] = inputArray[index2]; //index 1 = index2
		inputArray[index2] = temp; //index 2 = old index 1
		
		return inputArray;
	}
	
    /**
     * Code to read arrays from file. Ended up not using it, generating datasets on the fly.
     * @param fileName filename to open
     * @return list of integer arrays to be sorted.
     */
    /*public static List<int[]> readExpressions (String fileName) {
		
		//Declare List of integer arrays to be returned.
		List<int[]> inputArrays = new ArrayList<int[]>();
		
		//Open the file pointed to by fileName
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			
			//String to hold one line of the file.
			String line;
			
			//While lines remain...
			while ((line = br.readLine()) != null) {
				List<Integer> expressions = new ArrayList<>();
				
				//..split each line on spaces to extract integers
				String[] tmp = line.split(" ");
				for (String s : tmp) {
					//put the integers in a List
					expressions.add(Integer.parseInt(s));
				}
				
				//convert the list to an integer array
				int[] inputArray = expressions.stream().mapToInt(i->i).toArray();
				
				//add it to the list of arrays to be returned
				inputArrays.add(inputArray);
			}
			br.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		//inputArrays now represents the list of arrays read from inFile that we want to sort
		//return it
		return inputArrays;
		
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
     * Helper function to generate random arrays of integers.
     * Lengths are powers of 2 up to the specified upper bound.
     * @param upperBound upper bound of array lengths. Keep it less than 32768 to avoid integer overflow.
     * @return list of random integer arrays.
     */
    public static List<int[]> generateRandomArrays(int upperBound) {
    	//Declare List of integer arrays to be returned.
    	List<int[]> inputArrays = new ArrayList<int[]>();
    	Random rd = new Random();
    	for (int i = 2; i <= upperBound; i = i * 2) {
    		int [] inputArray = new int[i];
    		for (int j = 0; j < inputArray.length; j++) {
    	         inputArray[j] = rd.nextInt(); // storing random integers in an array
    		}
    		inputArrays.add(inputArray);
    	}
    	return inputArrays;
    }
    
    /**
     * Helper function to generate in-order sequences of integers.
     * Lengths are powers of 2 up to the specified upper bound.
     * Designed to test the expected worst case of quicksort.
     * @param upperBound upper bound of array lengths. Keep it less than 32768 to avoid integer overflow.
     * @return list of in-order integer arrays.
     */
    public static List<int[]> generateSequences(int upperBound) {
    	List<int[]> inputArrays = new ArrayList<int[]>();
    	for (int i = 2; i < upperBound; i = i * 2) {
    		
    		List<Integer> range = IntStream.rangeClosed(0, i-1).boxed().collect(Collectors.toList());
    		int[] inputArray = range.stream().mapToInt(j->j).toArray();
    		inputArrays.add(inputArray);
    	}
    	
    	return inputArrays;
    }
    
    /**
     * Process a List of arrays containing integers to be sorted by
     * calling quicksort and medianQuicksort.
     * Outputs results to screen, and populates resultsTable for output to file.
     * @param inputArrays List of integer arrays to be sorted.
     */
    public static void processArrays(List<int[]> inputArrays) {
    	
    	//format string for resultsTable
    	String format = "|%1$-10s|%2$-10s|%3$-15s|%4$-15s|%5$-15s|%6$-15s";
    	
    	//loop through all of the integer arrays in integerArrays
    	for (int[] inputArray : inputArrays){
			//reset performance counters
			swaps = 0;
			comparisons = 0;
			int arraySize = inputArray.length;
			System.out.println("Array size is " + arraySize + " elements.");
			
			//Print each unsorted array if length is reasonable
			System.out.println("Read array from input file:");
			if(inputArray.length < 101) {
				printArray(inputArray);
			}
			//Clone the array for non-destructive sorting
			int [] quickArray = inputArray;
			System.out.println("Sorting with unmodified Quicksort:");
			//Call quickSort
			quickSort(quickArray, 0, quickArray.length -1);
			//Display results
			System.out.println("Quicksorted with " + swaps + " swaps and " + comparisons + " comparisons.");
			System.out.println("Expected O(nlgn) performance = " + (inputArray.length * (Math.log(inputArray.length) / Math.log(2))));
			System.out.println("Expected O(n^2) performance = " + (inputArray.length * inputArray.length));
			if (quickArray.length < 101) {
				printArray(quickArray);
			}
			
			//Format results for inclusion in resultsTable
			String resultsString = String.format(format, "Stock", 
													Integer.toString(arraySize), 
													Integer.toString(comparisons),
													Integer.toString(swaps),
													Double.toString(inputArray.length * (Math.log(inputArray.length) / Math.log(2))),
													Integer.toString((inputArray.length * inputArray.length)));
			//Add results to resultsTable
			resultsTable.add(resultsString);
			//Reset performance counters
			swaps = 0;
			comparisons = 0;
			
			//Call our median of 3 quicksort method
			System.out.println("Sorting with Quicksort Median of Three:");
			int [] quick3Array = inputArray;
			medianQuickSort(quick3Array, 0, quick3Array.length - 1);
			//Print results
			System.out.println("Mo3 Quicksorted with " + swaps + " swaps and " + comparisons + " comparisons.");
			System.out.println("Expected O(nlgn) performance = " + (inputArray.length * (Math.log(inputArray.length) / Math.log(2))));
			System.out.println("Expected O(n^2) performance = " + (inputArray.length * inputArray.length));
			if(quick3Array.length < 101) {
				printArray(quick3Array);
			}
			
			resultsString = String.format(format, "Mo3",
					Integer.toString(arraySize), 
					Integer.toString(comparisons),
					Integer.toString(swaps),
					Double.toString(inputArray.length * (Math.log(inputArray.length) / Math.log(2))),
					Integer.toString((inputArray.length * inputArray.length)));
			resultsTable.add(resultsString);
			//Blank lines between sorts for readability
			System.out.println();
			System.out.println();
			
		}
	
    	
    }
    
	/**
	 * main method simply sets up headers in resultsTable, 
	 * generates random and in-order arrays, and processes them
	 * with processArrays. Writes resultsTable to file "results.txt" 
	 * at the end.
	 * @param args
	 */
	public static void main(String[] args) {
		
		String inFile = args[0];
		List<int[]> inputArrays = new ArrayList<int[]>();
		
		resultsTable.add("Random sequences of lengths 2 - 32768:\n\n");
		String format = "|%1$-10s|%2$-10s|%3$-15s|%4$-15s|%5$-15s|%6$-15s";
		String headerString = String.format(format, "Method", "Length", "Comparisons", "Swaps", "Theta", "O");
		resultsTable.add(headerString);
		
		
		System.out.println("Now assessing performance of randomly generated sequences of integers, starting with length 2, increasing by powers of 2 to 32768.");
		inputArrays = generateRandomArrays(32768);
		processArrays(inputArrays);
		
		
		resultsTable.add("\n\nIn-order sequences of lengths 2 - 32768 (expected worst case):\n\n");
		resultsTable.add(headerString);
		System.out.println("Now assessing performance of in-order sequences of integers of random length, starting at 32, increasing by powers of 2 to 32768. Designed to simulate worst-case performance.");
		inputArrays = generateSequences(32768);
		processArrays(inputArrays);
		
		
		writeResults(resultsTable, "results.txt");
		
		
		/*early protoype code
		for (int[] inputArray : inputArrays){
			//reset performance counters
			swaps = 0;
			comparisons = 0;
			int arraySize = inputArray.length;
			
			//Print each unsorted array
			System.out.println("Read array from input file:");
			printArray(inputArray);
			//Clone the array for non-destructive sorting
			int [] quickArray = inputArray;
			System.out.println("Sorting with unmodified Quicksort:");
			//Call quickSort
			quickSort(quickArray, 0, quickArray.length -1);
			//Dislay results
			System.out.println("Quicksorted with " + swaps + " swaps and " + comparisons + " comparisons.");
			System.out.println("Expected O(nlgn) performance = " + (inputArray.length * (Math.log(inputArray.length) / Math.log(2))));
			System.out.println("Expected O(n^2) performance = " + (inputArray.length * inputArray.length));
			printArray(quickArray);
			
			//Reset performance counters
			swaps = 0;
			comparisons = 0;
			
			//Call our median of 3 quicksort method
			System.out.println("Sorting with Quicksort Median of Three:");
			int [] quick3Array = inputArray;
			medianQuickSort(quick3Array, 0, quick3Array.length - 1);
			//Print results
			System.out.println("Mo3 Quicksorted with " + swaps + " swaps and " + comparisons + " comparisons.");
			System.out.println("Expected O(nlgn) performance = " + (inputArray.length * (Math.log(inputArray.length) / Math.log(2))));
			System.out.println("Expected O(n^2) performance = " + (inputArray.length * inputArray.length));
			printArray(quick3Array);
			
			//Blank lines between sorts for readability
			System.out.println();
			System.out.println();
			
		}*/
	}
}
