package main;

import java.io.IOException;
import java.text.DecimalFormat;

import concurrent.AtomicKNN;
import concurrent.MutexKNN;
import knn.InterfaceKNN;
import serial.SerialKNN;

/**
 * Running: java -Xms2G -Xmx6G Main.java
 * @author rannaraabe
 */
public class Main {
	
	enum KNNType {
		SERIAL,
		MUTEX,
		ATOMIC,
	}
	
    private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 			//[40000000][9]
    private static final int NUM_THREADS_EXECUTE = 8;
    private static final int NUM_INSTANCES_EXECUTE = 400000;
    static int k = 5;
    
    static DecimalFormat df = new DecimalFormat("#.###");
    
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		runAtomic(k);
		runMutex(k);
		runSerial(k);
	}
	
	public static void runSerial(int k) throws IOException, InterruptedException {
		System.out.println(">>> Serial k-NN <<<");
		runKNN(k, KNNType.SERIAL);
	}
	
	public static void runMutex(int k) throws IOException, InterruptedException {
		System.out.println(">>> Mutex k-NN <<<");
		runKNN(k, KNNType.MUTEX);
	}
	
	public static void runAtomic(int k) throws IOException, InterruptedException {
		System.out.println(">>> Atomic k-NN <<<");
		runKNN(k, KNNType.ATOMIC);
	}
	
	public static void runKNN(int k, KNNType knnType) throws IOException, InterruptedException {
		long startTime = System.nanoTime();
		InterfaceKNN knn = null;

		switch (knnType) {
		case SERIAL:
			knn = new SerialKNN(k);
			break;
		case MUTEX:
			knn = new MutexKNN(k, NUM_THREADS_EXECUTE);
			break;
		case ATOMIC:
			knn = new AtomicKNN(k, NUM_THREADS_EXECUTE);
			break;
		default:
			throw new IOException("Invalid KNN type!");
		}
		
		System.out.println("Reading files...");
		knn.setDataTest(DATA_FILE, NUM_INSTANCES_EXECUTE);
		knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE/10);
		
		knn.getKNN();
		calculateTime(startTime);
	}
	
	public static void calculateTime(long startTime) {
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Running time: " + df.format((totalTime/1e+9)/60) + " minutes (" + df.format(totalTime/1e+9) + " seconds). \n");
	}
}