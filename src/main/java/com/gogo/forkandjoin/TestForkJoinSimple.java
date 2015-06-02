package com.gogo.forkandjoin;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Created by 80107436 on 2015-05-07.
 */
public class TestForkJoinSimple {

    public static void main(String[] args) throws Exception {

        int NARRAY = 16; //For demo only
        long[] array = new long[NARRAY];
        Random rand = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextLong() % 100; //For demo only
        }
        log("Initial Array: " + Arrays.toString(array));

        ForkJoinTask sort = new SortTask(array);
        ForkJoinPool fjpool = new ForkJoinPool();
        fjpool.submit(sort);
        fjpool.shutdown();
        fjpool.awaitTermination(30, TimeUnit.SECONDS);
    }

    private static void log(String msg) {
        System.out.println(Thread.currentThread() + " : " + msg);;
    }

    private static class SortTask extends RecursiveAction {
        final long[] array;
        final int lo;
        final int hi;
        private int THRESHOLD = 0; //For demo only

        public SortTask(long[] array) {
            this.array = array;
            this.lo = 0;
            this.hi = array.length - 1;
        }

        public SortTask(long[] array, int lo, int hi) {
            this.array = array;
            this.lo = lo;
            this.hi = hi;
        }

        protected void compute() {
            if (hi - lo < THRESHOLD)
                sequentiallySort(array, lo, hi);
            else {
                int pivot = partition(array, lo, hi);
                log("pivot = " + pivot + ", low = " + lo + ", high = " + hi);
                log("array" + Arrays.toString(array));
                invokeAll(new SortTask(array, lo, pivot - 1), new SortTask(array, pivot + 1, hi));
            }
        }

        private int partition(long[] array, int lo, int hi) {
            long x = array[hi];
            int i = lo - 1;
            for (int j = lo; j < hi; j++) {
                if (array[j] <= x) {
                    i++;
                    swap(array, i, j);
                }
            }
            swap(array, i + 1, hi);
            return i + 1;
        }

        private void swap(long[] array, int i, int j) {
            if (i != j) {
                long temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        private void sequentiallySort(long[] array, int lo, int hi) {
            Arrays.sort(array, lo, hi + 1);
        }
    }

}
