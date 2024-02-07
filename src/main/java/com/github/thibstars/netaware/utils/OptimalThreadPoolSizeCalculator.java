package com.github.thibstars.netaware.utils;

import com.github.thibstars.netaware.exceptions.TargetCpuUtilisationOutOfBoundsException;

/**
 * @author Thibault Helsmoortel
 */
public class OptimalThreadPoolSizeCalculator {

    private static final double MAX_TARGET_CPU_UTILISATION = 1.0;
    private static final double MIN_TARGET_CPU_UTILISATION = 0.1;

    int get(long waitTime, long serviceTime) {
        return get(MAX_TARGET_CPU_UTILISATION, waitTime, serviceTime);
    }

    public int get(double targetCpuUtilisation, long waitTime, long serviceTime) {
        int availableCores = Runtime.getRuntime().availableProcessors();

        if (targetCpuUtilisation < MIN_TARGET_CPU_UTILISATION || targetCpuUtilisation > MAX_TARGET_CPU_UTILISATION) {
            throw new TargetCpuUtilisationOutOfBoundsException();
        }

        return (int) (availableCores * targetCpuUtilisation * (1 + (double) waitTime / serviceTime));
    }

}
