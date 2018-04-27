package Main;

import java.io.File;
import java.io.FilenameFilter;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class DiscretizeData {

	public static void main(String[] args) throws Exception {
		
		// path to folder
		File dir = new File("C:/Users/Utilizador/Documents/Java/EarlyPrediction");
		// all CSV files from directory are considered
		File[] directoryListing = dir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".csv");
	        }
		});
		
		// number of files in the directory
		int nrFiles = directoryListing.length;
		System.out.println("Analysing " + String.valueOf(nrFiles) + " CSV files from the directory.");
			
		int cntFile = 1;
		
		// check if there are files in the directory
		if (directoryListing != null) {
			
			// Loop through all files
			for (File child : directoryListing) {
				
				String inputFile = child.getName();
				
				// Load CVS file
				CSVLoader loader = new CSVLoader();
				loader.setSource(child);
				Instances result = loader.getDataSet(); // Get instances object
				result.setClassIndex(result.numAttributes() - 1); // class label is last attribute
			    
			    // ==============================================================================
			    // 								DISCRETIZATION SUPERVISED
			    // ==============================================================================
			    // DISCRETIZATION with Fayyad & Irani's MDL method (the default)
		 		Discretize discreteFilter = new Discretize(); // setup filter
		 		discreteFilter.setInputFormat(result);
		 		// Apply filter
		 	    Instances discreteTrain = Filter.useFilter(result, discreteFilter); 
		 	    
		 	    // Save dataset discretized into CSV file
		 	    CSVSaver saverNew = new CSVSaver();
			 	saverNew.setInstances(discreteTrain);
			 	saverNew.setFile(new File("files/" + inputFile.replace(".csv","_DISCRETE.csv")));
			 	saverNew.writeBatch();
			 	
				System.out.println("============= " + String.valueOf(cntFile) + " from " + String.valueOf(directoryListing.length) + " =============");
			 	
				cntFile++;
			}
		}

	}

}
