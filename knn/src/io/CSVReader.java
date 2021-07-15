package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

	/**
	 * Read CSV file
	 * @throws IOException 
	 */
	public static double[][] read(String path, int instances) throws IOException {
		double[][] dataset = new double[instances][9];
   	 	FileReader fileReaderTest = new FileReader(path);

   	 	try (BufferedReader bufferedReader = new BufferedReader(fileReaderTest)) {
   	 		int lcount = 0;
            int ccount = 0;
   		 	String line;
   		 	while((line = bufferedReader.readLine()) != null) {
   		 		if(lcount >= instances)
	       			 break;
   		 		
	       		 ccount = 0;
	       		 String[] arr = line.split(",");
	       		 if(lcount != 0) {
	        		 for(String a : arr) {
	        			 dataset[lcount][ccount] = Double.parseDouble(a);
	                 	ccount++;
	                 }
	       		 }
            lcount++;
   		 	}
   	 	}
   	 	
   	 	fileReaderTest.close();
   	 	return dataset;
	}
}
