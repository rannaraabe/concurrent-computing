package concurrent;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import knn.AbstractKNN;

public class AtomicKNN extends AbstractKNN {
	 
	protected AtomicInteger hits;
	protected int numThreads, numInstances;
	
	/**
	 * Parameterized constructor
	 */
	public AtomicKNN(int k, int numThreads){
		this.k = k;
		this.numThreads = numThreads;
		this.hits = new AtomicInteger(0);
	} 
	
	/**
	 * Implementation k-NN algorithm
	 */
	@Override
	public void getKNN() throws InterruptedException {			
		System.out.println("Executing k-NN...");
		Thread[] threads = new Thread[this.numThreads];
		
		for(int i = 0; i < this.numThreads; i++){
		    threads[i] = new PredictionThread(i*this.dataTest.length/this.numThreads, (i+1)*(this.dataTest.length/this.numThreads));
		    threads[i].start();
		}			
	
		for(Thread t: threads) {
			t.join();			
		}
		
		this.printResults(this.hits.intValue());
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
	
	/**
	 * Thread class
	 * @author rannaraabe
	 */
	class PredictionThread extends Thread {
		
		private int l;
		private int r;
		
		public PredictionThread(int l, int r) {
			this.l = l;
			this.r = r;
		}
		
		@Override
		public void run() {
			for(int i=this.l; i<this.r; i++) {
				int result = getPrediction(AtomicKNN.this.dataTest[i]);
				
				if(result == 1) {						
					AtomicKNN.this.hits.incrementAndGet();
				}
			}
			
//				System.out.println("Finished thread: [" + this.l +", " + this.r +")");
		}
		
	}
}
