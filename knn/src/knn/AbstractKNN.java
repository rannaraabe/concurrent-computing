package knn;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.SortedMap;

import io.CSVReader;

public abstract class AbstractKNN implements KNN {
	
	protected int k;
	protected double[][] dataTrain, dataTest;

    static DecimalFormat df = new DecimalFormat("#.###");
    
	/**
	 * Print accuracy and hits
	 */
	protected void printResults(int hits) {
		System.out.println("Accuracy: " + df.format(hits/(double)this.dataTest.length*100) + "%");
		System.out.println("Hits: " + hits + " | DataTest: " + this.dataTest.length + " | k: " + this.k);
	}
	
	/**
	 * Calculate Euclidean distance between two points
	 */
	protected static double getDistance(double[] vector1, double[] vector2) {
		double sum = 0.0;
		
		for(int i=0; i<vector1.length-1; i++) {
			sum += Math.pow(vector1[i] - vector2[i], 2);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Check if element prediction is correct (returns 1)
	 */
	protected int getOutcome(Map<Double, Double> kNeighbors, double[] element) {
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
	
	/**
	 * Check if neighbor is should be between k-nearest neighbors
	 */
	protected void updateNeighbors(SortedMap<Double, Double> kNeighbors, double[] neighbor, double[] element) {
		double distance = 0.0;
		distance = getDistance(element, neighbor);
		
		if(kNeighbors.size() < this.k) {
			kNeighbors.put(distance, neighbor[neighbor.length-1]);
		} else {
			if(distance < kNeighbors.lastKey()) {
				kNeighbors.remove(kNeighbors.lastKey(), kNeighbors.get(kNeighbors.lastKey()));
				kNeighbors.put(distance, neighbor[neighbor.length-1]);
			}
		}
	}
	
	@Override
	public void setDataTrain(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTrain = CSVReader.read(path, numInstances);
	}

	@Override
	public void setDataTest(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTest = CSVReader.read(path, numInstances);
	}
}
