package sorters;

import java.io.FileWriter;
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
     * Quality of life method that writes the result of a test to results.txt
     */
    private static void writeResults(FileWriter writer, long[] result, int iters, String inputSize) throws IOException {
        writer.write("\n" + iters + " iterations on one file of size " + inputSize);
        writer.write("\nTime elapsed: " + result[0] + "ns");
        writer.write("\nEnergy used: " + result[1] + " µJ");
    }

    public static void main(String[] args) throws IOException {

        // The output file for the results
        FileWriter writer = new FileWriter("results.txt");

        // The range of sizes of data that we will test our sorting algorithms on
        String[] inputSizes =  {"Test"};

        // --------------------- Testing mege sort ---------------------

        // Worst case
        int iters = 30; // This is the number of times that each array is sorted after being read from a CSV
        writer.write("Merge sort, worst case, alternating elements.");
        // We run the test once for each specified input size
        for (String inputSize : inputSizes) {
            // We run the test and write the results
            long[] result = testSorter(MergeSort::mergeSort, "resources/alternatingElements" + inputSize + ".csv", iters);
            writeResults(writer, result, iters, inputSize);
        }

        // Best case
        iters = 30;
        writer.write("\n\nMerge sort, best case, sorted.");
        for (String inputSize : inputSizes) {
            long[] result = testSorter(MergeSort::mergeSort, "resources/sorted" + inputSize + ".csv", iters);
            writeResults(writer, result, iters, inputSize);
        }

        // Random case
        iters = 3;
        writer.write("\n\nMerge sort, random case, randomly sorted.");
        for (String inputSize : inputSizes) {
            writer.write("\n" + iters + " iterations each on ten files of size " + inputSize);
            // In the random case, we have 10 randomly sorted arrays of a given size, and we are interested in the average time elapsed and average energy used over all 10 arrays
            long totalTime = 0;
            long totalEnergy = 0;
            for (int i = 1; i <= 10; i++) {
                long[] result = testSorter(MergeSort::mergeSort, "resources/randomlySorted" + inputSize + i + ".csv", iters);
                totalTime += result[0];
                totalEnergy += result[1];
            }
            writer.write("\nAverage time elapsed: " + totalTime/10 + "ns");
            writer.write("\nAverage energy used: " + totalEnergy/10 + " µJ");
        }

        // --------------------- Testing CSV read ---------------------

//        // Sorted
//        iters = 400;
//        writer.write("\n\nCSV read, sorted.");
//        for (String inputSize : inputSizes) {
//            long[] result = testCSVtoArr("resources/sorted" + inputSize + ".csv", iters);
//            writeResults(writer, result, iters, inputSize);
//        }
//
//        // Reverse-sorted
//        iters = 400;
//        writer.write("\n\nCSV read, reverse-sorted.");
//        for (String inputSize : inputSizes) {
//            long[] result = testCSVtoArr("resources/reverseSorted" + inputSize + ".csv", iters);
//            writeResults(writer, result, iters, inputSize);
//        }
//
//        // Randomly sorted
//        iters = 40;
//        writer.write("\n\nCSV read, randomly sorted.");
//        for (String inputSize : inputSizes) {
//            writer.write("\n" + iters + " iterations each on ten files of size " + inputSize);
//            long totalTime = 0;
//            long totalEnergy = 0;
//            for (int i = 1; i <= 10; i++) {
//                long[] result = testCSVtoArr("resources/randomlySorted" + inputSize + i + ".csv", iters);
//                totalTime += result[0];
//                totalEnergy += result[1];
//            }
//            writer.write("\nAverage time elapsed: " + totalTime/10 + "ns");
//            writer.write("\nAverage energy used: " + totalEnergy/10 + " µJ");
//        }
//
//        // Alternating elements
//        iters = 400;
//        writer.write("\n\nCSV read, alternating elements.");
//        for (String inputSize : inputSizes) {
//            long[] result = testCSVtoArr("resources/alternatingElements" + inputSize + ".csv", iters);
//            writeResults(writer, result, iters, inputSize);
//        }
//
//        // Evenly-partitioned
//        iters = 400;
//        writer.write("\n\nCSV read, evenly-partitioned.");
//        for (String inputSize : inputSizes) {
//            long[] result = testCSVtoArr("resources/evenlyPartitioned" + inputSize + ".csv", iters);
//            writeResults(writer, result, iters, inputSize);
//        }
//
//        // Counting worst // TODO what is this??
//        iters = 400;
//        writer.write("\n\nCSV read, counting worst.");
//        for (String inputSize : inputSizes) {
//            long[] result = testCSVtoArr("resources/countingWorst" + inputSize + ".csv", iters);
//            writeResults(writer, result, iters, inputSize);
//        }
//
//        // Randomly sorted, small k
//        iters = 40;
//        writer.write("\n\nCSV read, randomly sorted, small k.");
//        for (String inputSize : inputSizes) {
//            writer.write("\n" + iters + " iterations each on ten files of size " + inputSize);
//            long totalTime = 0;
//            long totalEnergy = 0;
//            for (int i = 1; i <= 10; i++) {
//                long[] result = testCSVtoArr("resources/randomlySortedSmallk" + inputSize + i + ".csv", iters);
//                totalTime += result[0];
//                totalEnergy += result[1];
//            }
//            writer.write("\nAverage time elapsed: " + totalTime/10 + "ns");
//            writer.write("\nAverage energy used: " + totalEnergy/10 + " µJ");
//        }
//
//        // Randomly sorted, big k
//        iters = 40;
//        writer.write("\n\nCSV read, randomly sorted, big k.");
//        for (String inputSize : inputSizes) {
//            writer.write("\n" + iters + " iterations each on ten files of size " + inputSize);
//            long totalTime = 0;
//            long totalEnergy = 0;
//            for (int i = 1; i <= 10; i++) {
//                long[] result = testCSVtoArr("resources/randomlySortedBigk" + inputSize + i + ".csv", iters);
//                totalTime += result[0];
//                totalEnergy += result[1];
//            }
//            writer.write("\nAverage time elapsed: " + totalTime/10 + "ns");
//            writer.write("\nAverage energy used: " + totalEnergy/10 + " µJ");
//        }
//
        writer.close();

    }
}
