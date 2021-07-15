package knn;

import java.io.IOException;

public interface KNN {
	
	void setDataTrain(String path, int instances) throws IOException;
	void setDataTest(String path, int instances) throws IOException;
	void getKNN();

}
