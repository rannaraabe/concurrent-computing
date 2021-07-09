public class Main {

	public static void main(String[] args) {
		System.out.println(">>> k-NN implementation <<<");

		long startTime = System.nanoTime();
		
		KNN knn = new KNN(5);
		knn.getKNN();
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Running time: " + totalTime + "nanoseconds.");
	}

}
