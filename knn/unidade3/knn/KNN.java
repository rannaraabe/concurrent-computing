package knn;

import java.io.IOException;
import java.util.SortedMap;

public interface KNN {
	void getKNN() throws InterruptedException;
	Double[] getDistance(Double[] vector1, Double[] vector2);
//	SortedMap<Double, Double> updateNeighbors(double[] currTest);

}
