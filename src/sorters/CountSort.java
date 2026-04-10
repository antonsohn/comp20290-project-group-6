package sorters;

import java.util.Arrays;

public class CountSort {
    public static int[] countSort(int[] array){

        int[] sorted = new int[array.length];
        int[] numbers = new int[Arrays.stream(array).max().getAsInt() + 1];
        Arrays.fill(numbers, 0);

        for (int j : array) {
            numbers[j]++;
        }

        for(int i = 1; i< numbers.length; i++){
            numbers[i] = numbers[i] + numbers[ i-1];
        }

        for(int i = array.length - 1; i >= 0; i--){
            sorted[numbers[array[i]]-1] = array[i];
            numbers[array[i]] --;
        }

        return sorted;

    }


}
