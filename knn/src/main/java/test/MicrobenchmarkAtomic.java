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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.openjdk.jmh.annotations.*;

import io.*;
import knn.*;
import concurrent.*;

public class MicrobenchmarkAtomic {

	private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 
    private static final int NUM_INSTANCES_EXECUTE = 400000;
    private static final int NUM_THREADS_EXECUTE = 8;
    static int k = 2000;
    static AtomicInteger hits;
    static double[][] dataTrain, dataTest;
	static Thread threads[];
    
    @State(Scope.Thread)
    public static class BenchmarkState {
        public KNN knn;
        
        @Setup
        public void setupBenchmark() throws FileNotFoundException, IOException, InterruptedException {
            this.knn = new SerialAtomic(k, NUM_THREADS_EXECUTE);
            threads = new Thread[NUM_THREADS_EXECUTE]; 
            
        	dataTrain = CSVReader.read(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	dataTest = CSVReader.read(DATA_FILE, NUM_INSTANCES_EXECUTE/10);
        	
        	this.knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	this.knn.setDataTest(DATA_FILE, NUM_INSTANCES_EXECUTE/10);
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    public int testPredict(BenchmarkState state) throws InterruptedException, IOException {    	
    	for(int k = 0; k < NUM_THREADS_EXECUTE; k++) {	
			// Left, Right: Indexes of the test dataset that the current thread is responsible (size: NUM_INSTANCES_TEST/NUM_THREADS per thread)
			int l = k*(dataTest.length/NUM_THREADS_EXECUTE);
			int r = (k+1)*(dataTest.length/NUM_THREADS_EXECUTE);
			
			MicrobenchmarkMutex.threads[k] = new Thread(new Runnable() {
				public void run() {
			    	for(int i=l; i<r; i++) {
			    		double[] element = dataTest[i];
						hits = state.knn.getPrediction(element);
					}
				}
			});
			MicrobenchmarkMutex.threads[k].start();
		}	

		for(Thread t : MicrobenchmarkMutex.threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	return hits.intValue();
    }
    
    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    public void testDistance(BenchmarkState state) throws InterruptedException, IOException {
    	for(int k=0; k<NUM_INSTANCES_EXECUTE; k++) {
    		int l = k*(dataTest.length/NUM_THREADS_EXECUTE);
			int r = (k+1)*(dataTest.length/NUM_THREADS_EXECUTE);
    		
			threads[k] = new Thread(new Runnable() {
				public void run() {
		    		for(int i=l; i<r; i++) {
		    			double[] vector1 = dataTest[i];
		    			for(int j=0; j<dataTrain.length-1; j++) {
		    				double[] vector2 = dataTrain[j];
		    				state.knn.getDistance(vector1, vector2);
		    			}
		    		}
				}
			});
			
			MicrobenchmarkMutex.threads[k].start();
    	}
			
		for(Thread t : MicrobenchmarkMutex.threads) {		
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return;
    }
    
    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    public void testNeighbors(BenchmarkState state) throws InterruptedException, IOException {
    	SortedMap<Double, Double> kNeighbors = new TreeMap<Double, Double>();
    	for(int i=0; i<dataTest.length-1; i++) {
			double[] element = dataTest[i];
			for(int j=0; j<dataTrain.length-1; j++) {
				double[] neighbor = dataTrain[j];
				state.knn.updateNeighbors(kNeighbors, neighbor, element);
			}
		}
    	
    	return;
    	
    }

}
