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
	public void setDataTrain(String path, int instances) throws IOException {
		this.dataTrain = CSVReader.read(path, instances);
	}

	@Override
	public void setDataTest(String path, int instances) throws IOException {
		this.dataTest = CSVReader.read(path, instances);
	}

	@Override
	public void getKNN() {
		// TODO Auto-generated method stub
		
	}

}
