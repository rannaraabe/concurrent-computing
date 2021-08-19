package knn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.CSVReader;

public class CallableAndFuture implements InterfaceKNN {
	private int k;
	private double[][] dataTrain, dataTest;
	private int NUM_THREADS;
	private AtomicInteger accurates;
	private ExecutorService executorService;
	List<Callable<String>> rangePredictions;
	
	public CallableAndFuture(int k, String filename, int NUM_THREADS) throws IOException {
		this.k = k;
		this.NUM_THREADS = NUM_THREADS;		
		this.executorService = Executors.newFixedThreadPool(NUM_THREADS);
		this.rangePredictions = new ArrayList<>(); // List of tasks
		this.accurates = new AtomicInteger(0);
	}
	
	@Override
	public void setDataTrain(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTrain = CSVReader.read(path, numInstances);
	}

	@Override
	public void setDataTest(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTest = CSVReader.read(path, numInstances);		
	}
	
	/**
	 * Calculate Euclidean distance between two points
	 */
	public double getEuclidianDistance(double[] a1, double[] a2) {
		double distance = 0.0;
		
		for(int i = 0; i < a1.length-1; i++) {
			distance += Math.pow(a1[i] - a2[i], 2);
		}
		distance = Math.sqrt(distance);
		
		return distance;
	}

	/**
	 * Implementation k-NN algorithm
	 */
	@Override
	public void getKNN() throws InterruptedException {
		for(int i = 0; i < this.NUM_THREADS; i++) {
			int left = i*(this.dataTest.length/NUM_THREADS);
			int right = (i+1)*(this.dataTest.length/NUM_THREADS);
			
			this.rangePredictions.add(new Callable<String> () {
				@Override
				public String call() throws Exception {
					for(int testLine = left; testLine < right; testLine++) {
						CallableAndFuture.this.getPrediction(testLine);
					}
					return "Predictions Completed";
				}
			});
			
		}
		
		List<Future<String>> result = this.executorService.invokeAll(this.rangePredictions);
		this.executorService.shutdown();

		try {
			this.executorService.awaitTermination(60, TimeUnit.SECONDS);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		double accuracy = (accurates.doubleValue()/(double)this.dataTest.length)*100;
		System.out.println(">> Final Accuracy: " + accuracy + "% | " + accurates + "/" + this.dataTest.length);
	}
	
	/**
	 * Calculate prediction 
	 */
	public void getPrediction(int testLine) {
		double[] currTest = this.dataTest[testLine];
		SortedMap<Double, Double> kNeighbours = updateNeighbors(currTest);
		
		getOutcome(kNeighbours, currTest);
	}
	
	/**
	 * Check if neighbor is should be between k-nearest neighbors
	 */
	@Override
	public SortedMap<Double, Double> updateNeighbors(double[] currTest) {
		double distance = 0.0;
		SortedMap<Double, Double> kNeighbours = new TreeMap<Double, Double>();
		
		for(int j = 0; j < this.dataTrain.length; j++) {
			double[] currNeighbour = this.dataTrain[j];
			
			distance = getEuclidianDistance(currTest, currNeighbour);
			
			if(kNeighbours.size() < this.k) {
				kNeighbours.put(distance, currNeighbour[currNeighbour.length-1]);
			} else {
				if(distance < kNeighbours.lastKey()) {
					kNeighbours.remove(kNeighbours.lastKey(), kNeighbours.get(kNeighbours.lastKey()));
					kNeighbours.put(distance, currNeighbour[currNeighbour.length-1]);
				}
			}
		}
		
		return kNeighbours;
	}
	
	/**
	 * Check if element prediction is correct (returns 1)
	 */
	@Override
	public void getOutcome(SortedMap<Double, Double> kNeighbours, double[] currTest) {
		double resultSum  = 0.0;
		for(Map.Entry<Double, Double> entry : kNeighbours.entrySet()) {
			resultSum += entry.getValue();
		}
		
		if(resultSum < this.k-resultSum) {
			if(currTest[currTest.length-1] == 0) {
				this.accurates.incrementAndGet();
			}
		} 
		else {
			if(currTest[currTest.length-1] == 1) {
				this.accurates.incrementAndGet();
			}
		}
	}

}