package serial;

import java.util.Map;
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
		
		for(int j=0; j<this.dataTrain.length; j++) {
			double[] neighbor = this.dataTrain[j];
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
