package test;

import java.io.Serializable;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import knn.InterfaceKNN;
import serial.SerialKNN;

public class JmeterParallelStreams extends AbstractJavaSamplerClient implements Serializable {

	private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 
    private static final int NUM_INSTANCES_EXECUTE = 40000000;
	
	@Override 
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("k", "2000");
        return defaultParameters; 
    } 
	   
    @Override 
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String kStr = javaSamplerContext.getParameter("k");
        int k = Integer.parseInt(kStr);
        
        SampleResult result = new SampleResult();
        result.sampleStart();

        try {
        	InterfaceKNN knn = new SerialKNN(k);

        	System.out.println("Reading files...");
            knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	knn.setDataTest(DATA_FILE, 200);
            
        	knn.getKNN();
            
            result.sampleEnd(); 
            result.setSuccessful(true);
            result.setResponseMessage("Successfully performed action");
            result.setResponseCodeOK();
        } catch (Exception e) {
        	result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage("Exception: " + e);
            result.setResponseCode("500");
        }
              
        return result; 
    }

 
}