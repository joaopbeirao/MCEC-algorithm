package Entropy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import weka.core.Instances;

public class Groups {
	
	// Number of rows (entries)
	public int nrRows;
	// Number of columns
	public int nrColumns;
	// Number of dimensions (features)
	public int nrDims;
	// Length of time series (time points)
	public int m;
	// Conditional Entropy H(C|AB)
	private double hCAB = 0d;
	
	// List with the counting for each case of group AC
	private HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>> listAC;
	// List with the counting for each case of group A
	private HashMap<ArrayList<String>, Double> listA;
	// List with the counting for each case of group B
	private HashMap<ArrayList<String>, Double> listB;
	// List with the counting for each case of group C
	private HashMap<String, Double> listC;
	// List with the counting for each case of group ABC
	private HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>> listABC;
	// List with the counting for each case of group AB
	private HashMap<ArrayList<String>, Double> listAB;
	// Auxiliary list with the counting for each case of group ABC
	private HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Map.Entry<ArrayList<String>, ArrayList<String>>>> auxListABC;
	
	// output data
	public String outputData;
	
	// Constructor
	public Groups(int rows, int columns, int dims){
		// initialisation of the three integers
		nrRows = rows;
		nrColumns = columns;
		nrDims = dims;
		m = ((columns-1)/dims); // length of time series
		outputData = "";
	}
	
	// Cleans all lists
	public void cleanLists(){
		// initialisation of the lists
		listAC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>>();
		listA = new HashMap<ArrayList<String>, Double>();
		listB = new HashMap<ArrayList<String>, Double>();
		listC = new HashMap<String, Double>();
		listAB = new HashMap<ArrayList<String>, Double>();
		listABC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>>();
		auxListABC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Map.Entry<ArrayList<String>, ArrayList<String>>>>();
	}
	
	// Gets data from Excel sheets and returns a 2D array
	public String[][] getData(Instances sheet){
		
        String[][] data = new String[nrRows][nrColumns];
		
		// i --> iterating over each row
		for (int i = 0; i < nrRows-1; i++) {
			// j --> iterating over each column
			for (int j = 0; j < nrColumns; j++) {
				if (i == 0){
					data[i][j] = sheet.attribute(j).name();
					data[i+1][j] = sheet.instance(i).toString(j);
				} else {
					data[i+1][j] = sheet.instance(i).toString(j);
				}
			}
		}
		
		return data;
	}
	
	// Counts the number of occurrences of each key for each group
	// [check] is to produce some lists only once
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void generateCount(int n, String[][] data){
		
		// initialisation of the lists
		listAC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>>();
		listA = new HashMap<ArrayList<String>, Double>();
		listB = new HashMap<ArrayList<String>, Double>();
		listAB = new HashMap<ArrayList<String>, Double>();
		listABC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Double>>();
		listC = new HashMap<String, Double>();
		auxListABC = new HashMap<ArrayList<String>, Map.Entry<ArrayList<String>, Map.Entry<ArrayList<String>, ArrayList<String>>>>();
		
		ArrayList<String> strAB; // string to save the key for Group AB
		ArrayList<String> strABC; // string to save the key for Group ABC
		ArrayList<String> strA; // string to save the key for Group A
		ArrayList<String> strB; // string to save the key for Group B
		ArrayList<String> strAC; // string to save the key for Group AC
    	String strC; // string to save the key for Group C
    	
    	String auxStr; // auxiliary string for concatenating the data from all dimensions
    	
    	int timepoint; // time point cursor
    	int idCol; // column cursor
    	
    	// iterating over rows (entries)
    	// first row contains the attribute names, so data starts on the second
    	for(int idRow = 1; idRow < nrRows; idRow++){
    		
    		strAB = new ArrayList<String>();
    		strABC = new ArrayList<String>();
    		strA = new ArrayList<String>();
    		strB = new ArrayList<String>();
    		strAC = new ArrayList<String>();
        	strC = "";
    		
        	timepoint = 0;
    		// iterating over time points
        	idCol = 0;
        	while(timepoint < m){
    			// iterating over dimensions (features)
    			auxStr = "";
            	for(int idDim = 0; idDim < nrDims; idDim++){
            		// concatenate data from all dimensions (features)
            		auxStr = auxStr + data[idRow][idCol];
            		idCol++;
            	}
            	if(!auxStr.equals("")){
	            	if(timepoint < n){
	            		strA.add(auxStr); // Group A = {(X1, ..., Xn)}
	            		strAC.add(auxStr); // Group AC = {(X1, ..., Xn) + class}
	            	} else {
	            		strB.add(auxStr); // Group B = {(Xn+1, ..., Xm)} 
	            	}
	            	strAB.add(auxStr); // Group AB = {(X1, ..., Xm)}
	            	strABC.add(auxStr); // Group ABC = {(X1, ..., Xm) + class}
            	}
            	timepoint++;
    		}
    		
        	// ========================== Filling the lists =============================
    		strC = data[idRow][nrColumns-1]; // last column is the class
    		
    		strAC.add(strC); // Add class to Group AC
			strABC.add(strC); // Add class to Group ABC
			
			// Group C
    		if(!strC.isEmpty()){
	        	if(listC.containsKey(strC)){
	        		listC.put(strC, listC.get(strC)+1.0); // increment if already exists
	        	} else {
	        		listC.put(strC, 1.0); // create new with counter equals to 1 if it is a new key (remember it is double, so 1.0)
	        	}
    		}
			
			// Group AB
    		if(!strAB.isEmpty()){
	        	if(listAB.containsKey(strAB)){
	        		listAB.put(strAB, listAB.get(strAB)+1.0); // increment if already exists
	        	} else {
	        		listAB.put(strAB, 1.0); // create new with counter equals to 1 if it is a new key (remember it is double, so 1.0)
	        	}
    		}
    		
    		// Group ABC
    		if(!strABC.isEmpty()){
	        	if(listABC.containsKey(strABC)){
	        		// [value] corresponds to class {C} and counter of occurrences
	        		listABC.put(strABC, new AbstractMap.SimpleEntry(strAB, listABC.get(strABC).getValue()+1.0)); // increment if already exists
	        	} else {
	        		// [value] corresponds to class {C} and counter of occurrences
	        		listABC.put(strABC, new AbstractMap.SimpleEntry(strAB, 1.0)); // create new with counter one if it is a new key
	        	}
    		}
    		
    		// Group A
    		if(!strA.isEmpty()){
	        	if(listA.containsKey(strA)){
	        		listA.put(strA, listA.get(strA)+1.0); // increment if already exists
	        	} else {
	        		listA.put(strA, 1.0); // create new with counter equals to 1 if it is a new key (remember it is double, so 1.0)
	        	}
    		}
    		
    		// Group B
    		if(!strB.isEmpty()){
	        	if(listB.containsKey(strB)){
	        		listB.put(strB, listB.get(strB)+1.0); // increment if already exists
	        	} else {
	        		listB.put(strB, 1.0); // create new with counter equals to 1 if it is a new key (remember it is double, so 1.0)
	        	}
    		}
    		
    		// Group AC
    		if(!strAC.isEmpty()){
	        	if(listAC.containsKey(strAC)){
	        		listAC.put(strAC, new AbstractMap.SimpleEntry(strA, listAC.get(strAC).getValue()+1.0)); // increment if already exists
	        	} else {
	        		listAC.put(strAC, new AbstractMap.SimpleEntry(strA, 1.0)); // create new with counter equals to 1 if it is a new key
	        	}
    		}
    		
    		// Auxiliary Group ABC
    		if(!strABC.isEmpty()){
    			// [value1] is groupA; [value2] is groupB; [value3] is groupC
    			// there is no counter of occurrences
    			auxListABC.put(strABC, new AbstractMap.SimpleEntry(strAB, new AbstractMap.SimpleEntry(strAC, strA)));
    		}
        	// ============================================================================
    		
    	}
    	
	}

	// Save data in the static String [outputData] for printing in the terminal and/or save in a file
	@SuppressWarnings("rawtypes")
	public void printDatabase() {
		
		// ListA
		outputData = outputData + "============= ListA has " + listA.size() + " entries =============\n";
		Set set = listA.entrySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListB
		outputData = outputData + "============= ListB has " + listB.size() + " entries =============\n";
		set = listB.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListC
		outputData = outputData + "============= ListC has " + listC.size() + " entries =============\n";
		set = listC.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListAB
		outputData = outputData + "============= ListAB has " + listAB.size() + " entries =============\n";
		set = listAB.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListAC
		outputData = outputData + "============= ListAC has " + listAC.size() + " entries =============\n";
		set = listAC.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListABC
		outputData = outputData + "============= ListABC has " + listABC.size() + " entries =============\n";
		set = listABC.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
		// ListABC
		outputData = outputData + "============= ListAuxABC has " + auxListABC.size() + " entries =============\n";
		set = auxListABC.entrySet();
		iterator = set.iterator();
		while(iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
		    outputData = outputData + "key is: " + mentry.getKey() + " & Value is: ";
		    outputData = outputData + mentry.getValue() + "\n";
		}
		outputData = outputData + "\n";
		
	}
	
	// computation of the difference in entropy
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double computeEntropy(){
		
		double valA = 0d; // value from listA
		double valAB = 0d; // value from listAB
		double valAC = 0d; // value from listAC
		double valABC = 0d; // value from listABC
		
		double pA = 0d; // probability P(A)
		double pAB = 0d; // probability P(A&B)
		double pAC = 0d; // probability P(A&C)
		double pABC = 0d; // probability P(A&B&C)
		
		double hCA = 0d; // conditional entropy H(C|A)
		hCAB = 0d; // conditional entropy H(C|AB)
		
		// iterate over listA
		Iterator iteratorA = listA.entrySet().iterator();
		while(iteratorA.hasNext()) {
			Map.Entry mentryA = (Map.Entry) iteratorA.next();
			
			// number of times keyA occurs
			valA = ((Double) mentryA.getValue()).doubleValue();
			
			// calculate probability of each occurrence
			pA = ((double) (valA/(nrRows-1))); // P(A)
			
			// save the probability in the HashMap [listA]
			mentryA.setValue(pA);
		}
		
		// iterate over listAC
		Iterator iteratorAC = listAC.entrySet().iterator();
		while(iteratorAC.hasNext()) {
			Map.Entry mentryAC = (Map.Entry) iteratorAC.next();
			
			// number of times keyAC occurs
			valAC = ((Double) ((Entry<ArrayList<String>, Double>) mentryAC.getValue()).getValue()).doubleValue();
			
			// ================ H(C|A) ================
			pAC = ((double) (valAC/(nrRows-1))); // P(A&C)
			// gets the probability of A from the HashMap [listA]
			pA = listA.get(((Entry<ArrayList<String>, Double>) mentryAC.getValue()).getKey()).doubleValue(); // P(A)
			// if denominator is zero, not considered
			if(pAC > 0.0)
				hCA = hCA + pAC*(Math.log(pA/pAC)/Math.log(2)); // H(C|A)
			// ========================================
		}
		
		// iterate over listAB
		Iterator iteratorAB = listAB.entrySet().iterator();
		while(iteratorAB.hasNext()) {
			Map.Entry mentryAB = (Map.Entry) iteratorAB.next();
			
			// number of times keyA occurs
			valAB = ((Double) mentryAB.getValue()).doubleValue();
			
			// calculate probability of each occurrence
			pAB = ((double) (valAB/(nrRows-1))); // P(A&B)
			
			// save the probability in the HashMap [listAB]
			mentryAB.setValue(pAB);
		}
	
		// iterate over listABC
		// *** This calculation is fixed when varying n, so it should be done only once ***
		Iterator iteratorABC = listABC.entrySet().iterator();
		while(iteratorABC.hasNext()) {
			Map.Entry mentryABC = (Map.Entry) iteratorABC.next();
			
			// number of times keyAC occurs
			valABC = ((Double) ((Entry<ArrayList<String>, Double>) mentryABC.getValue()).getValue()).doubleValue();
			
			// ================ H(C|A) ================
			pABC = (valABC/(nrRows-1)); // P(A&B&C)
			// gets the probability of C from the HashMap [listAB]
			pAB = listAB.get(((Entry<ArrayList<String>, Double>) mentryABC.getValue()).getKey()).doubleValue(); // P(A&B)
			// if denominator is zero, not considered
			if(pABC > 0.0)
				hCAB = hCAB + pABC*(Math.log(pAB/pABC)/Math.log(2)); // H(C|AB)
			// ========================================
			
		}
		
		outputData = outputData + "H(C|AB) = " + String.format(Locale.ROOT, "%.4f", hCAB) + "\n";
		outputData = outputData + "H(C|A) = " + String.format(Locale.ROOT, "%.4f", hCA) + "\n";
		outputData = outputData + "H(C|A) - H(C|AB) = " + String.format(Locale.ROOT, "%.4f", hCA-hCAB) + "\n";
		
		return (hCA-hCAB);
	}
	
	// compute MDL and AIC scoring functions
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double[] computeMDL_AIC(String[][] dataArray, int n){
		
		double alpha_AIC = 0d; // penalization on complexity
		double alpha_MDL = 0d; // penalization on complexity
		double beta = 0d; // number of free parameters present in the model
		double sum = 0d; // result of the sum of probabilities
		double pA = 0d; // probability P(A)
		double pAB = 0d; // probability P(A&B)
		double pAC = 0d; // probability P(A&C)
		
		double valA = 0d; // value from listA
		double valAB = 0d; // value from listAB
		double valAC = 0d; // value from listAC
		double valABC = 0d; // value from listABC
		
		double nrKeysA = listA.size(); // number of free parameters group A
		double nrKeysC = listC.size(); // number of free parameters group C
		
		// MDL or AIC scoring function
		alpha_AIC = 1; // AIC score
		alpha_MDL = (Math.log(nrRows-1)/Math.log(2))/2.0; // MDL score
		
		beta = ((Double) (nrKeysA*nrKeysC-1.0)).doubleValue(); // beta = #A*#C-1
		
		// iterate over listA
		Iterator iteratorA = listA.entrySet().iterator();
		while(iteratorA.hasNext()) {
			Map.Entry mentryA = (Map.Entry) iteratorA.next();
			
			// number of times keyA occurs
			valA = ((Double) mentryA.getValue()).doubleValue();
			
			// calculate probability of each occurrence
			pA = ((double) (valA/(nrRows-1))); // P(A)
			
			// save the probability in the HashMap [listA]
			mentryA.setValue(pA);
		}
		
		// iterate over listAB
		Iterator iteratorAB = listAB.entrySet().iterator();
		while(iteratorAB.hasNext()) {
			Map.Entry mentryAB = (Map.Entry) iteratorAB.next();
			
			// number of times keyA occurs
			valAB = ((Double) mentryAB.getValue()).doubleValue();
			
			// calculate probability of each occurrence
			pAB = ((double) (valAB/(nrRows-1))); // P(A&B)
			
			// save the probability in the HashMap [listAB]
			mentryAB.setValue(pAB);
		}
		
		// iterate over listAC
		Iterator iteratorAC = listAC.entrySet().iterator();
		while(iteratorAC.hasNext()) {
			Map.Entry mentryAC = (Map.Entry) iteratorAC.next();
			
			// number of times keyAC occurs
			valAC = ((Double) ((Entry<ArrayList<String>, Double>) mentryAC.getValue()).getValue()).doubleValue();
			
			// calculate probability of each occurrence
			pAC = ((double) (valAC/(nrRows-1))); // P(A&C)

			// save the probability in the HashMap [listAC]
			((Entry<ArrayList<String>, Double>) mentryAC.getValue()).setValue(pAC);
		}
		
		// iterate over auxListABC
		Iterator iteratorABC = auxListABC.entrySet().iterator();
		Iterator auxIteratorABC = listABC.entrySet().iterator();
		while(iteratorABC.hasNext()) {
			Map.Entry mentryABC = (Map.Entry) iteratorABC.next();
			Map.Entry auxMentryABC = (Map.Entry) auxIteratorABC.next();
			
			pAB = listAB.get(((Entry<ArrayList<String>, Entry<ArrayList<String>, ArrayList<String>>>) mentryABC.getValue()).getKey()).doubleValue(); // P(A&B)
			pAC = (((Entry<ArrayList<String>, Double>) listAC.get(((Entry<ArrayList<String>, ArrayList<String>>) ((Entry<ArrayList<String>, Entry<ArrayList<String>, ArrayList<String>>>) mentryABC.getValue()).getValue()).getKey())).getValue()).doubleValue(); // P(A&C)
			pA = listA.get(((Entry<ArrayList<String>, ArrayList<String>>) ((Entry<ArrayList<String>, Entry<ArrayList<String>, ArrayList<String>>>) mentryABC.getValue()).getValue()).getValue()); // P(A)
			
			// number of times keyABC occurs
			valABC = ((Double) ((Entry<ArrayList<String>, Double>) auxMentryABC.getValue()).getValue()).doubleValue();
			// compute sum
			sum = (sum + (Math.log((pAB*pAC)/pA)/Math.log(2))*valABC);
			
		}
		
		double[] scoreResult = new double[3];
		
		scoreResult[0] = ((alpha_MDL*beta)-sum);
		scoreResult[1] = ((alpha_AIC*beta)-sum);
		scoreResult[2] = (-sum);
		outputData = outputData + "MDL = " + String.format(Locale.ROOT, "%.4f", scoreResult[0]) + "\n";
		outputData = outputData + "AIC = " + String.format(Locale.ROOT, "%.4f", scoreResult[1]) + "\n";
		outputData = outputData + "LL = " + String.format(Locale.ROOT, "%.4f", scoreResult[2]) + "\n";
		
		return (scoreResult);
	}
	
	// Saves a specific [data] in a specific [fileName].txt file
	public void saveDataTextFile(String fileName, String data) throws IOException{
	    FileWriter newFile = new FileWriter(fileName + ".txt"); // Create file 
	    BufferedWriter out = new BufferedWriter(newFile);
	    out.write(data); // write in the file
	    out.close(); // close the file
	}
	
}
