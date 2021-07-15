package concurrent;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import io.CSVReader;
import knn.KNN;
import utils.DistanceCalculator;

/**
 * Running: java -Xms2G -Xmx6G MutexKNN.java
 * @author rannaraabe
 */
public class MutexKNN implements KNN {

    private int k;
	private double[][] dataTrain, dataTest;
	private ReentrantLock mutex;
	protected int hits;
	protected int numThreads;
	
	/**
	 * Parameterized constructor
	 */
	public MutexKNN(int k, int numThreads){
		this.k = k;
		this.mutex = new ReentrantLock();
		this.numThreads = numThreads;
	} 
	
	/**
	 * Implementation k-NN algorithm
	 */
	@Override
	public void getKNN() {			
		System.out.println("Executing k-NN with k=" + this.k + "...");
		
		for(double[] element : this.dataTest) {	
			while(this.numThreads <= 0) {
				System.out.println("Waiting for threads to end...");
			}
			
			CalculatePredictionThread thread = new CalculatePredictionThread(element);
			thread.start();
		}
		
		System.out.println("Accuracy: " + this.hits/(double)this.dataTest.length + "%");
	}
	
	/**
	 * Calculate prediction 
	 */
	int calculatePrediction(double[] element) {
		SortedMap<Double, Double> kNeighbors = new TreeMap<Double, Double>();
		
		for(int j=0; j<this.dataTrain.length; j++) {
			this.mutex.lock();
			double[] neighbor = this.dataTrain[j];
			this.mutex.unlock();
			
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
			if(element[element.length-1] == 0) {
				return 1;
			} 
		} else {
			if(element[element.length-1] == 1) {
				return 1;
			}
		}
		
		return 0;
	}

	@Override
	public void setDataTrain(String path, int instances) throws IOException {
		this.dataTrain = CSVReader.read(path, instances);
	}

	@Override
	public void setDataTest(String path, int instances) throws IOException {
		this.dataTest = CSVReader.read(path, instances);
	}
	
	class CalculatePredictionThread extends Thread {
		
		private double[] element;
		
		public CalculatePredictionThread(double[] element) {
			this.element = element;
		}
		
		@Override
		public void run() {
			MutexKNN.this.numThreads--;
			MutexKNN.this.hits += calculatePrediction(element);
			MutexKNN.this.numThreads++;
		}
		
	}
}
