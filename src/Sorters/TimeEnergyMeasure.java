package Sorters;

import java.io.IOException;

public class TimeEnergyMeasure {

    private static long readEnergy() throws IOException {
        // TODO
        return 0;
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        long startEnergy = readEnergy();

        // programme here

        long endTime = System.nanoTime();
        long endEnergy = readEnergy();

        long timeElapsed = endTime - startTime;
        long energyUsed = endEnergy - startEnergy;

        System.out.println("Time elapsed: " + timeElapsed + "ns");
        System.out.println("Energy used: " + energyUsed + " µJ");
    }

}
