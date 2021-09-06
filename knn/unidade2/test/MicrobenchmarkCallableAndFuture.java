/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

import io.*;
import knn.CallableAndFuture;
import knn.KNN;

public class MicrobenchmarkCallableAndFuture {

	private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 
    private static final int NUM_INSTANCES_EXECUTE = 40000000;
    private static final int NUM_THREADS_EXECUTE = 4;
    static int k = 2000, hits;
    static double[][] dataTrain, dataTest;
    static ExecutorService executorService;
    static List<Callable<String>> rangePredictions;
    
    @State(Scope.Thread)
    public static class BenchmarkState {
        public KNN knn;
        
        @Setup
        public void setupBenchmark() throws FileNotFoundException, IOException, InterruptedException {		
        	executorService  = Executors.newFixedThreadPool(NUM_THREADS_EXECUTE);
        	rangePredictions = new ArrayList<>(); // List of tasks;

        	this.knn = new CallableAndFuture(k, NUM_THREADS_EXECUTE);
            
        	dataTrain = CSVReader.read(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	dataTest = CSVReader.read(DATA_FILE, 200);
        	    		
        	this.knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	this.knn.setDataTest(DATA_FILE, 200);
        }
    }

//    @Benchmark
//    @Warmup(iterations = 3)
//    @Measurement(iterations = 3)
//    @BenchmarkMode(Mode.Throughput)
//    @Fork(value=1)
//    public int testPredict(BenchmarkState state) throws InterruptedException, IOException {    	
//    	for(int k = 0; k < NUM_THREADS_EXECUTE; k++) {	
//			int l = k*(dataTest.length/NUM_THREADS_EXECUTE);
//			int r = (k+1)*(dataTest.length/NUM_THREADS_EXECUTE);
//			
//	    	for(int i=l; i<r; i++) {
//	    		double[] element = dataTest[i];
//				hits = state.knn.getPrediction(element);
//			}
//		}	
//
//    	return hits;
//    }
    
    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testDistance(BenchmarkState state) throws InterruptedException, IOException {
    	for(int i = 0; i < MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE; i++) {		
			// Left, Right: Indexes of the test dataset that the current thread is responsible (size: NUM_INSTANCES_TEST/NUM_THREADS per thread)
			int left = i*(200/MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE);
			int right = (i+1)*(200/MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE);
			
			MicrobenchmarkCallableAndFuture.rangePredictions.add(new Callable<String> () {
				@Override
				public String call() throws Exception {
					for(int testLine = left; testLine < right; testLine++) {
						// Line i of testDataset
			    		double[] currTest = MicrobenchmarkCallableAndFuture.dataTest[testLine];
			    		for(int j = 0; j < MicrobenchmarkCallableAndFuture.dataTrain.length; j++) {
			    			double[] currNeighbour = MicrobenchmarkCallableAndFuture.dataTrain[j];
			    			state.knn.getDistance(currTest, currNeighbour);
			    		}
					}
					return "Predictions Completed";
				}
			});
		}

		return;
    }
    
    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testNeighbours(BenchmarkState state) throws InterruptedException, IOException {
    	for(int i = 0; i < MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE; i++) {	
			// Left, Right: Indexes of the test dataset that the current thread is responsible (size: NUM_INSTANCES_TEST/NUM_THREADS per thread)
			int left = i*(200/MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE);
			int right = (i+1)*(200/MicrobenchmarkCallableAndFuture.NUM_THREADS_EXECUTE);
			
			MicrobenchmarkCallableAndFuture.rangePredictions.add(new Callable<String> () {
				@Override
				public String call() throws Exception {
					for(int testLine = left; testLine < right; testLine++) {
						// Line i of testDataset
			    		double[] currTest = MicrobenchmarkCallableAndFuture.dataTest[testLine];
			    		state.knn.updateNeighbors(currTest);
					}
					return "Predictions Completed";
				}
			});
    	
			return;
    	}
    }
}
