package Sorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class Main {

    /**
     * Reads the current RAPL energy counter
     * @return
     * @throws IOException
     */
    private static long readEnergy() throws IOException {
        // TODO
        return 0;
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
     * @param sortAlgorithm sorting algorithm to be run.
     * @param filePath file path of data to be sorted
     */
    private static void runSorter(Consumer<int[]> sortAlgorithm, String filePath, int iters) throws IOException {
        int[] arr = CSVtoArr(filePath);
        for (int i = 0; i < iters; i++) {
            int[] arrCopy = new int[arr.length];
            System.arraycopy(arr, 0, arrCopy, 0, arr.length);
            sortAlgorithm.accept(arrCopy);
        }
    }

    // The file paths of the various CSV files to be sorted by mergesort
    private static final List<String> mergeSortFilePaths = List.of(
            "resources/testfile.csv",
            "resources/testfile2.csv"
    );


    public static void main(String[] args) throws IOException {

        Consumer<int[]> sortAlgorithm = MergeSort::mergeSort; // The sorting algorithm we will measure
        List<String> filePaths =  mergeSortFilePaths; // The file paths of the CSV files we will sort

        for (String path : filePaths) {

            //System.out.println(Arrays.toString(CSVtoArr(path)));

            long startTime = System.nanoTime();
            long startEnergy = readEnergy();

            // --------------- Programme to be measured ---------------

            runSorter(sortAlgorithm, path, 400);

            // ------------ End of programme to be measured ------------

            long endTime = System.nanoTime();
            long endEnergy = readEnergy();
            long timeElapsed = endTime - startTime;
            long energyUsed = endEnergy - startEnergy;

            System.out.println(path);
            System.out.println("Time elapsed: " + timeElapsed + "ns");
            System.out.println("Energy used: " + energyUsed + " µJ");
        }

    }

}
