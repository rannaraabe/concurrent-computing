package concurrent;

import java.io.IOException;

import io.CSVReader;
import knn.KNN;

public class SynchronizedKNN implements KNN {
	 
    private int k;
	private double[][] dataTrain, dataTest;
	
	/**
	 * Parameterized constructor
	 * @throws IOException 
	 */
	public SynchronizedKNN(int k) {
		this.k = k;
	}
	
	@Override
	public void setDataTrain(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTrain = CSVReader.read(path, numInstances);
	}

	@Override
	public void setDataTest(String path, int numInstances) throws IOException, InterruptedException {
		this.dataTest = CSVReader.read(path, numInstances);
	}

	@Override
	public void getKNN() {
		// TODO Auto-generated method stub
		
	}

}
