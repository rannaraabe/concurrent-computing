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

import org.openjdk.jmh.annotations.*;

import io.*;
import knn.*;
import serial.*;

public class MicrobenchmarkSerial {

	private static final String DATA_FILE = "/home/rannaraabe/Documents/concurrent-computing/data/diabetes.csv"; 
    private static final int NUM_INSTANCES_EXECUTE = 40000000;
    static int k = 2000, hits;
    static double[][] dataTrain, dataTest;
    
    @State(Scope.Thread)
    public static class BenchmarkState {
        public KNN knn;
        
        @Setup
        public void setupBenchmark() throws FileNotFoundException, IOException, InterruptedException {
            this.knn = new SerialKNN(k);
        	
        	dataTrain = CSVReader.read(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	dataTest = CSVReader.read(DATA_FILE, 200);
        	
        	this.knn.setDataTrain(DATA_FILE, NUM_INSTANCES_EXECUTE);
        	this.knn.setDataTest(DATA_FILE, 200);
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    public int testPredict(BenchmarkState state) throws InterruptedException, IOException {
    	
    	for(int i=0; i<dataTest.length-1; i++) {
			double[] element = dataTest[i];
			hits = state.knn.getPrediction(element);
		}
    	
    	return hits;
    }
    
    @Benchmark
    @Warmup(iterations = 3)
    @Measurement(iterations = 3)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value=1)
    public void testDistance(BenchmarkState state) throws InterruptedException, IOException {
		for(int i=0; i<dataTest.length-1; i++) {
			double[] vector1 = dataTest[i];
			for(int j=0; j<dataTrain.length-1; j++) {
				double[] vector2 = dataTrain[j];
				state.knn.getDistance(vector1, vector2);
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
