package serial;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.CSVReader;
import knn.KNN;
import utils.DistanceCalculator;

public class SerialKNN implements KNN {
    
    private int k;
	private double[][] dataTrain, dataTest;
	
	/**
	 * Parameterized constructor
	 */
	public SerialKNN(int k) {
		this.k = k;
	}
	
	/**
	 * Implementation k-NN algorithm
	 */
	@Override
	public void getKNN() {			
		System.out.println("Executing k-NN with k=" + this.k + "...");
		int hits = 0;
		
		for(double[] element : this.dataTest) {
			hits += getPrediction(element);
		}
		
		System.out.println("Accuracy: " + hits/(double)this.dataTest.length*100 + "%");
		System.out.println("Hits: " + hits + " | DataTest: "+ this.dataTest.length);
	}
	
	/**
	 * Calculate prediction 
	 */
	int getPrediction(double[] element) {
		SortedMap<Double, Double> kNeighbors = new TreeMap<Double, Double>();
		
		for(int j=0; j<this.dataTrain.length; j++) {
			double[] neighbor = this.dataTrain[j];
			double distance = 0.0;
			distance = DistanceCalculator.getDistance(element, neighbor);
			
			if(kNeighbors.size() < this.k) {
				kNeighbors.put(distance, neighbor[neighbor.length-1]);
			} else {
				if(distance < kNeighbors.lastKey()) {
					kNeighbors.remove(kNeighbors.lastKey(), kNeighbors.get(kNeighbors.lastKey()));
					kNeighbors.put(distance, neighbor[neighbor.length-1]);
				}
			}
		}

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

	@Override
	public void setDataTrain(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTrain = CSVReader.read(path, numInstances);
	}

	@Override
	public void setDataTest(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTest = CSVReader.read(path, numInstances);
	}
}
