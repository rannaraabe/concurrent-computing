package knn;

import java.io.IOException;
import java.util.SortedMap;

public interface InterfaceKNN {
	
	void setDataTrain(String path, int numInstances) throws IOException, InterruptedException;
	void setDataTest(String path, int numInstances) throws IOException, InterruptedException;
	void getKNN() throws InterruptedException;
	void getOutcome(SortedMap<Double, Double> kNeighbours, double[] currTest);
	SortedMap<Double, Double> updateNeighbors(double[] currTest);

}
