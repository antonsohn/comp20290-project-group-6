package sorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickSort {

    /* ---- Array Implementation ---- */

    // Worst case: O(n^2), Average O(nLog(n))
    public static void quickSort(final int[] data) {
        if (data == null || data.length <= 1) return;
        quickSort(data, 0, data.length - 1);
    }

    private static void quickSort(int[] A, int p, int r) {
        if (p < r) {
            int q = partition(A, p, r);

            quickSort(A, p, q - 1);
            quickSort(A, q + 1, r);
        }
    }

    private static int partition(int[] A, int p, int r) {
        int pivot = A[r];
        int i = (p - 1);

        for (int j = p; j < r; j++) {
            if (A[j] <= pivot) {
                i++;

                int temp = A[i];
                A[i] = A[j];
                A[j] = temp;
            }
        }

        int temp = A[i + 1];
        A[i + 1] = A[r];
        A[r] = temp;

        return i + 1;
    }

    /* ---- List implementation ---- */

    // Average: O(nLog(n)), Worst case: O(n^2)
    public static void quickSort(final List<Integer> data) {
        if (data == null || data.size() <= 1) return;
        quickSort(data, 0, data.size() - 1);
    }

    private static void quickSort(List<Integer> A, int p, int r) {
        if (p < r) {
            int q = partition(A, p, r);
            quickSort(A, p, q - 1);
            quickSort(A, q + 1, r);
        }
    }

    private static int partition(List<Integer> A, int p, int r) {
        int pivot = A.get(r);
        int i = (p - 1);

        for (int j = p; j < r; j++) {
            if (A.get(j) <= pivot) {
                i++;

                int temp = A.get(i);
                A.set(i, A.get(j));
                A.set(j, temp);
            }
        }

        int temp = A.get(i + 1);
        A.set(i + 1, A.get(r));
        A.set(r, temp);

        return i + 1;
    }

    // Testing
    public static void main(String[] args) {
        int[] arr = new int[] {1, 234, 5, 1,21, 56, 82, 609, 58, 2, 98};
        List<Integer> list = new ArrayList<>(Arrays.stream(arr).boxed().toList());


        // Test Array MergeSort
        quickSort(arr);
        System.out.print("QuickSort Array: [ ");
        for (Integer integer : arr) {
            System.out.print(integer + " ");
        }
        System.out.println("]");

        // Test List MergeSort
        quickSort(list);
        System.out.print("QuickSort List: ( ");
        for (Integer integer : list) {
            System.out.print(integer + " ");
        }
        System.out.println(")");
    }
}
