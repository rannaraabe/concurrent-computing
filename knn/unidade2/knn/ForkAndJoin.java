package knn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.CSVReader;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

////===== TODO
public class ForkAndJoin implements InterfaceKNN {
	private int k;
	private double[][] dataTest, dataTrain;
	private int NUM_THREADS;
	private Thread[] threads;
	private AtomicInteger accurates;
	
	public ForkAndJoin(int k, String filename, int NUM_THREADS) throws IOException, InterruptedException {
		this.k = k;
		this.NUM_THREADS = NUM_THREADS;
		threads = new Thread[NUM_THREADS];
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
	 * PredictionTask
	 */
	public class PredictionTask extends RecursiveAction {
		int currNumInstancesTest;
		int numInstancesPartition;
		int left;
		int right;

	    public PredictionTask(int currNumInstancesTest, int numInstancesPartition, int left, int right) {
	        this.currNumInstancesTest = currNumInstancesTest;
	        this.numInstancesPartition = numInstancesPartition;
	        this.left = left;
	        this.right = right;
	    }

	    @Override
	    protected void compute() {
	        if (this.currNumInstancesTest <= this.numInstancesPartition) {
	           for(int testLine = this.left; testLine < this.right; testLine++) {
					ForkAndJoin.this.getPrediction(testLine);
				}
	        }
	        else {
	        	PredictionTask firstSubtask = new PredictionTask(numInstancesPartition/2, numInstancesPartition, this.left, this.right/2);
	        	PredictionTask secondSubtask = new PredictionTask(numInstancesPartition/2, numInstancesPartition, this.right/2, this.right);
	        	firstSubtask.fork();
	        	secondSubtask.compute();
	        	firstSubtask.join();
	        	
//	        	List<PredictionTask> subtasks = new ArrayList<>(); // List of tasks
//	        	for(int i = 0; i < NUM_THREADS; i++) {
//	        		int left = i*(dataTest.length/NUM_THREADS);
//        			int right = (i+1)*(dataTest.length/NUM_THREADS);
//        			
//        			subtasks.add(new PredictionTask(right-left, numInstancesPartition, this.left, this.right));
//        			subtasks.get(i).compute();
//	        	}
	        }
	    }
	}

	/**
	 * Implementation k-NN algorithm
	 */
	@Override
	public void getKNN() throws InterruptedException {		
		ForkJoinPool pool = new ForkJoinPool();
		PredictionTask task = new PredictionTask(this.dataTest.length, this.dataTest.length/2, 0, this.dataTest.length);
//		PredictionTask task = new PredictionTask(this.dataTest.length, this.dataTest.length/this.NUM_THREADS, 0, this.dataTest.length);
		pool.invoke(task);
		while(!pool.isQuiescent()){}
		
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