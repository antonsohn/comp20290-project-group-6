package sorters;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Main {
    private static final String RAPL_PATH = "/sys/class/powercap/intel-rapl/intel-rapl:0/energy_uj";

    /**
     * Reads the current RAPL energy counter for the CPU package.
     * @return Accumulated energy in microjoules (µJ)
     */
    public static long readEnergy() {
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
     * Generates the file suffixes from 25k up to the given limit
     * @param maxThousands Maximum number of elements in the CSV files
     * @return An array containing all the file suffixes
     */
    private static ArrayList<String> getSizesUpTo(int maxThousands) {
        // Get the correct step size
        ArrayList<String> sizes = new ArrayList<>();
        int size = 25;

        // Generate size suffixes
        while (size <  maxThousands) {
            sizes.add(size + "k");
            if (size < 100) {
                size += 25;
            }
            else {
                size += 100;
            }
        }
        return sizes;
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
     * Measure the time elapsed and energy used during this process and returns the results.
     * @param sortAlgorithm sorting algorithm to be run.
     * @param filePath file path of data to be sorted
     * @param iters the number of times that the array is sorted
     * @return an array containing the time elapsed in nanoseconds and the energy used in microjoules
     */
    private static long[] testSorter(Consumer<int[]> sortAlgorithm, String filePath, int iters) throws IOException {
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
        return new long[]{(endTime - startTime), (endEnergy - startEnergy)};
    }

    /**
     * Reads a given CSV file containing ints to an array {@code iters} times.
     * Measure the time elapsed and energy used during this process and returns the results.
     * @param filePath file path of CSV to be read
     * @param iters the number of times that the CSV file is read
     * @return an array containing the time elapsed in nanoseconds and the energy used in microjoules
     */
    private static long[] testCSVtoArr(String filePath, int iters) throws IOException {
        long startTime = System.nanoTime();
        long startEnergy = readEnergy();

        for (int i=0; i<iters; i++) {
            CSVtoArr(filePath);
        }

        // Reading and printing the time elapsed and energy used
        long endTime = System.nanoTime();
        long endEnergy = readEnergy();
        return new long[]{(endTime - startTime), (endEnergy - startEnergy)};
    }

    /**
     *
     * @param writer
     * @param sortAlgorithm
     * @param dataName
     * @param iters
     * @throws IOException
     */
    private static void writeResult (FileWriter writer, Consumer<int[]> sortAlgorithm, String dataName, int iters, ArrayList<String> sizes) throws IOException {
        // run the test once for each specified input size
        for (String inputSize : sizes) {
            // run the test and write the results
            long[] result = testSorter(sortAlgorithm, "resources/" + dataName + inputSize + ".csv", iters);
            writer.write("\n" + iters + " iterations on one file of size " + inputSize);
            writer.write("\nTime elapsed: " + result[0] + "ns");
            writer.write("\nEnergy used: " + result[1] + " µJ");
        }
    }

    /**
     *
     * @param writer
     * @param sortAlgorithm
     * @param dataName
     * @param iters
     */
    private static void writeAverageResult (FileWriter writer, Consumer<int[]> sortAlgorithm, String dataName, int iters, ArrayList<String> sizes) throws IOException {
        for (String inputSize : sizes) {
            // In the random case, we have 10 randomly sorted arrays of a given size, and we are interested in the average time elapsed and average energy used over all 10 arrays
            long totalTime = 0;
            long totalEnergy = 0;
            for (int i = 1; i <= 10; i++) {
                long[] result;
                if (sortAlgorithm == null) {
                    result = testCSVtoArr("resources/" + dataName + inputSize + i + ".csv", iters);
                }
                else {
                    result = testSorter(sortAlgorithm, "resources/" + dataName + inputSize + i + ".csv", iters);
                }
                totalTime += result[0];
                totalEnergy += result[1];
            }
            writer.write("\n" + iters + " iterations each on ten files of size " + inputSize);
            writer.write("\nAverage time elapsed: " + totalTime/10 + "ns");
            writer.write("\nAverage energy used: " + totalEnergy/10 + " µJ");
        }
    }

    public static void main(String[] args) throws IOException {

        // Defined limits based on the paper
        ArrayList<String> limit200k = getSizesUpTo(200);
        ArrayList<String> limit500k = getSizesUpTo(500);
        ArrayList<String> limit1Mil = getSizesUpTo(1000);

        // --------------------- Testing bubble sort ---------------------

        // The output file for bubble sort results
        FileWriter writer = new FileWriter("bubbleSortResults.txt");

        // Worst case
        writer.write("Bubble sort, worst case, reverse-sorted.");
        // writeResult(writer, BubbleSort::bubbleSort, "reverseSorted", 400, limit500k);

        // Best case
        writer.write("\n\nBubble sort, best case, sorted.");
        // writeResult(writer, BubbleSort::bubbleSort, "sorted", 400, limit500k);

        // Random case
        writer.write("\n\nBubble sort, random case, randomly sorted.");
        // writeAverageResult(writer, BubbleSort::bubbleSort, "randomlySorted", 40, limit500k);

        writer.close();
        System.out.println("Finished bubble sort test");

        // --------------------- Testing merge sort ---------------------

        // The output file for merge sort results
        writer = new FileWriter("mergeSortResults.txt");

        // Worst case
        writer.write("Merge sort, worst case, alternating elements.");
        writeResult(writer, MergeSort::mergeSort, "alternatingElements", 30, limit1Mil);

        // Best case
        writer.write("\n\nMerge sort, best case, sorted.");
        writeResult(writer, MergeSort::mergeSort, "sorted", 30, limit1Mil);

        // Random case
        writer.write("\n\nMerge sort, random case, randomly sorted.");
        writeAverageResult(writer, MergeSort::mergeSort, "randomlySorted", 3, limit1Mil);

        writer.close();
        System.out.println("Finished merge sort test");

        // --------------------- Testing quick sort ---------------------

        // The output file for quick sort results
        writer = new FileWriter("quickSortResults.txt");

        // Worst case
        writer.write("Quick sort, worst case, reverse-sorted.");
        writeResult(writer, QuickSort::quickSort, "reverseSorted", 30, limit200k);

        // Best case
        writer.write("\n\nQuick sort, best case, evenly-partitioned.");
        writeResult(writer, QuickSort::quickSort, "evenlyPartitioned", 30, limit1Mil);

        // Random case
        writer.write("\n\nQuick sort, random case, randomly sorted.");
        writeAverageResult(writer, QuickSort::quickSort, "randomlySorted", 3, limit1Mil);

        writer.close();
        System.out.println("Finished quick sort test");

        // --------------------- Testing counting sort ---------------------

        // The output file for counting sort results
        writer = new FileWriter("countingSortResults.txt");

        // Worst case
        writer.write("Counting sort, worst case, randomly sorted, small k.");
        writeResult(writer, CountSort::countSort, "randomlySortedSmallk", 30, limit1Mil);

        // Best case
        writer.write("\n\nCounting sort, best case, randomly sorted, big k.");
        writeResult(writer, CountSort::countSort, "randomlySortedBigk", 30, limit1Mil);

        writer.close();
        System.out.println("Finished counting sort test");

        // --------------------- Testing CSV read ---------------------

        // The output file for CSV read results
        writer = new FileWriter("CSVtoArrResults.txt");

        // Sorted
        writer.write("CSV read, sorted.");
        writeResult(writer, null, "sorted", 400, limit1Mil);

        // Reverse-sorted
        writer.write("\n\nCSV read, reverse-sorted.");
        writeResult(writer, null, "reverseSorted", 400, limit1Mil);

        // Randomly sorted
        writer.write("\n\nCSV read, randomly sorted.");
        writeAverageResult(writer, null, "randomlySorted", 40, limit1Mil);

        // Alternating elements
        writer.write("\n\nCSV read, alternating elements.");
        writeResult(writer, null, "alternatingElements", 400, limit1Mil);

        // Evenly-partitioned
        writer.write("\n\nCSV read, evenly-partitioned.");
        writeResult(writer, null, "evenlyPartitioned", 400, limit1Mil);

        // Counting worst
        writer.write("\n\nCSV read, counting worst.");
        writeResult(writer, null, "countingWorst", 400, limit1Mil);

        // Randomly sorted, small k
        writer.write("\n\nCSV read, randomly sorted, small k.");
        writeAverageResult(writer, null, "randomlySortedSmallk", 40, limit1Mil);

        // Randomly sorted, big k
        writer.write("\n\nCSV read, randomly sorted, big k.");
        writeAverageResult(writer, null, "randomlySortedBigk", 40,  limit1Mil);

        writer.close();
        System.out.println("Finished CSVtoArr test");

    }
}
