package knn;

import java.io.IOException;
import java.text.DecimalFormat;

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
	
	@Override
	public void setDataTrain(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTrain = CSVReader.read(path, numInstances);
	}

	@Override
	public void setDataTest(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTest = CSVReader.read(path, numInstances);
	}
}
