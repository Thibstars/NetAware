package com.github.thibstars.netaware.scanners;

/**
 * Input required to start an IP scan.
 *
 * @author Thibault Helsmoortel
 */
public record IpScannerInput(
        String firstIpInTheNetwork, // e.g: 192.168.1.0
        int amountOfIpsToScan // e.g: 254
) {

}
