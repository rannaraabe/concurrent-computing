package knn;

import java.text.DecimalFormat;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class Spark {
	 public static final String path = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv";
	 public static final String pathNoHeader = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes2.csv";
	 public Integer sizeTrain;
	 public static final Integer lines_300MB = 40000;
	 public Integer k = 5;
	 private JavaRDD<String> dataTest;
	 private Double[][] dataTrain;
     static DecimalFormat df = new DecimalFormat("#.###");
     
	 /**
	  * Constructor
	  */
	 public Spark(String path, int k, int sizeTest, int sizeTrain, SparkConf conf){
	     	this.k = k;
	     	System.out.println("Reading files...");
	        this.dataTest = setDataTest(conf, path);
	        this.dataTrain = setDataTrain(this.dataTest, sizeTrain);
	        this.sizeTrain = sizeTrain;
     }
	 
	 /**
	  * Parse String input to double
	  */
	 public Double[] parserToDouble(String strInput) {
		  String[] value = strInput.split(",");
		  
		  Double[] line = {Double.parseDouble(value[0]), Double.parseDouble(value[1]), Double.parseDouble(value[2]), Double.parseDouble(value[3]), Double.parseDouble(value[4]),
				  Double.parseDouble(value[5]), Double.parseDouble(value[6]), Double.parseDouble(value[7]), Double.parseDouble(value[8])};
		  
		  return line;
	 }
	 
	 /**
	  * Calculate Euclidean distance between two points
	  */
	 public Double[] getDistance(Double[] p, Double[] d) {
		  Double[] r = {Math.sqrt(Math.pow(d[0] - p[0], 2) + Math.pow(d[1] - p[1], 2) + Math.pow(d[2] - p[2], 2) + Math.pow(d[3] - p[3], 2) +
                        Math.pow(d[4] - p[4], 2) + Math.pow(d[5] - p[5], 2) + Math.pow(d[6] - p[6], 2) + Math.pow(d[7] - p[7], 2)), d[8]};
		  return r;
	 }
	
	 /**
	  * Check if neighbor is should be between k-nearest neighbors
	  */
	 public List<Double[]> updateNeighbors(Double[] train) {
		 return this.dataTest.map(line ->  new Double[] {Double.parseDouble(line.split(",")[0]), Double.parseDouble(line.split(",")[1]),
	        Double.parseDouble(line.split(",")[2]), Double.parseDouble(line.split(",")[3]), Double.parseDouble(line.split(",")[4]),
	        Double.parseDouble(line.split(",")[5]), Double.parseDouble(line.split(",")[6]), Double.parseDouble(line.split(",")[7]),
	        Double.parseDouble(line.split(",")[8])}).map(x -> new Double[] {
	        		Math.sqrt( Math.pow(x[0] - train[0], 2) +
                    Math.pow(x[1] - train[1], 2) + Math.pow(x[2] - train[2], 2) +
                    Math.pow(x[3] - train[3], 2) + Math.pow(x[4] - train[4], 2) +
                    Math.pow(x[5] - train[5], 2) + Math.pow(x[6] - train[6], 2) +
                    Math.pow(x[7] - train[7], 2)), x[8]}) .sortBy(x -> x[0], true, 8).take(k);
	 }
	
	 /**
	  * Calculate prediction 
	  */
	 public Boolean getPrediction(List<Double[]> kNeighbours, Double[] train) {
		  int accurate = 0;
		  for (Double[] n : kNeighbours) {
			  if (n[1] == 1)
				  accurate += 1;
		  }
		
		  int result = accurate > (this.k - accurate) ? 1 : 0;
		  if (result == train[8])
			  return true;
		
		  return false;
	 }
	 
	 /**
	  * Check if element prediction is correct (returns 1)
	  */	 
	 public int getOutcome(Double[][] train) {
		 int hits = 0;
		 
		 for (int i = 0; i < this.sizeTrain; i++){
			 List<Double[]> kNeighbours = updateNeighbors(train[i]);
			 if (getPrediction(kNeighbours, train[i]))
				 hits += 1;
		 }
		 
		 return hits;
	 }
	 
	 /**
	  * Print accuracy and hits
	  */
	 public void printResults() {
		 System.out.println("Accuracy: " + df.format(this.getOutcome(this.dataTrain)/(double)this.dataTrain.length*100) + "%");
		 System.out.println("Hits: " +  this.getOutcome(this.dataTrain) + " | DataTest: " + this.dataTrain.length + " | k: " + this.k);
	 }
	 
	 /**
	  * Read test dataset
	  */
	 public JavaRDD<String> setDataTest(SparkConf conf, String path){
	     JavaSparkContext sc = new JavaSparkContext(conf);
	     JavaRDD<String> data = sc.textFile(path);
	     
	     return data;
	 }
	 
	 /**
	  * Read train dataset
	  */
	 public Double[][] setDataTrain(JavaRDD<String> test, int sizeTrain){
	     List<String> dataTrainLine = test.take(sizeTrain); 
	    
	     Double[][] data = new Double [sizeTrain][9];
	     int cont = 0;
	    
	     for (String r : dataTrainLine) {
	    	 data[cont] = new Double[] {Double.parseDouble(r.split(",")[0]), Double.parseDouble(r.split(",")[1]),
		    		  Double.parseDouble(r.split(",")[2]), Double.parseDouble(r.split(",")[3]), Double.parseDouble(r.split(",")[4]),
		    		  Double.parseDouble(r.split(",")[5]), Double.parseDouble(r.split(",")[6]), Double.parseDouble(r.split(",")[7]), Double.parseDouble(r.split(",")[8])};
		      cont += 1;
	     }
	     
	     return data;
	 }
}