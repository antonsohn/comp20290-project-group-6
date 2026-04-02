package Sorters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Mergesort implementation based on "Energy and Time Complexity for Sorting Algorithms in Java" Figure 3.
 */
public class MergeSort {

    public static <T extends Comparable<T>> boolean isSorted(final T[] input, final Comparator<T> comparator) {
        for (int i = 0; i < input.length-1; i++) {
            if (comparator.compare(input[i], input[i+1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<? super T>> void merge(final T[] data, final Comparator<T> comparator, final T[] aux, int start, int mid, int end) {

        // copying the relevant part of the array into the auxiliary array.
        for (int k=start; k <= end; k++) {
            aux[k] = data[k];
        }

        int k = start; // index over the input subarray
        int i = start; // index over left aux subarray
        int j = mid+1; // index over right aux subarray


        // merge until we are at the end of one subarray
        while (i <= mid && j <= end) {
            if (comparator.compare(aux[i], aux[j]) <= 0) {
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

    private static <T extends Comparable<? super T>> void mergeSortHelper(final T[] data, final Comparator<T> comparator, final T[] aux, int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            mergeSortHelper (data, comparator, aux, start, mid);
            mergeSortHelper (data, comparator, aux, mid + 1, end);
            merge(data, comparator, aux, start, mid, end);
        }
    }

    public static <T extends Comparable<? super T>> void mergeSort(final T[] data, final Comparator<T> comparator) {
        T[] aux = data.clone();
        mergeSortHelper(data, comparator, aux, 0, data.length-1);
    }

    public static void main(String[] args) {

        Random rand = new Random();
        int n = 40; // size
        int max = 200;
        Integer[] data = new Integer[n];
        for (int i = 0; i < n; i++) {
            data[i] = rand.nextInt(max);
        }

        System.out.println(Arrays.toString(data));
        System.out.println(isSorted(data, Comparator.naturalOrder()));
        mergeSort(data, Comparator.naturalOrder());
        System.out.println(Arrays.toString(data));
        System.out.println(isSorted(data, Comparator.naturalOrder()));
    }
}
