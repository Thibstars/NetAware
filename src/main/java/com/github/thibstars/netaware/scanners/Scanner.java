package com.github.thibstars.netaware.scanners;


/**
 * Scanner contract.
 *
 * @author Thibault Helsmoortel
 */
public interface Scanner<I> {

    /**
     * Performs a scan based on the given input.
     *
     * @param input context for the scan job
     */
    void scan(I input);

}
