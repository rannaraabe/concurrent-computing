package main;
//package knn;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//
//import concurrent.AtomicKNN;
//import concurrent.MutexKNN;
//import knn.InterfaceKNN;
//import serial.SerialKNN;
//
///**
// * Running: java -Xms2G -Xmx6G Main.java
// * @author rannaraabe
// */
//public class Main {
//	
//	enum KNNType {
//		FORK_JOIN,
//		CALLABLE_FUTURE,
//		PARALLEL_STREAMS,
//	}
//	
//    private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 			//[40000000][9]
//    private static final int NUM_THREADS_EXECUTE = 2;
//    private static final int NUM_INSTANCES_EXECUTE = 4000;	// 400000
//    static int k = 5;
//    
//    // train 200
//    // test 
//    
//    static DecimalFormat df = new DecimalFormat("#.###");
//    
//	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
////		runForkJoin(k);
////		runCallableFuture(k);
//		runParallelStreams(k);
//	}
//	
//	public static void runForkJoin(int k) throws IOException, InterruptedException {
//		System.out.println(">>> Fork and Join k-NN <<<");
//		runKNN(k, KNNType.FORK_JOIN);
//	}
//	
//	public static void runCallableFuture(int k) throws IOException, InterruptedException {
//		System.out.println(">>> Callable Future k-NN <<<");
//		runKNN(k, KNNType.CALLABLE_FUTURE);
//	}
//	
//	public static void runParallelStreams(int k) throws IOException, InterruptedException {
//		System.out.println(">>> Parallel Streams k-NN <<<");
//		runKNN(k, KNNType.PARALLEL_STREAMS);
//	}
//	
//	public static void runKNN(int k, KNNType knnType) throws IOException, InterruptedException {
//		long startTime = System.nanoTime();
//		InterfaceKNN knn = null;
//
//		switch (knnType) {
//		case FORK_JOIN:
//			knn = new ForkAndJoin(k, NUM_THREADS_EXECUTE);
//			break;
//		case CALLABLE_FUTURE:
//			knn = new CallableAndFuture(k, NUM_THREADS_EXECUTE);
//			break;
//		case PARALLEL_STREAMS:
//			knn = new Spark(k, NUM_THREADS_EXECUTE);
//			break;
//		default:
//			throw new IOException("Invalid KNN type!");
//		}
//		
//		System.out.println("Reading files...");
//		knn.setDataTest(DATA_FILE, NUM_INSTANCES_EXECUTE);
//		knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE/10);
//		
//		knn.getKNN();
//		calculateTime(startTime);
//	}
//	
//	public static void calculateTime(long startTime) {
//		long endTime   = System.nanoTime();
//		long totalTime = endTime - startTime;
//		System.out.println("Running time: " + df.format((totalTime/1e+9)/60) + " minutes (" + df.format(totalTime/1e+9) + " seconds). \n");
//	}
//}