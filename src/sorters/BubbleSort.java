package sorters;
import java.util.Arrays;

public class BubbleSort {
	public static int[] bubbleSort(int[] array) {
	    //make copy of input array
	    int[] sortedArray = Arrays.copyOf(array, array.length);
        int len = array.length;

        //actual sorting algorithm
        for(int i=0; i<len-1; i++){
            for(int j = 0; j<len-1-i; j++){
                if(sortedArray[j]>sortedArray[j+1]){
                    int temp = sortedArray[j];
                    sortedArray[j] = sortedArray[j+1];
                    sortedArray[j+1] = temp;
                }
            }
        }
        return sortedArray;
	}

    //testing
	public static void main(String[] args){
	   int[] array = { 2, 4, 6, 8, 0, 9, 7, 5, 3, 1};
	   System.out.println("Original array: " + Arrays.toString(array));

	   int[] sortedArray = bubbleSort(array);
	   System.out.println("Sorted array: " + Arrays.toString(sortedArray));
	}
}