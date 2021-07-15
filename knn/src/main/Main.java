package main;

import java.io.IOException;

import concurrent.MutexKNN;
import knn.KNN;
import serial.SerialKNN;

/**
 * Running: java -Xms2G -Xmx6G SerialKNN.java
 * @author rannaraabe
 */
public class Main {

    private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 			//[40000000][9]
    
	public static void main(String[] args) throws NumberFormatException, IOException {
	 	System.out.println(">>> k-NN implementation <<<");
		long startTime = System.nanoTime();
		
		// KNN knn = new SerialKNN(5);
 		KNN knn = new MutexKNN(5, 10000);
   	 	
		System.out.println("Reading files...");
   	 	knn.setDataTest(DATA_FILE, 40000);
   	 	knn.setDataTrain(DATA_FILE, 8000);
   	 	
   	 	knn.getKNN();
 		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Running time: " + (totalTime/1e+9)/60 + " minutes (" + totalTime/1e+9 + " seconds).");
	}
	
}