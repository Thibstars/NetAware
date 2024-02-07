package com.github.thibstars.netaware.exceptions;

/**
 * @author Thibault Helsmoortel
 */
public class TargetCpuUtilisationOutOfBoundsException extends RuntimeException {

    public TargetCpuUtilisationOutOfBoundsException() {
        super("Target CPU utilisation must be a value [0..1].");
    }
}
