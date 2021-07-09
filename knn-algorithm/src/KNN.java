import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class KNN {
	
    private static final String TRAIN_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes-train.csv";
    private static final String TEST_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv";
    private int k;
	private ArrayList<ArrayList<Double>> dataTrain, dataTest;
	
	/**
	 * Parameterized constructor
	 */
	public KNN(int k) {
		this.k = k;
		
		try {
			System.out.println("Reading files...");
			this.dataTrain = readCSV(TRAIN_FILE);
			this.dataTest = readCSV(TEST_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to read CSV file
	 */
	public ArrayList<ArrayList<Double>> readCSV(String path) throws IOException {		
		BufferedReader csvReader = new BufferedReader(new FileReader(path));
		String row = "";
		String[] data = null;
		int count = 0;
		
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		
		while((row = csvReader.readLine()) != null) {
			if(count == 0) {
				count++;
				continue;
			} else {				
				ArrayList<Double> rowValues = new ArrayList<Double>();
				data = row.split(",");
				for(int i=0; i<data.length; i++) {
					rowValues.add(Double.valueOf(data[i]));
				}
				result.add(rowValues);
			}
			count++;
		}
		
		csvReader.close();
		return result;
	}
	
	/**
	 * Calculate Euclidean distance between two points
	 */
	public double getDistance(ArrayList<Double> vector1, ArrayList<Double> vector2) {
		double sum = 0.0;
		
		for(int i=0; i<vector1.size()-1; i++) {
			sum += Math.pow(vector1.get(i) - vector2.get(i), 2);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Implementation k-NN algorithm
	 */
	public void getKNN() {
		System.out.println("Executing k-NN...");
		int hits = 0;
		
		for(ArrayList<Double> element : this.dataTest) {
			SortedMap<Double, Double> kNeighbors = new TreeMap<Double, Double>();
			double distance = 0.0;
			
			for(ArrayList<Double> neighbor : this.dataTrain) {
				distance = getDistance(element, neighbor);
				
				if(kNeighbors.size() < this.k) {
					kNeighbors.put(distance, neighbor.get(neighbor.size()-1));
				} else {
					if(distance < kNeighbors.lastKey()) {
						kNeighbors.remove(kNeighbors.lastKey(), kNeighbors.get(kNeighbors.lastKey()));
						kNeighbors.put(distance, neighbor.get(neighbor.size()-1));
					}
				}
			}
			
			// Gambiarra para acessar as chaves do SortedMap
			double outcome = 0.0;
			for(Map.Entry<Double, Double> entry : kNeighbors.entrySet()) {
				outcome += entry.getValue();
			}
			
			if(outcome < this.k-outcome) {
				if(element.get(element.size()-1) == 0) {
					hits++;
				} 
			} else {
				if(element.get(element.size()-1) == 1) {
					hits++;
				}
			}
		}
		
		double accuracy = hits/this.dataTest.size();
		System.out.println("k: " + this.k + ", Accuracy: " + accuracy);
		
	}
	
}
