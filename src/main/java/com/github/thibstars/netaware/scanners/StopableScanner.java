package com.github.thibstars.netaware.scanners;

/**
 * @author Thibault Helsmoortel
 */
public interface StopableScanner<I> extends Scanner<I> {

    void stop(I input);

}
