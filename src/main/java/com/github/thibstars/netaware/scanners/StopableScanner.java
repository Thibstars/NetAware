package com.github.thibstars.netaware.scanners;

/**
 * Contract for a scanner that is able to be stopped.
 *
 * @author Thibault Helsmoortel
 */
public interface StopableScanner<I> extends Scanner<I> {

    /**
     * Stops a scanning job.
     *
     * @param input the original scan input
     */
    void stop(I input);

}
