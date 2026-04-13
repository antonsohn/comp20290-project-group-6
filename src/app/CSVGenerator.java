package app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class CSVGenerator {
    private static final int START_SIZE = 25_000;
    private static final int MAX_SIZE = 1_000_000;

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("resources"));

            int size = START_SIZE;
            while (size <= MAX_SIZE) {
                // Get right suffix
                String sizeName = (size / 1000) + "k";
                System.out.println("Generating files for size: " + sizeName);

                // Generate all CSVs for current size
                writeCSV(generateSorted(size), "sorted" + sizeName + ".csv");
                writeCSV(generateReverseSorted(size), "reverseSorted" + sizeName + ".csv");
                writeCSV(generateMergeWorstCase(size), "alternatingElements" + sizeName + ".csv");
                writeCSV(generateQuickBestCase(size), "evenlyPartitioned" + sizeName + ".csv");
                writeCSV(generateRandom(size, 0, 10), "randomlySortedSmallk" + sizeName + ".csv");
                writeCSV(generateRandom(size, 0, 100_000_000), "randomlySortedBigk" + sizeName + ".csv");
                writeCSV(generateRandom(size, Integer.MIN_VALUE, Integer.MAX_VALUE), "countingWorst" + sizeName + ".csv");

                for (int j = 1; j <= 10; j++) {
                    writeCSV(generateRandom(size, Integer.MIN_VALUE, Integer.MAX_VALUE), "randomlySorted" + sizeName + j + ".csv");
                }

                // Increment by the correct step size
                if (size < 100_000) {
                    size += 25_000;
                }
                else {
                    size += 100_000;
                }
            }
            System.out.println("All CSV files generated successfully!");

        } catch (IOException e) {
            System.err.println("Error writing CSV files: " + e.getMessage());
        }
    }

    private static int[] generateSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = i;
        return arr;
    }

    private static int[] generateReverseSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = (size - 1) - i;
        return arr;
    }

    private static int[] generateRandom(int size, int min, int max) {
        int[] arr = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                arr[i] = rand.nextInt();
            } else {
                arr[i] = rand.nextInt((max - min) + 1) + min;
            }
        }
        return arr;
    }

    private static int[] generateMergeWorstCase(int size) {
        int[] arr = generateSorted(size);
        return unmerge(arr);
    }

    private static int[] unmerge(int[] arr) {
        if (arr.length <= 1) return arr;
        int[] left = new int[(arr.length + 1) / 2];
        int[] right = new int[arr.length / 2];
        for (int i = 0; i < arr.length; i++) {
            if (i % 2 == 0) left[i / 2] = arr[i];
            else right[i / 2] = arr[i];
        }
        left = unmerge(left);
        right = unmerge(right);
        int[] res = new int[arr.length];
        System.arraycopy(left, 0, res, 0, left.length);
        System.arraycopy(right, 0, res, left.length, right.length);
        return res;
    }

    private static int[] generateQuickBestCase(int size) {
        int[] arr = generateSorted(size);
        buildQuickBest(arr, 0, size - 1);
        return arr;
    }

    private static void buildQuickBest(int[] arr, int low, int high) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        buildQuickBest(arr, low, mid - 1);
        buildQuickBest(arr, mid + 1, high);
        int temp = arr[mid];
        arr[mid] = arr[high];
        arr[high] = temp;
    }

    private static void writeCSV(int[] arr, String filename) throws IOException {
        Path path = Path.of("resources", filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < arr.length; i++) {
                writer.write(String.valueOf(arr[i]));
                if (i < arr.length - 1) writer.write(",");
            }
        }
    }
}