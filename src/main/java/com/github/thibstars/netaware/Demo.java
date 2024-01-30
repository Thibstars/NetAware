package com.github.thibstars.netaware;

import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.PortScanner;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        IpScanner ipScanner = new IpScanner();
        PortScanner portScanner = new PortScanner();

        int amountOfIpsToScan = 254;
        ConcurrentSkipListSet<String> ipsInNetwork = ipScanner.scan(new IpScannerInput("192.168.1.0", amountOfIpsToScan));
        LOGGER.info("\n===================================================\n");
        LOGGER.info("Scanning {} IPs...", amountOfIpsToScan);

        ConcurrentSkipListMap<String, Set<Integer>> ipsWithOpenPorts = new ConcurrentSkipListMap<>();

        ipsInNetwork.forEach(ip -> {
            LOGGER.info("Found IP: {}", ip);
            ipsWithOpenPorts.put(ip, new HashSet<>());
        });

        ipsWithOpenPorts.forEach((ip, ports) -> {
            Set<Integer> openPorts = portScanner.scan(ip);
            ports.addAll(openPorts);
            LOGGER.info("Open ports for IP {}: {}", ip, openPorts.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        });

        LOGGER.info("Finished scanning!");
        LOGGER.info("\n===================================================\n");

        LOGGER.info("Found {} IPs:", ipsWithOpenPorts.size());
        ipsWithOpenPorts.forEach((ip, ports) -> LOGGER.info("IP: {} Open ports: {}", ip, ports.stream().map(String::valueOf).collect(Collectors.joining(", "))));
    }

}
