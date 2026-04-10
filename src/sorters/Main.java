package sorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class Main {
    private static final String RAPL_PATH = "/sys/class/powercap/intel-rapl/intel-rapl:0/energy_uj";

    /**
     * Reads the current RAPL energy counter for the CPU package.
     * @return Accumulated energy in microjoules (µJ)
     */
    private static long readEnergy() {
        try {
            // Try to read the file, trim any newline characters, and parse it as a long
            String energyString = Files.readString(Path.of(RAPL_PATH)).trim();
            return Long.parseLong(energyString);
        } catch (Exception e) {
            // If the file does not exist or cannot be read (like on WSL or Windows) return 0
            return 0;
        }
    }

    /**
     * Reads a CSV file containing ints to an array.
     * @return array of ints.
     */
    private static int[] CSVtoArr(String filePath) throws IOException {
        String line = Files.readString(Path.of(filePath)).trim();
        String[] parts = line.split(",");

        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i].trim());
        }
        return arr;
    }

    /**
     * Reads a given CSV file containing ints to an array, and then sorts the array {@code iters} times using the given sorting algorithm.
     * Measure the time elapsed and energy used during this process and prints the results.
     * @param sortAlgorithm sorting algorithm to be run.
     * @param filePath file path of data to be sorted
     * @param iters the number of times that the array is sorted
     */
    private static void testSorter(Consumer<int[]> sortAlgorithm, String filePath, int iters) throws IOException {
        long startTime = System.nanoTime();
        long startEnergy = readEnergy();

        // Following the paper, we read the CSV once and then sort a copy of the data multiple times
        int[] arr = CSVtoArr(filePath);
        for (int i = 0; i < iters; i++) {
            int[] arrCopy = new int[arr.length];
            System.arraycopy(arr, 0, arrCopy, 0, arr.length);
            sortAlgorithm.accept(arrCopy);
        }

        // Reading and printing the time elapsed and energy used
        long endTime = System.nanoTime();
        long endEnergy = readEnergy();
        System.out.println(filePath);
        System.out.println("Time elapsed: " + (endTime - startTime) + "ns");
        System.out.println("Energy used: " + (endEnergy - startEnergy) + " µJ");
    }

    /**
     * Reads a given CSV file containing ints to an array {@code iters} times.
     * Measure the time elapsed and energy used during this process and prints the results.
     * @param filePath file path of CSV to be read
     * @param iters the number of times that the CSV file is read
     */
    private static void testCSVtoArr(String filePath, int iters) throws IOException {
        long startTime = System.nanoTime();
        long startEnergy = readEnergy();

        int[] arr = CSVtoArr(filePath);

        // Reading and printing the time elapsed and energy used
        long endTime = System.nanoTime();
        long endEnergy = readEnergy();
        System.out.println(filePath);
        System.out.println("Time elapsed: " + (endTime - startTime) + "ns");
        System.out.println("Energy used: " + (endEnergy - startEnergy) + " µJ");
    }

    public static void main(String[] args) throws IOException {

        // The range of sizes of data that we will test our sorting algorithms on
        String[] inputSizes =  {"5K", "10K", "15K"};

        // --------------------- Testing mege sort ---------------------

        // Testing once for each input size
        for (String inputSize : inputSizes) {

            // Worst case
            System.out.println("Merge sort, worst case: alternating elements.");
            testSorter(MergeSort::mergeSort, "resources/alternatingElements" + inputSize + ".csv", 30);

            // Best case
            System.out.println("Merge sort, best case: sorted.");
            testSorter(MergeSort::mergeSort, "resources/sorted" + inputSize + ".csv", 30);

            // Random case
            System.out.println("Merge sort, random case: randomly sorted.");
            for (int i = 1; i <= 10; i++) {
                testSorter(MergeSort::mergeSort, "resources/randomlySorted" + inputSize + i + ".csv", 3);
            }

        }

        // --------------------- Testing CSV read ---------------------

        // Testing once for each input size
        for (String inputSize : inputSizes) {

            // Sorted
            System.out.println("CSV read, sorted.");
            testCSVtoArr("resources/sorted" + inputSize + ".csv", 400);

            // Reverse-sorted
            System.out.println("CSV read, reverse-sorted.");
            testCSVtoArr("resources/reverseSorted" + inputSize + ".csv", 400);

            // Randomly sorted
            System.out.println("CSV read, randomly sorted.");
            for (int i = 1; i <= 10; i++) {
                testCSVtoArr("resources/randomlySorted" + inputSize + i + ".csv", 40);
            }

            // Alternating elements
            System.out.println("CSV read, alternating elements.");
            testCSVtoArr("resources/alternatingElements" + inputSize + ".csv", 400);

            // Evenly-partitioned
            System.out.println("CSV read, evenly-partitioned.");
            testCSVtoArr("resources/evenlyPartitioned" + inputSize + ".csv", 400);

            // Counting worst // TODO what is this??
            System.out.println("CSV read, counting worst.");
            testCSVtoArr("resources/countingWorst" + inputSize + ".csv", 400);

            // Randomly sorted, small k
            System.out.println("CSV read, randomly sorted, small k.");
            for (int i = 1; i <= 10; i++) {
                testCSVtoArr("resources/randomlySortedSmallk" + inputSize + i + ".csv", 40);
            }

            // Randomly sorted, big k
            System.out.println("CSV read, randomly sorted, big k.");
            for (int i = 1; i <= 10; i++) {
                testCSVtoArr("resources/randomlySortedBigk" + inputSize + i + ".csv", 40);
            }

        }
    }

}
