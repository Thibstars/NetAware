package com.github.thibstars.netaware.exceptions;

/**
 * Exception to throw when target CPU usage is not a value between 0 and 1.
 *
 * @author Thibault Helsmoortel
 */
public class TargetCpuUtilisationOutOfBoundsException extends RuntimeException {

    public TargetCpuUtilisationOutOfBoundsException() {
        super("Target CPU usage must be a value [0..1].");
    }
}
