package Sorters;

import java.util.Arrays;
import java.util.Random;

/**
 * Mergesort implementation based on "Energy and Time Complexity for Sorting Algorithms in Java" Figure 3.
 * Sorts an array of ints.
 */
public class MergeSort {

    public static boolean isSorted(final int[] data) {
        for (int i = 0; i < data.length-1; i++) {
            if (data[i] > data[i+1]) {
                return false;
            }
        }
        return true;
    }

    public static void merge(final int[] data, final int[] aux, int start, int mid, int end) {

        // copying the relevant part of the array into the auxiliary array.
        for (int k=start; k <= end; k++) {
            aux[k] = data[k];
        }

        int k = start; // index over the input subarray
        int i = start; // index over left aux subarray
        int j = mid+1; // index over right aux subarray


        // merge until we are at the end of one subarray
        while (i <= mid && j <= end) {
            if (aux[i] <= aux[j]) {
                data[k] = aux[i];
                i++;
                k++;
            }
            else {
                data[k] = aux[j];
                j++;
                k++;
            }
        }

        // add the remainder of the other subarray
        while (i <= mid) {
            data[k] = aux[i];
            i++;
            k++;
        }
        while (j <= end) {
            data[k] = aux[j];
            j++;
            k++;
        }
    }

    private static void mergeSortHelper(final int[] data, final int[] aux, int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            mergeSortHelper (data, aux, start, mid);
            mergeSortHelper (data, aux, mid + 1, end);
            merge(data, aux, start, mid, end);
        }
    }

    public static void mergeSort(final int[] data) {
        int[] aux = data.clone();
        mergeSortHelper(data, aux, 0, data.length-1);
    }

    public static void main(String[] args) {

        Random rand = new Random();
        int n = 40; // size
        int max = 200;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rand.nextInt(max);
        }

        System.out.println(Arrays.toString(data));
        System.out.println(isSorted(data));
        mergeSort(data);
        System.out.println(Arrays.toString(data));
        System.out.println(isSorted(data));
    }
}
