package Sorters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class QuickSort {
    // Worst case: O(n^2), Average O(nLog(n))
    public static <T extends Comparable<? super T>> void quickSort(final T[] data, final Comparator<T> comparator) {
        int low = 0;
        int high = data.length - 1;

        if (low < high) {
            // Find the partition index
            int pi = partition(data, low, high, comparator);

            // Recursively sort elements before and after partition
            quickSortRecursive(data, low, pi - 1, comparator);
            quickSortRecursive(data, pi + 1, high, comparator);
        }
    }

    private static <T extends Comparable<? super T>> void quickSortRecursive(T[] arr, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            // Find the partition index
            int pi = partition(arr, low, high, comparator);

            // Recursively sort elements before and after partition
            quickSortRecursive(arr, low, pi - 1, comparator);
            quickSortRecursive(arr, pi + 1, high, comparator);
        }
    }

    private static <T extends Comparable<? super T>> int partition(T[] arr, int low, int high, Comparator<T> comparator) {
        T pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (comparator.compare(arr[j], pivot) <= 0) {
                i++;

                T temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Swap the pivot element with the element at i+1
        T temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    // Average: O(nLog(n)), Worst case: O(n^2)
    public static <T extends Comparable<? super T>> void quickSort(final List<T> data, final Comparator<T> comparator) {
        int low = 0;
        int high = data.size() - 1;

        if (low < high) {
            // Find the partition index
            int pi = partition(data, low, high, comparator);

            // Recursively sort elements before and after partition
            quickSortRecursive(data, low, pi - 1, comparator);
            quickSortRecursive(data, pi + 1, high, comparator);
        }
    }

    private static <T extends Comparable<? super T>> void quickSortRecursive(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            // Find the partition index
            int pi = partition(list, low, high, comparator);

            // Recursively sort elements before and after partition
            quickSortRecursive(list, low, pi - 1, comparator);
            quickSortRecursive(list, pi + 1, high, comparator);
        }
    }

    private static <T extends Comparable<? super T>> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            // Check whether current element is smaller than the pivot
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;

                // Put current element in sorted spot
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        // Swap the pivot element with the element at i+1
        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }

    // Testing
    public static void main(String[] args) {
        Integer[] arr = new Integer[] {1, 234, 5, 1,21, 56, 82, 609, 58, 2, 98};
        List<Integer> list = Arrays.asList(arr);


        // Test Array MergeSort
        quickSort(arr, Comparator.naturalOrder());
        System.out.print("QuickSort Array: [ ");
        for (Integer integer : arr) {
            System.out.print(integer + " ");
        }
        System.out.println("]");

        // Test List MergeSort
        quickSort(list, Comparator.naturalOrder());
        System.out.print("QuickSort List: ( ");
        for (Integer integer : list) {
            System.out.print(integer + " ");
        }
        System.out.println(")");
    }
}
