package dtm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.Set;


import java.util.Map;

/**
 * @author cyrus
 * Class DTM implements a Deterministic Turing Machine.
 * My code is based on the following public examples:
 * https://rosettacode.org/wiki/Universal_Turing_machine#Java
 * https://introcs.cs.princeton.edu/java/52turing/TuringMachine.java.html
 * 
 * The class represents the tape as a list of strings, using a ListIterator to navigate
 * left and right. Transitions are built as HashMaps of StateTapeSymbolPairs, a custom
 * class defined to contain state/tape symbol pairs.
 */
/**
 * @author cyrus
 *
 */
public class DTM {
    private List<String> tape;		//the tape
    private String blankSymbol;		//the blank symbol
    private ListIterator<String> head;	//a ListIterator to navigate the tape
    
    //a table defining transitions between states
    private Map<StateTapeSymbolPair, Transition> transitions = new HashMap<StateTapeSymbolPair, Transition>();
    private Set<String> terminalStates;	//states that will terminate execution
    private String initialState;		//the state at which the TM enters execution
 
    /**
     * Class constructor initializes the member variables listed above.
     * @param transitions HashMap of transition functions
     * @param terminalStates Set of states that will terminate execution
     * @param initialState state at which the machine enters execution
     * @param blankSymbol arbitrary symbol indicating  blank entry
     */
    public DTM(Set<Transition> transitions, Set<String> terminalStates, String initialState, String blankSymbol) {
        //Initialize member variables
    	this.blankSymbol = blankSymbol;
        for (Transition t : transitions) {
            this.transitions.put(t.from, t);
        }
        this.terminalStates = terminalStates;
        this.initialState = initialState;
    }
 
    /**
     * @author cyrus
     * Helper class StateTapeSymbolPair contains a combination of state/tape symbol. Implements
     * hashing, comparison, and toString methods. It is the basis for class Transition, which is the basis
     * for the table of transitions required by class DTM. 
     */
    public static class StateTapeSymbolPair {
        private String state;
        private String tapeSymbol;
 
        public StateTapeSymbolPair(String state, String tapeSymbol) {
            this.state = state;
            this.tapeSymbol = tapeSymbol;
        }
 
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((state == null) ? 0 : state.hashCode());
            result = prime
                    * result
                    + ((tapeSymbol == null) ? 0 : tapeSymbol
                            .hashCode());
            return result;
        }
 
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StateTapeSymbolPair other = (StateTapeSymbolPair) obj;
            if (state == null) {
                if (other.state != null)
                    return false;
            } else if (!state.equals(other.state))
                return false;
            if (tapeSymbol == null) {
                if (other.tapeSymbol != null)
                    return false;
            } else if (!tapeSymbol.equals(other.tapeSymbol))
                return false;
            return true;
        }
 
        @Override
        public String toString() {
            return "(" + state + "," + tapeSymbol + ")";
        }
    }
 
    /**
     * @author cyrus
     * Helper class transition is the basis for the transition tables that store transition functions
     * between states. It consists of 2 StateTapeSymbolPairs defining the from and to of the function,
     * and a member variable to store the direction (1 Right, -1 Left).
     */
    public static class Transition {
        private StateTapeSymbolPair from;
        private StateTapeSymbolPair to;
        private int direction; 
 
        /** Class constructor initializes member variables
         * @param from original state
         * @param to state to transition to
         * @param direction left (-1) or right (1)
         */
        public Transition(StateTapeSymbolPair from, StateTapeSymbolPair to, int direction) {
            this.from = from;
            this.to = to;
            this.direction = direction;
        }
        
        
        @Override
        public String toString() {
            return from + "=>" + to + "/" + direction;
        }
    }
 
    /**Method initializeTape assigns a List <string> of tape symbols to the tape member variable.
     * @param input list of strings, each element being a tape symbol
     */
    public void initializeTape(List<String> input) { // Arbitrary Strings as symbols.
        tape = input;
    }
 
    /**Method initializeTape assigns a List <string> of tape symbols to the tape member variable.
     * @param input a string of symbols. The method converts the string to a List <string>
     */
    public void initializeTape(String input) { // Uses single characters as symbols.
        tape = new LinkedList<String>();
        for (int i = 0; i < input.length(); i++) {
            tape.add(input.charAt(i) + "");
        }
    }
 
    /**Method runTM executes the TM, terminating on an exception or a halting state.
     * @return the finished tape if successful, null otherwise.
     */
    public List<String> runTM() { 
        
    	//add a blank symbol if thereisnothing in the tape.
    	if (tape.size() == 0) {
            tape.add(blankSymbol);
        }
 
        //Treat the tape as a linked list.
    	head = tape.listIterator();
        head.next();
        head.previous();
 
        //tsp will point to the current state for the remainder of thefunction
        //initialize it to the initialState specified during construction, + the first tape symbol
        StateTapeSymbolPair tsp = new StateTapeSymbolPair(initialState, tape.get(0));
 
        //While a matching symbol exists
        while (transitions.containsKey(tsp)) { 
            //Print the machine state and the applicable transition function
        	System.out.println(this + " --- " + transitions.get(tsp));
            
        	//Get the applicable transition function
        	Transition trans = transitions.get(tsp);
            
        	//Write the tape symbol
        	head.set(trans.to.tapeSymbol); 
            
        	//Change the state
        	tsp.state = trans.to.state; // Change state.
            
        	//Go left or right depending on the direction specified by the trans function, extending the tape if necessary
        	if (trans.direction == -1) { 
                if (!head.hasPrevious()) {
                    head.add(blankSymbol); 
                }
                tsp.tapeSymbol = head.previous(); // Memorize tape symbol.
            } else if (trans.direction == 1) { // Go right.
                head.next();
                if (!head.hasNext()) {
                    head.add(blankSymbol); // Extend tape.
                    head.previous();
                }
                tsp.tapeSymbol = head.next(); // Memorize tape symbol.
                head.previous();
            } else {
                tsp.tapeSymbol = trans.to.tapeSymbol;
            }
        }
 
        //Print final machine state + applicable transition function
        System.out.println(this + " --- " + tsp);

        //If we ended in a state that is defined as a halting condition, we win, return the tape.
        //Otherwise we failed, return null
        if (terminalStates.contains(tsp.state)) {
            return tape;
        } else {
            return null;
        }
    }

    /**Overloaded to return the final tape plus the halting condition. This is important
     * for algorithms like the example from module 3.
     * @return the finished tape if successful, null otherwise.
     */
    public String runTM(boolean returnState) { 
        
    	//add a blank symbol if thereisnothing in the tape.
    	if (tape.size() == 0) {
            tape.add(blankSymbol);
        }
 
        //Treat the tape as a linked list.
    	head = tape.listIterator();
        head.next();
        head.previous();
 
        //tsp will point to the current state for the remainder of thefunction
        //initialize it to the initialState specified during construction, + the first tape symbol
        StateTapeSymbolPair tsp = new StateTapeSymbolPair(initialState, tape.get(0));
 
        //While a matching symbol exists
        while (transitions.containsKey(tsp)) { 
            //Print the machine state and the applicable transition function
        	System.out.println(this + " --- " + transitions.get(tsp));
            
        	//Get the applicable transition function
        	Transition trans = transitions.get(tsp);
            
        	//Write the tape symbol
        	head.set(trans.to.tapeSymbol); 
            
        	//Change the state
        	tsp.state = trans.to.state; // Change state.
            
        	//Go left or right depending on the direction specified by the trans function, extending the tape if necessary
        	if (trans.direction == -1) { 
                if (!head.hasPrevious()) {
                    head.add(blankSymbol); 
                }
                tsp.tapeSymbol = head.previous(); // Memorize tape symbol.
            } else if (trans.direction == 1) { // Go right.
                head.next();
                if (!head.hasNext()) {
                    head.add(blankSymbol); // Extend tape.
                    head.previous();
                }
                tsp.tapeSymbol = head.next(); // Memorize tape symbol.
                head.previous();
            } else {
                tsp.tapeSymbol = trans.to.tapeSymbol;
            }
        }
 
        //Print final machine state + applicable transition function
        System.out.println(this + " --- " + tsp);

        //If we ended in a state that is defined as a halting condition, we win, return the tape.
        //Otherwise we failed, return null
        if (terminalStates.contains(tsp.state)) {
            if(returnState) {
            	return (tape.toString() + tsp.toString());
            } else {
            	return tape.toString();
            }
        } else {
            return null;
        }
    }


    public String toString() {
        try {
        	int headPos = head.previousIndex();
            String s = "[ ";
 
            for (int i = 0; i <= headPos; i++) {
                s += tape.get(i) + " ";
            }
 
            s += "[H] ";
 
            for (int i = headPos + 1; i < tape.size(); i++) {
                s += tape.get(i) + " ";
            }
 
            return s + "]";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**Helper function returns just the digits of the tape, discarding formatting
     * niceties like commas. Useful when manually checking accuracy of arithmetic.
     * @param tape tape to be evaluated
     * @return just the digits (e.g., just the binary 0s and 1s)
     */
    public static String justDigits(List<String> tape) {
    	String returnDigits = "";
    	for (String s : tape) {
    		if (s == "1" || s == "0") {
        		returnDigits += s;
        	}
    	}
    	return returnDigits;
    }
    
    /**Helper function returns just the alpha characters of the tape, discarding
     * formatting niceties like commas. Useful when manually validating the accuracy
     * of unary operations
     * @param tape the tape to be evaluated
     * @return just the alpha characters on the tape
     */
    public static String justAlpha(List<String> tape) {
    	String returnAlpha = "";
    	for (String s : tape) {
    		if (s.matches("^[a-zA-Z]*$")) {
        		returnAlpha += s;
        	}
    	}
    	return returnAlpha;
    }
    
    /**Helper function returns just the A characters of the tape, discarding
     * formatting niceties like commas. Useful when manually validating the accuracy
     * of unary operations
     * @param tape the tape to be evaluated
     * @return just the alpha characters on the tape
     */
    public static String justA(List<String> tape) {
    	String returnA = "";
    	for (String s : tape) {
    		if (s.matches("A")) {
        		returnA += s;
        	}
    	}
    	return returnA;
    }
    
    /**Mutator method adds a transition to the transition table.
     * @param s transition set to add the transition to
     * @param base original state
     * @param in input symbol
     * @param to destination state
     * @param out output symbol to overwrite input symbol with
     * @param seek direction
     * @return
     */
    public static Set<Transition> addTransition(Set<Transition> s, String base, String in, String to, String out, int seek) {
    	s.add(new Transition(new StateTapeSymbolPair(base, in), new StateTapeSymbolPair(to, out), seek));
    	return s;
    }
    
   
    /**Turing machine to increment a binary operand
     * @param operand String representation of a binary operand to be incremented
     * @return the binary operand incremented by 1, String representation
     */
    public static String incrementTM(String operand) {    	
    	
    	
    	String init = "q0";		//state to enter execution at
    	String blank = "#";		//blank symbol
    	
    	//Make a set of halting states and add qF to it
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	//Create a set of transitions and add transitions to it
    	Set<Transition> trans = new HashSet<Transition>();

    	//Seek right edge
    	trans = addTransition(trans, "q0", "0", "q0", "0", 1);
        trans = addTransition(trans, "q0", "1", "q0", "1", 1);
        trans = addTransition(trans, "q0", "#", "q1", "#", -1);
    	//seek left, turning 1s to 0s until first 0, which is turned into 1
        trans = addTransition(trans, "q1", "0", "qF", "1", -1);
        trans = addTransition(trans, "q1", "1", "q1", "0", -1);
        trans = addTransition(trans, "q1", "#", "qF", "#", 1);
        
        
        //Initialize and run the TM
        DTM incrementMachine = new DTM(trans, term, init, blank);
        incrementMachine.initializeTape(operand);
        
        //Return the String representation of the binary with minimal formatting to ease validation.
        return justDigits(incrementMachine.runTM());
    }

    /**Turing machine to decrement a binary operand
     * @param operand String representation of a binary operand to be decremented
     * @return String representation of the decremented binary
     */
    public static String decrementTM(String operand) {
    	//Initialization, running and return are as per incrementTM above
    	//The transitions are obviously different. They are described in comments below.
    	String init = "q0";
    	String blank = "#";
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	Set<Transition> trans = new HashSet<Transition>();

    	//Take the 1s complement moving right
    	trans = addTransition(trans, "q0", "0", "q0", "1", 1);
        trans = addTransition(trans, "q0", "1", "q0", "0", 1);
        trans = addTransition(trans, "q0", "#", "q1", "#", -1);
    	//Add 1
        trans = addTransition(trans, "q1", "0", "q2", "1", 1);
        trans = addTransition(trans, "q1", "1", "q1", "0", -1);
        //Seek right
        trans = addTransition(trans, "q2", "0", "q2", "0", 1);
        trans = addTransition(trans, "q2", "1", "q2", "1", 1);
        trans = addTransition(trans, "q2", "#", "q3", "#", -1);
        //Take the 1s complement moving left
        trans = addTransition(trans, "q3", "0", "q3", "1", -1);
        trans = addTransition(trans, "q3", "1", "q3", "0", -1);
        trans = addTransition(trans, "q3", "#", "qF", "#", 1);
        
        
        
        DTM decrementMachine = new DTM(trans, term, init, blank);
        decrementMachine.initializeTape(operand);
        
        return justDigits(decrementMachine.runTM());
    }

    /**Helper method converts a binary operand into a unary operand
     * @param operand String representation of a binary operand to be converted to unary
     * @return String representation of a unary operand converted from binary operand
     */
    public static String unaryTM(String operand) {
    	String init = "q0";
    	String blank = "#";
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	Set<Transition> trans = new HashSet<Transition>();

    	//Take the 1s complement moving right. Drop delimiter D
    	trans = addTransition(trans, "q0", "0", "q0", "1", 1);
        trans = addTransition(trans, "q0", "1", "q0", "0", 1);
        trans = addTransition(trans, "q0", "#", "q1", "D", 1);
    	//Add 1
        trans = addTransition(trans, "q1", "0", "q2", "1", 1);
        trans = addTransition(trans, "q1", "1", "q1", "0", -1);
        trans = addTransition(trans, "q1", "#", "q1", "#", -1);
        trans = addTransition(trans, "q1", "D", "q1", "D", -1);
        //Seek right
        trans = addTransition(trans, "q2", "0", "q2", "0", 1);
        trans = addTransition(trans, "q2", "1", "q2", "1", 1);
        trans = addTransition(trans, "q2", "D", "q3", "D", -1);
        //Take the 1s complement moving left, then seek right past D to # and replace with A
        trans = addTransition(trans, "q3", "0", "q3", "1", -1);
        trans = addTransition(trans, "q3", "1", "q3", "0", -1);
        trans = addTransition(trans, "q3", "#", "q4", "#", 1);
        //Seek right, drop A
        trans = addTransition(trans, "q4", "0", "q4", "0", 1);
        trans = addTransition(trans, "q4", "1", "q4", "1", 1);
        trans = addTransition(trans, "q4", "D", "q4", "D", 1);
        trans = addTransition(trans, "q4", "A", "q4", "A", 1);
        trans = addTransition(trans, "q4", "#", "q5", "A", -1);
        //Seek left looking for 1s. Halt if not, seek all the way left if yes.
        trans = addTransition(trans, "q5", "0", "q5", "0", -1);
        trans = addTransition(trans, "q5", "1", "q6", "1", -1);
        trans = addTransition(trans, "q5", "D", "q5", "D", -1);
        trans = addTransition(trans, "q5", "A", "q5", "A", -1);
        trans = addTransition(trans, "q5", "#", "qF", "#", 1);
        //Seek all the way left, start again by taking the 1s complement to the right, no D
        trans = addTransition(trans, "q6", "0", "q6", "0", -1);
        trans = addTransition(trans, "q6", "1", "q6", "1", -1);
        trans = addTransition(trans, "q6", "D", "q6", "D", -1);
        trans = addTransition(trans, "q6", "#", "q7", "#", 1);
        //Take the 1s complement moving right. 
    	trans = addTransition(trans, "q7", "0", "q7", "1", 1);
        trans = addTransition(trans, "q7", "1", "q7", "0", 1);
        trans = addTransition(trans, "q7", "D", "q1", "D", -1);

        
        DTM unaryMachine = new DTM(trans, term, init, blank);
        unaryMachine.initializeTape(operand);
        
        return justA(unaryMachine.runTM());
    }
    
    /**Method implements the Turing Machine from Module 3
     * @param operand operand to be evaluated by the TM from Module 3
     * @return results of operations on the operand
     */
    public static String threeTM(String operand) {

    	
    	String init = "q0";
    	String blank = "b";
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qY");
    	term.add("qN");
        
    	Set<Transition> trans = new HashSet<Transition>();

    	trans = addTransition(trans, "q0", "0", "q0", "0", 1);
        trans = addTransition(trans, "q0", "1", "q0", "1", 1);
        trans = addTransition(trans, "q0", "b", "q1", "b", -1);
        trans = addTransition(trans, "q1", "0", "q2", "b", -1);
        trans = addTransition(trans, "q1", "0", "q2", "b", -1);
        trans = addTransition(trans, "q1", "1", "q3", "b", -1);
        trans = addTransition(trans, "q1", "b", "qN", "b", -1);
        trans = addTransition(trans, "q2", "0", "qY", "b", -1);
        trans = addTransition(trans, "q2", "1", "qN", "b", -1);
        trans = addTransition(trans, "q2", "b", "qN", "b", -1);
        trans = addTransition(trans, "q3", "0", "qN", "b", -1);
        trans = addTransition(trans, "q3", "1", "qN", "b", -1);
        trans = addTransition(trans, "q3", "b", "qN", "b", -1);   
        
        DTM threeMachine = new DTM(trans, term, init, blank);
        threeMachine.initializeTape(operand);
        
        return ((threeMachine.runTM(true)).toString());
    }
    
    /**Not used, but it works. This TM adds 2 binaries in place, natively.
     * @param input binary expression <binary1>+<binary2>
     * @return String representation of the binary sum
     */
    public static String addInPlaceTM(String input) {
    	String returnResult = "";
    	
    	String init = "q0";
    	String blank = "#";
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qY");
    	term.add("qN");
        
    	Set<Transition> trans = new HashSet<Transition>();
    	
    	trans = addTransition(trans, "q0", "0", "q0", "0", 1);
        trans = addTransition(trans, "q0", "1", "q1", "1", -1);
        trans = addTransition(trans, "q0", "+", "q10", "#", -1);
        trans = addTransition(trans, "q1", "0", "q1", "0", -1);
        trans = addTransition(trans, "q1", "1", "q1", "1", -1);
        trans = addTransition(trans, "q1", "#", "q2", "#", 1);
        trans = addTransition(trans, "q2", "0", "q2", "1", 1);
        trans = addTransition(trans, "q2", "1", "q2", "0", 1);
        trans = addTransition(trans, "q2", "+", "q3", "+", -1);
        trans = addTransition(trans, "q3", "0", "q4", "1", 1);
        trans = addTransition(trans, "q3", "1", "q3", "0", -1);
        trans = addTransition(trans, "q4", "0", "q4", "0", 1);
        trans = addTransition(trans, "q4", "1", "q4", "1", 1);
        trans = addTransition(trans, "q4", "+", "q5", "+", -1);
        trans = addTransition(trans, "q5", "0", "q5", "1", -1);
        trans = addTransition(trans, "q5", "1", "q5", "0", -1);
        trans = addTransition(trans, "q5", "#", "q6", "#", 1);
        trans = addTransition(trans, "q6", "0", "q6", "0", 1);
        trans = addTransition(trans, "q6", "1", "q6", "1", 1);
        trans = addTransition(trans, "q6", "+", "q7", "+", 1);
        trans = addTransition(trans, "q7", "0", "q7", "0", 1);
        trans = addTransition(trans, "q7", "1", "q7", "1", 1);
        trans = addTransition(trans, "q7", "#", "q8", "#", -1);
        trans = addTransition(trans, "q8", "0", "q9", "1", -1);
        trans = addTransition(trans, "q8", "1", "q8", "0", -1);
        trans = addTransition(trans, "q9", "0", "q9", "0", -1);
        trans = addTransition(trans, "q9", "1", "q9", "1", -1);
        trans = addTransition(trans, "q9", "+", "q9", "+", -1);
        trans = addTransition(trans, "q9", "#", "q0", "#", 1);
        trans = addTransition(trans, "q10", "0", "q10", "#", -1);
        trans = addTransition(trans, "q10", "#", "q11", "#", 1);
        trans = addTransition(trans, "q11", "0", "qF", "0", -1);
        trans = addTransition(trans, "q11", "1", "qF", "1", -1);
        trans = addTransition(trans, "q11", "#", "q11", "#", 1);
        
        return null;
    }
    
    /**Method builds a unary expression of the form AAAAADAAAAA.
     * D is the delimiter.
     * The number of As to the left and right represent the integer value of
     * the left and right operands, respectively.
     * @param expression The full expression of the form <binary operand><operator><binary operand>
     * @param splitter the character to split the expression on. coresponds to the operator
     * @return a unary expression of the form AAAADAAAA
     */
    public static String buildUnary(String expression, String splitter) {
    	
    	String leftOperand = expression.split(splitter)[0];
        String rightOperand = expression.split(splitter)[1];
        
        String unaryLeft = unaryTM(leftOperand);
        String unaryRight = unaryTM(rightOperand); 
        
        String returnString = unaryLeft + "D" + unaryRight;
        
        return returnString;
    }
    
    /**Method implements a turing machine that evaluates a unary expression
     * of the form AAAADAAAA, as per buildUnary.
     * @param unaryExpression the unary expression to be added.
     * @return the results of the evaluation in the form AAAAA, where the number of
     * 				As indicates the integer value of the result. As a convenience
     * 				the unary-to-decimal representation is returned as well.
     */
    public static String unaryAdd(String unaryExpression) {
    	
    	String init = "q0";
    	String blank = "#";
    	
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	Set<Transition> trans = new HashSet<Transition>();
    	
    	//Seek right to delimiter D
    	trans = addTransition(trans, "q0", "A", "q0", "A", 1);
        trans = addTransition(trans, "q0", "D", "q1", "D", 1);
        //Seek right to first A past the delimiter, turn it to D, then seek left to original D.
    	//If we hit blank, no more As, so delete Ds and halt
        trans = addTransition(trans, "q1", "D", "q1", "D", 1);
        trans = addTransition(trans, "q1", "A", "q2", "D", -1);
        trans = addTransition(trans, "q1", "#", "q3", "#", -1);
    	//Turn original D into an A. 
    	//We've just incremented the left operand and decremented the right operand.
        trans = addTransition(trans, "q2", "D", "q0", "A", 1);
        trans = addTransition(trans, "q2", "A", "q2", "A", -1);
        //Delete Ds and halt
        trans = addTransition(trans, "q3", "D", "q3", "#", -1);
        trans = addTransition(trans, "q3", "A", "q3", "A", -1);
        trans = addTransition(trans, "q3", "#", "qF", "#", 1);
    	
        DTM unaryAdd = new DTM(trans, term, init, blank);
        unaryAdd.initializeTape(unaryExpression);
        
        String result =  justA(unaryAdd.runTM());
        return ("Tape: " + result + " Unary to Decimal: " + result.length());
   
    }
    
    /**As per above, performs subtraction on a unary expression.
     * @param unaryExpression unary expression of the form AAADAAA, consistent with buildUnary.
     * @return subtraction result in unary form. As a convenience
     * 				the unary-to-decimal representation is returned as well.
     */
    public static String unarySubtract(String unaryExpression) {
    	
    	String init = "q0";
    	String blank = "#";
    	
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	Set<Transition> trans = new HashSet<Transition>();
    	
    	//Seek right all the way right
    	trans = addTransition(trans, "q0", "A", "q0", "A", 1);
    	trans = addTransition(trans, "q0", "D", "q0", "D", 1);
    	trans = addTransition(trans, "q0", "#", "q1", "#", -1);
        //Seek left to the delimiter. If no delimiter, halt.
        trans = addTransition(trans, "q1", "A", "q1", "A", -1);
        trans = addTransition(trans, "q1", "D", "q2", "D", 1);
        trans = addTransition(trans, "q1", "#", "qF", "#", 1);
    	//Seek right to the last A of the right operand. Make it blank, decrementing the right operand.
        //If no more As, begin cleanup and halt
        trans = addTransition(trans, "q2", "A", "q2", "A", 1);
        trans = addTransition(trans, "q2", "#", "q6", "#", -1);
        //Make the last A of the right operand blank. If we hit the delimiter first, no more As, halt
        trans = addTransition(trans, "q6", "A", "q3", "#", -1);
        trans = addTransition(trans, "q6", "D", "q5", "#", -1);
        //Seek left to the first A of the left operand.
        trans = addTransition(trans, "q3", "A", "q3", "A", -1);
        trans = addTransition(trans, "q3", "D", "q3", "D", -1);
        trans = addTransition(trans, "q3", "#", "q4", "#", 1);
        //Turn the first A of the left operand blank, decrementing it. Loop back to q0 and begin again
        trans = addTransition(trans, "q4", "A", "q0", "#", 1);
        //Seek left, clean up delimiter and halt
        trans = addTransition(trans, "q5", "A", "q5", "A", -1);
        trans = addTransition(trans, "q5", "D", "qF", "#", -1);
        trans = addTransition(trans, "q5", "#", "qF", "#", 1);
    	
        DTM unarySubtract = new DTM(trans, term, init, blank);
        unarySubtract.initializeTape(unaryExpression);
                
        String result =  justA(unarySubtract.runTM());
        return ("Tape: " + result + " Unary to Decimal: " + result.length());
   
    }
    
    /**As per above, method implements unary multiplication.
     * @param unaryExpression unary expression consistent with buildUnary.
     * @return results of the multiplication operation in unary format. As a convenience
     * 				the unary-to-decimal representation is returned as well.
     */
    public static String unaryMultiply(String unaryExpression) {
    	
    	String init = "q0";
    	String blank = "#";
    	
    	
    	Set <String> term = new HashSet<String>();
    	term.add("qF");
        
    	Set<Transition> trans = new HashSet<Transition>();
    	
    	//C = decremented right operand
    	//B = decremented left operand
    	//E = equals. Everything to the right represents the result.
    	
    	//Seek right all the way right, making the last blank E. Everything
    	//to the right of E will be our answer
    	trans = addTransition(trans, "q0", "A", "q0", "A", 1);
    	trans = addTransition(trans, "q0", "D", "q0", "D", 1);
    	trans = addTransition(trans, "q0", "#", "q1", "E", 1);
        //Seek left to the delimiter. 
        trans = addTransition(trans, "q1", "A", "q1", "A", -1);
        trans = addTransition(trans, "q1", "B", "q1", "B", -1);
        trans = addTransition(trans, "q1", "E", "q1", "E", -1);
        trans = addTransition(trans, "q1", "C", "q1", "C", -1);
        trans = addTransition(trans, "q1", "#", "q1", "#", -1);
        trans = addTransition(trans, "q1", "D", "q2", "D", 1);
    	//Seek right to the last A of the right operand. Make it C, decrementing the right operand.
        //If no more As, begin cleanup and halt
        trans = addTransition(trans, "q2", "A", "q3", "C", -1);
        trans = addTransition(trans, "q2", "C", "q2", "C", 1);
        trans = addTransition(trans, "q2", "E", "q9", "E", -1);
        //Seek left to the edge of the left operand
        trans = addTransition(trans, "q3", "A", "q3", "A", -1);
        trans = addTransition(trans, "q3", "B", "q3", "B", -1);
        trans = addTransition(trans, "q3", "C", "q3", "C", -1);
        trans = addTransition(trans, "q3", "D", "q3", "D", -1);
        trans = addTransition(trans, "q3", "E", "q3", "E", -1);
        trans = addTransition(trans, "q3", "#", "q4", "#", 1);
        //Seek right to the first A. Mark it B, temporarily decrementing the left operand.
        //If we hit the delimiter, we're done with this round and need to change B's back to A's
        trans = addTransition(trans, "q4", "A", "q5", "B", 1);
        trans = addTransition(trans, "q4", "B", "q4", "B", 1);
        trans = addTransition(trans, "q4", "D", "q7", "D", -1);
        //Seek right to the edge of the answer, adding an A
        trans = addTransition(trans, "q5", "A", "q5", "A", 1);
        trans = addTransition(trans, "q5", "B", "q5", "B", 1);
        trans = addTransition(trans, "q5", "C", "q5", "C", 1);
        trans = addTransition(trans, "q5", "D", "q5", "D", 1);
        trans = addTransition(trans, "q5", "E", "q5", "E", 1);
        trans = addTransition(trans, "q5", "#", "q6", "A", 1);
        //Loop back to q3
        trans = addTransition(trans, "q6", "#", "q3", "#", -1);
        //Seek left, changing Bs to As
        trans = addTransition(trans, "q7", "B", "q7", "A", -1);
        trans = addTransition(trans, "q7", "#", "q8", "#", 1);
        //Seek all the way right, loop back to q1
        trans = addTransition(trans, "q8", "A", "q8", "A", 1);
        trans = addTransition(trans, "q8", "B", "q8", "B", 1);
        trans = addTransition(trans, "q8", "C", "q8", "C", 1);
        trans = addTransition(trans, "q8", "D", "q8", "D", 1);
        trans = addTransition(trans, "q8", "E", "q8", "E", 1);
        trans = addTransition(trans, "q8", "#", "q1", "#", -1);
        //Seek all the way left, blanking everything
        trans = addTransition(trans, "q9", "A", "q9", "#", -1);
        trans = addTransition(trans, "q9", "B", "q9", "#", -1);
        trans = addTransition(trans, "q9", "C", "q9", "#", -1);
        trans = addTransition(trans, "q9", "D", "q9", "#", -1);
        trans = addTransition(trans, "q9", "E", "q9", "#", -1);
        trans = addTransition(trans, "q9", "#", "qF", "#", 1);
        
        DTM unaryMultiply = new DTM(trans, term, init, blank);
        unaryMultiply.initializeTape(unaryExpression);
                
        
        String result =  justA(unaryMultiply.runTM());
        return ("Tape: " + result + " Unary to Decimal: " + result.length());
   
    }    
    
    /**Helper method to read expressions from a file.
     * @param fileName path to file.
     * @return list of expressions.
     */
    public static List<String> readExpressions (String fileName) {
		
		//Declare List of Points to be returned.
		List<String> expressions = new ArrayList<String>();
		
		//Open the file pointed to by fileName
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			
			//String to hold one line of the file.
			String line;
			
			//While lines remain...
			while ((line = br.readLine()) != null) {
				//Split the line on the space.
				expressions.add(line);
				
			}
			br.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
		//Return list of Points read from the file.
		return expressions;
		
	}
    
    /**Helper method to append text to a file. Used to write results back to the input file.
     * @param filePath path to file.
     * @param textToAppend text to be appended.
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
    

    /**The main function runs tests based on user input
     * @param args 	1. Specify the TM to be run:
     * 					"module3"
     * 					"add"
     * 					"subtract"
     * 					"multiply"
     * 				2. specify the input file with expressions to be evaluated.
     * 				WARNING: little to no input validation is performed. It is assumed that
     * 				expressions evaluate to positive integers. 
     */
    public static void main(String[] args) {
    	String operation = args[0];
    	String inFile = args[1];
    	boolean quiet = false;
    	List<String> expressions;
    	switch (operation) {
    		case "module3":
    			quiet = false;
    			expressions = readExpressions(inFile);	//read tapes from file
    			appendToFile(inFile, ("Module 3 Machine Results " + LocalDateTime.now() +":")); //append results section to file
    			if (expressions.size() > 30) {		//suppress console output if > 30 tapes to evaluate
    				quiet = true;
    			}
    			for(String operand : expressions) {		//evaluate each tape and append to the file. optionally output to console.
    				String m3Result = threeTM(operand);
    				appendToFile(inFile, m3Result);
    				if(!quiet) { 
    					System.out.println(m3Result);
    				}
    			}
    			break;
    		case "add":
    			quiet = false;
    			expressions = readExpressions(inFile);
    			appendToFile(inFile, ("Addition Results " + LocalDateTime.now() +":"));
    			if (expressions.size() > 30) {
    				quiet = true;
    			}
    			for(String operand : expressions) {
    				String unaryOperand = buildUnary(operand, "\\+");		//build a unary expression from the binary tape
    				System.out.println(unaryOperand);						//evaluate the unary expression
    				String addResult = unaryAdd(unaryOperand);
    				appendToFile(inFile, addResult);
    				if(!quiet) { 
    					System.out.println(addResult);
    				}
    			}
    			break;
    		case "subtract":
    			quiet = false;
    			expressions = readExpressions(inFile);
    			appendToFile(inFile, ("Subtraction Results " + LocalDateTime.now() +":"));
    			if (expressions.size() > 30) {
    				quiet = true;
    			}
    			for(String operand : expressions) {
    				String unaryOperand = buildUnary(operand, "\\-");
    				System.out.println(unaryOperand);
    				String addResult = unarySubtract(unaryOperand);
    				appendToFile(inFile, addResult);
    				if(!quiet) { 
    					System.out.println(addResult);
    				}
    			}
    			break;
    		case "multiply":
    			quiet = false;
    			expressions = readExpressions(inFile);
    			appendToFile(inFile, ("Multiplication Results " + LocalDateTime.now() +":"));
    			if (expressions.size() > 30) {
    				quiet = true;
    			}
    			for(String operand : expressions) {
    				String unaryOperand = buildUnary(operand, "\\*");
    				System.out.println(unaryOperand);
    				String addResult = unaryMultiply(unaryOperand);
    				appendToFile(inFile, addResult);
    				if(!quiet) { 
    					System.out.println(addResult);
    				}
    			}
    			break;
    		default:
    			System.out.println("First argument must be one of the following:");
    			System.out.println("module3: The module 3 example TM.");
    			System.out.println("add: Converts binary to unary, then adds.");
    			System.out.println("subtract: Converts binary to unary, then subtracts.");
    			System.out.println("multiply: Converts binary to unary, then multiplies.");
    			break;
    	}
    	
    	//old attempts that I might need to refer to:
    	
        /*String exampleThree = threeTM("10100bb");
        //System.out.println(exampleThree);
       
        //TODO: remember to add code checking forleading zero
        String userInput = "011111+011111";
        String leftOperand = userInput.split("\\+")[0];
        String rightOperand = userInput.split("\\+")[1];
        String result = leftOperand;
        while (Integer.parseInt(rightOperand, 2) > 0) {
        	rightOperand = decrementTM(rightOperand);
        	result = incrementTM(result);
        }
        //System.out.println(result);
        
        userInput = "011111-000001";
        leftOperand = userInput.split("\\-")[0];
        rightOperand = userInput.split("\\-")[1];
        result = leftOperand;
        while (Integer.parseInt(rightOperand, 2) > 0) {
        	rightOperand = decrementTM(rightOperand);
        	result = decrementTM(result);
        }
        //System.out.println(result);
        
        //TODO: add padding zeroes
        userInput = "00111*00011";
        leftOperand = userInput.split("\\*")[0];
        rightOperand = userInput.split("\\*")[1];
        result = leftOperand;
        while(Integer.parseInt(rightOperand, 2) > 1) {
        	rightOperand = decrementTM(rightOperand);
        	String innerLeft = leftOperand;
        	while (Integer.parseInt(innerLeft, 2) > 0) {
        		innerLeft = decrementTM(innerLeft);
        		result = incrementTM(result);
        	}
        	
        }
        //System.out.println(result);
        
        userInput = "000000000000111";
        result = unaryTM(userInput);
        System.out.println(result);
        
        result = buildUnary("00111*00011", "\\*");
        System.out.println(result);
        
        //System.out.println(unaryAdd(result));
        
        /*result = unarySubtract(buildUnary("01111111-01111", "\\-"));
        System.out.println(result);
        System.out.println(result.length());*/
        
        //String result = unaryMultiply(buildUnary("01111*01", "\\*"));
        //System.out.println(result);
        //System.out.println(result.length());
        
        //String s = incrementTM("011111");
        //System.out.println(s);
        
        //s = decrementTM("0001");
        //System.out.println(s);
        //s = decrementTM("1");
        //System.out.println(s);
        
    }
    
}
