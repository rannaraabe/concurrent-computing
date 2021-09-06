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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import concurrent.AtomicKNN;
import io.CSVReader;

public class ParallelStreams implements InterfaceKNN {
	private int k;
	private double[][] dataTrain, dataTest;
	private int NUM_THREADS;
	double distance;
	private AtomicInteger hits;

	public ParallelStreams(int k, int NUM_THREADS) throws IOException {
		this.k = k;
		this.NUM_THREADS = NUM_THREADS;		
		this.hits = new AtomicInteger(0);
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
	public void getKNN() {			
		System.out.println("Executing k-NN...");
		
		IntStream.range(0, this.dataTest.length).parallel().forEach(i -> {	
			int result = getPrediction(this.dataTest[i]);
			if(result == 1) {						
				this.hits.incrementAndGet();
			}
		});
		
		double accuracy = (this.hits.doubleValue()/(double)this.dataTest.length)*100;
		System.out.println(">> Final Accuracy: " + accuracy + "% | " + this.hits.intValue() + "/" + this.dataTest.length);
	}
	
	/**
	 * Calculate prediction 
	 */
	public int getPrediction(double[] element) {		
		SortedMap<Double, Double> kNeighbors = updateNeighbors(element);
		
		return getOutcome(kNeighbors, element);
	}

	/**
	 * Check if neighbor is should be between k-nearest neighbors
	 */
	@Override
	public SortedMap<Double, Double> updateNeighbors(double[] currTest) {
		this.distance = 0.0;
		SortedMap<Double, Double> kNeighbours = new TreeMap<Double, Double>();
		
		for(int j = 0; j < this.dataTrain.length; j++) {
			double[] currNeighbour = this.dataTrain[j];
			
			this.distance = getEuclidianDistance(currTest, currNeighbour);
			
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
	public int getOutcome(SortedMap<Double, Double> kNeighbors, double[] element) {
		double outcome = 0.0;
		
		for(Map.Entry<Double, Double> entry : kNeighbors.entrySet()) {
			outcome += entry.getValue();
		}
		
		if(outcome < this.k-outcome) {
			if(element[element.length-1] == 0) return 1;
		} else {
			if(element[element.length-1] == 1) return 1;
		}
		
		return 0;
	}

}