package knn;

import java.io.IOException;
import java.util.SortedMap;

public interface KNN {
	
	void setDataTrain(String path, int numInstances) throws IOException, InterruptedException;
	void setDataTest(String path, int numInstances) throws IOException, InterruptedException;
	void getKNN() throws InterruptedException;
	int getPrediction(double[] element);
	double getDistance(double[] vector1, double[] vector2);
	void updateNeighbors(SortedMap<Double, Double> kNeighbors, double[] neighbor, double[] element);

}
