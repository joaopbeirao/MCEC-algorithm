package Main;

import java.io.File;
import java.util.Locale;
import java.util.Random;

import Measurements.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// For analysing computation time
		long startTime = System.currentTimeMillis();
		
		// In case no arguments were given
		if(args.length < 4){
			System.out.println("Please insert the terminal commands.");
			System.out.println("Invalid arguments to run MCEC algorithm. \n");
			System.out.println("Format: java -jar MCECalgorithm.jar <<dataset-filename>>.csv [N] [optionClass] [MarkovLag]");
			System.out.println("[N] - number of features per time point");
			System.out.println("[optionClass] - boolean: TRUE = with classification; FALSE = without classification");
			System.out.println("[MarkovLag] - boolean: TRUE = with Markov lag; FALSE = standard Early Classification");
			System.exit(1);
		}
		
		// =============== INPUT INFORMATION ===============
		// File where data is contained
		String inputFile = args[0];
		
		// Number of dimensions (features)
		int nrDimensions = Integer.parseInt(args[1]);
				
		// if classCheck = TRUE --> WITH classification
		// if classCheck = FALSE --> WITHOUT classification
		boolean classCheck = Boolean.parseBoolean(args[2]);
		
		// if MarkovLag = TRUE --> with Markov lag
		// if MarkovLag = FALSE --> standard Early Classification
		boolean MarkovLag = Boolean.parseBoolean(args[3]);
		// =======================================================
		
		// Create File
		File file = new File(inputFile);

		// Load CVS file
		CSVLoader loader = new CSVLoader();
		loader.setSource(file);
		Instances data = loader.getDataSet(); // Get instances object
		data.setClassIndex(data.numAttributes() - 1); // class label is the last attribute
		
		// Create Groups object
		Groups database = new Groups(data.numInstances()+1, data.numAttributes(), nrDimensions);
		
		// Get data from CSV file
		String[][] dataArray = database.getData(data, MarkovLag);
		
		// Variables
		double entropyDiff; // {H(C|A)-H(C|AB)}
		double[] scoringFunc; // {MDL,AIC}
		double[] nArray = new double[database.m]; // array with early classification time point
		double[] entropyDiffArray = new double[database.m]; // array with the values of all {entropyDiff}
		double[] MDLArray = new double[database.m]; // array with the values of all {scoringFunc[0]}
		double[] AICArray = new double[database.m]; // array with the values of all {scoringFunc[1]}
		double[] LLArray = new double[database.m]; // array with the values of all {scoringFunc[2]}
		
		// Auxiliary variables
		Double[][] outputDIFF = new Double[2][database.m];
		Double[][] outputMDL = new Double[2][database.m];
		Double[][] outputAIC = new Double[2][database.m];
		Double[][] outputLL = new Double[2][database.m];
		
		// n = early classification time point
		// Loop for {n} from {1, ..., L}
		for(int n = 1; n <= database.m; n++){
			database.outputData = database.outputData + "********\n[n = " + n + "]\n";
			
			// Create groups A, B and C for given {n}
			database.generateCount(n, dataArray);
			
			// print list A, B and C for given n
			//database.printDatabase();
			
			// ======================== ENTROPY DIFFERENCE ========================
			// compute difference in entropy for given {n}
			entropyDiff = database.computeEntropy();
			// save result
			nArray[n-1] = n;
			outputDIFF[1][n-1] = entropyDiff;
			
			// Auxiliary variables
			outputDIFF[0][n-1] = (double) n;
			entropyDiffArray[n-1] = entropyDiff;
			// ====================================================================
			
			// ========================= BIC/MDL and AIC ==========================
			// compute MDL and AIC for given {n}
			scoringFunc = database.computeMDL_AIC(dataArray, n);
			// save result
			MDLArray[n-1] = scoringFunc[0];
			AICArray[n-1] = scoringFunc[1];
			LLArray[n-1] = scoringFunc[2];
			
			// Auxiliary variables
			outputAIC[0][n-1] = (double) n;
			outputMDL[0][n-1] = (double) n;
			outputLL[0][n-1] = (double) n;
			outputMDL[1][n-1] = scoringFunc[0];
			outputAIC[1][n-1] = scoringFunc[1];
			outputLL[1][n-1] = scoringFunc[2];
			// ====================================================================
			
			database.outputData = database.outputData + "[n = " + n + "]\n********\n\n\n";
		}
		
		// Print output in the terminal
		//System.out.print(database.outputData);
		
		// ===============================================
		
		// For analysing computation time
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Computation Time = " + totalTime + " milliseconds.");
		System.out.println("File name: " + file.getName());
		System.out.println("Dataset information: N = " + String.valueOf(database.nrDims) + "; w = " + String.valueOf(database.nrRows-1) + "; L = " + String.valueOf(database.m));
		
		// ==================== CLASSIFICATION LOOP =====================
		// Variables for plotting
		double[] LArray = new double[database.m];
		double[] NBArray = new double[database.m];
		double[] BNArray = new double[database.m];
		double[] SMOArray = new double[database.m];
		double[] J48Array = new double[database.m];
		double[] REPTreeArray = new double[database.m];
		double[] RandForestArray = new double[database.m];
		double[] KNNArray = new double[database.m];
		
		int idxRow = 0; // index for rows
		
		// Auxiliary variables
		Double[][] classResultArray = new Double[data.numAttributes()-1][9];
		String outputClass = ""; // outputString
		
		if (classCheck){		
			// Classification loop while removing the time points
			// for each time, the last time point is removed until the first is the only one left --> standard Early Classification
			// for each time, the first time point is removed until the last is the only one left --> Markov Lag approach
		    while(((data.numAttributes()-1)/nrDimensions) >= 1){
		    	
		    	System.out.println("==============");
		    	System.out.println("Time Length = " + ((data.numAttributes()-1)/nrDimensions));
		    	data.setClassIndex(data.numAttributes()-1); // class label is last attribute
			    
			    // Save information
		    	LArray[idxRow] = (double) ((data.numAttributes()-1)/nrDimensions);
		    	
		    	// Auxiliary variables
		    	classResultArray[idxRow][0] = (double) ((data.numAttributes()-1)/nrDimensions);
		    	outputClass = outputClass + String.valueOf((data.numAttributes()-1)/nrDimensions);
		 	    
		 	    // =============== NAIVE BAYES ===============
		 	    // Naive Bayes classifier
				NaiveBayes nB = new NaiveBayes();
				nB.buildClassifier(data);
				Evaluation evalNB = new Evaluation(data);
				evalNB.crossValidateModel(nB, data, 10, new Random(1));
				System.out.println("NaiveBayes done");
				
				// Save information
				NBArray[idxRow] = evalNB.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][1] = evalNB.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalNB.pctCorrect());
				// ===========================================
				
				// ================ BAYES NET ================
				// Bayes Net classifier
				BayesNet bN = new BayesNet();
				bN.buildClassifier(data); // discrete data
				Evaluation evalBN = new Evaluation(data);
				evalBN.crossValidateModel(bN, data, 10, new Random(1));
				System.out.println("BayesNet done");
				
				// Save information
				BNArray[idxRow] = evalBN.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][2] = evalBN.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalBN.pctCorrect());
				// ===========================================
				
				// =================== SMO ===================
				// SMO (PolyKernel) classifier
				SMO smo = new SMO();
				smo.buildClassifier(data);
				Evaluation evalSMO = new Evaluation(data);
				evalSMO.crossValidateModel(smo, data, 10, new Random(1));
				System.out.println("SMO done");
				
				// Save information
				SMOArray[idxRow] = evalSMO.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][4] = evalSMO.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalSMO.pctCorrect());
				// ===========================================
				
				// =================== J48 ===================
				// J48 classifier
				J48 j48 = new J48();
				j48.buildClassifier(data);
				Evaluation evalJ48 = new Evaluation(data);
				evalJ48.crossValidateModel(j48, data, 10, new Random(1));
				System.out.println("J48 done");
				
				// Save information
				J48Array[idxRow] = evalJ48.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][5] = evalJ48.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalJ48.pctCorrect());
				// ===========================================
				
				// ================= REPTREE =================
				// REPTree classifier
				REPTree reptree = new REPTree();
				reptree.buildClassifier(data);
				Evaluation evalREPTree = new Evaluation(data);
				evalREPTree.crossValidateModel(reptree, data, 10, new Random(1));
				System.out.println("REPTree done");
				
				// Save information
				REPTreeArray[idxRow] = evalREPTree.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][6] = evalREPTree.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalREPTree.pctCorrect());
				// ===========================================
				
				// ============== RANDOMFOREST ===============
				// RandomForest classifier
				RandomForest randForest = new RandomForest();
				randForest.buildClassifier(data);
				Evaluation evalRandForest = new Evaluation(data);
				evalRandForest.crossValidateModel(randForest, data, 10, new Random(1));
				System.out.println("RandomForest done");
				
				// Save information
				RandForestArray[idxRow] = evalRandForest.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][7] = evalRandForest.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalRandForest.pctCorrect());
				// ===========================================
				
				// =================== KNN ===================
				// kNN classifier
				IBk kNN = new IBk();
				kNN.buildClassifier(data);
				Evaluation evalkNN = new Evaluation(data);
				evalkNN.crossValidateModel(kNN, data, 10, new Random(1));
				System.out.println("kNN done");
				
				// Save information~
				KNNArray[idxRow] = evalkNN.pctCorrect();
				
				// Auxiliary variables
				classResultArray[idxRow][8] = evalkNN.pctCorrect();
				outputClass = outputClass + "\t" + String.format(Locale.ROOT, "%.3f", evalkNN.pctCorrect());
				
				// new line in String
				outputClass = outputClass + "\n";
				
				idxRow++;
				
				if(MarkovLag){
					// All the features of the first time point are removed until the last time point is the only one left
					for(int idx = 0; idx < nrDimensions; idx++){
						data.deleteAttributeAt(0);
					}
				} else {
					// All the features of the last time point are removed until the first time point is the only one left
					for(int idx = 0; idx < nrDimensions; idx++){
						data.deleteAttributeAt(data.numAttributes()-2);
					}
				}
		    }
		}
	    // ====================================================================
	    
	    // Auxiliary computation
	    // Save data in text files
	    String strAuxDIFF = "";
	    String strAuxMDL = "";
	    String strAuxAIC = "";
	    String strAuxLL = "";
		for(int i = 0; i < outputDIFF[0].length; i++){
			for(int j = 0; j < outputDIFF.length; j++){
				strAuxDIFF = strAuxDIFF + outputDIFF[j][i] + " ";
				strAuxMDL = strAuxMDL + outputMDL[j][i] + " ";
				strAuxAIC = strAuxAIC + outputAIC[j][i] + " ";
				strAuxLL = strAuxLL + outputLL[j][i] + " ";
			}
			strAuxDIFF = strAuxDIFF + "\n";
			strAuxMDL = strAuxMDL + "\n";
			strAuxAIC = strAuxAIC + "\n";
			strAuxLL = strAuxLL + "\n";
		}
		database.saveDataTextFile("dataEntropyDiff", strAuxDIFF);
		database.saveDataTextFile("dataMDL", strAuxMDL);
		database.saveDataTextFile("dataAIC", strAuxAIC);
		database.saveDataTextFile("dataLL", strAuxLL);
		database.saveDataTextFile("classResults", outputClass);
		database.saveDataTextFile("datasetFilename", file.getName().replace(".csv", "").replaceAll("_", "-"));
		
		// =======================================================
		
	}

}