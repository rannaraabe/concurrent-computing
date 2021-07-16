package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import concurrent.MutexKNN;

public class CSVReader {
    
    /**
     * Default constructor
     */
    public CSVReader() {
    	
	}
    
	/**
	 * Read CSV file
	 */
	public static double[][] read(String path, int numInstances) throws IOException, InterruptedException {
		double[][] dataset = new double[numInstances][9];
   	 	FileReader fileReader = new FileReader(path);
	   	int line = 0;
	    int column = 0;
     
   	 	try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
   	 		
   		 	String row;
   		 	while((row = bufferedReader.readLine()) != null) {
	   		 	if(line >= numInstances) {
					break;
				}
					
				column = 0;
				String[] data = row.split(",");
				
				if(line != 0) {
				   	for(String a : data) {
				   		dataset[line][column] = Double.parseDouble(a);
				   		column++;
				    }
				}
				line++;
   		 	}
   	 	}
   	 	
   	 	fileReader.close();
   	 	return dataset;
	}
}
