package knn;

import java.io.IOException;

public interface KNN {
	
	void setDataTrain(String path, int numInstances) throws IOException, InterruptedException;
	void setDataTest(String path, int numInstances) throws IOException, InterruptedException;
	void getKNN() throws InterruptedException;

}
