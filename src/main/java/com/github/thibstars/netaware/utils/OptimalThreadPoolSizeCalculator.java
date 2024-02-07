package com.github.thibstars.netaware.utils;

import com.github.thibstars.netaware.exceptions.TargetCpuUtilisationOutOfBoundsException;

/**
 * Calculator able to determine the optimal thread pool size required to run tasks of which wait and service times are known.
 *
 * @author Thibault Helsmoortel
 */
public class OptimalThreadPoolSizeCalculator {

    private static final double MAX_TARGET_CPU_USAGE = 1.0;
    private static final double MIN_TARGET_CPU_USAGE = 0.1;

    /**
     * Determines the optimal thread pool size required to run a task with a given wait and service time.
     * This will use the maximum target CPU usage (1).
     *
     * @param waitTime the wait time of the task (response time)
     * @param serviceTime the service time of the task (processing time)
     * @return the optimal thread pool size required to run a task with a given wait and service time
     */
    int get(long waitTime, long serviceTime) {
        return get(MAX_TARGET_CPU_USAGE, waitTime, serviceTime);
    }

    /**
     * Determines the optimal thread pool size required to run a task with a given wait and service time
     * taking into account the desired CPU usage.
     *
     * @param targetCpuUsage desired CPU usage, value between 0 and 1
     * @param waitTime the wait time of the task (response time)
     * @param serviceTime the service time of the task (processing time)
     * @return the optimal thread pool size required to run a task with a given wait and service time
     */
    public int get(double targetCpuUsage, long waitTime, long serviceTime) {
        int availableCores = Runtime.getRuntime().availableProcessors();

        if (targetCpuUsage < MIN_TARGET_CPU_USAGE || targetCpuUsage > MAX_TARGET_CPU_USAGE) {
            throw new TargetCpuUtilisationOutOfBoundsException();
        }

        return (int) (availableCores * targetCpuUsage * (1 + (double) waitTime / serviceTime));
    }

}
