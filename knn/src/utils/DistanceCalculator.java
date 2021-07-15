package utils;

public class DistanceCalculator {
	
	/**
	 * Calculate Euclidean distance between two points
	 */
	public static double getDistance(double[] vector1, double[] vector2) {
		double sum = 0.0;
		
		for(int i=0; i<vector1.length-1; i++) {
			sum += Math.pow(vector1[i] - vector2[i], 2);
		}
		
		return Math.sqrt(sum);
	}
}
