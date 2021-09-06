package main;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

import concurrent.Spark;

/**
 * Running: java -Xms2G -Xmx6G Main.java
 * @author rannaraabe
 */
public class Main3 {
	
	enum KNNType {
		SPARK,
	}
	
    private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes2.csv"; 			//[40000000][9]
    private static final int NUM_INSTANCES_EXECUTE = 40000000;	// 400000
    static int k = 2000;
    static DecimalFormat df = new DecimalFormat("#.###");
    
    // train 200
    // test 
    
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		Logger.getLogger("org").setLevel(Level.OFF);		
		SparkConf conf = new SparkConf().setAppName("Spark").setMaster("local[2]");
		
		System.out.println(">>> Spark k-NN <<<");
		long startTime = System.nanoTime();
		Spark knn =  new Spark(DATA_FILE, k, NUM_INSTANCES_EXECUTE, NUM_INSTANCES_EXECUTE/2, conf);		
		knn.printResults();
		calculateTime(startTime);
		
//		runSpark(k, conf);
	}
	
	public static void runSpark(int k, SparkConf conf) throws IOException, InterruptedException {
		System.out.println(">>> Spark k-NN <<<");
		runKNN(k, KNNType.SPARK, conf);
	}
	
	public static void runKNN(int k, KNNType knnType, SparkConf conf) throws IOException, InterruptedException {
		long startTime = System.nanoTime();
		Spark knn = null;

		switch (knnType) {
		case SPARK:
			knn = new Spark(DATA_FILE, k, NUM_INSTANCES_EXECUTE, 200, conf);
			break;
		default:
			throw new IOException("Invalid KNN type!");
		}
		
		knn.printResults();
		calculateTime(startTime);
	}
	
	public static void calculateTime(long startTime) {
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Running time: " + df.format((totalTime/1e+9)/60) + " minutes (" + df.format(totalTime/1e+9) + " seconds). \n");
	}	
	
}