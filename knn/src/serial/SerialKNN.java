package serial;

import java.util.SortedMap;
import java.util.TreeMap;

import knn.AbstractKNN;

public class SerialKNN extends AbstractKNN {
	
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
		System.out.println("Executing k-NN...");
		int hits = 0;
		
		for(double[] element : this.dataTest) {
			hits += getPrediction(element);
		}
		
		this.printResults(hits);
	}
	
	/**
	 * Calculate prediction 
	 */
	int getPrediction(double[] element) {
		SortedMap<Double, Double> kNeighbors = new TreeMap<Double, Double>();
		
		for(double[] neighbor : this.dataTrain) {
			updateNeighbors(kNeighbors, neighbor, element);			
		}

		return getOutcome(kNeighbors, element);
	}
}
