package com.github.thibstars.netaware;

import com.github.thibstars.netaware.events.EventManager;
import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.PortScanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        IpScanner ipScanner = new IpScanner(eventManager);
        PortScanner portScanner = new PortScanner(eventManager);
        HashMap<String, Set<Integer>> ipAddressesWithOpenPorts = new HashMap<>();
        eventManager.registerHandler(TcpIpPortFoundEvent.class, event -> {
            if (event instanceof TcpIpPortFoundEvent tcpIpPortFoundEvent) {
                String ipAddress = tcpIpPortFoundEvent.getIpAddress();
                Integer tcpIpPort = tcpIpPortFoundEvent.getTcpIpPort();
                LOGGER.info("Found open TCP/IP port '{}' on IP address '{}'.", tcpIpPort, ipAddress);
                ipAddressesWithOpenPorts.get(ipAddress).add(tcpIpPort);
            }
        });
        eventManager.registerHandler(IpAddressFoundEvent.class, event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                String ipAddress = ipAddressFoundEvent.getIpAddress();
                LOGGER.info("Found IP address '{}', will scan for open TCP/IP ports.", ipAddress);
                ipAddressesWithOpenPorts.put(ipAddress, new HashSet<>());
                portScanner.scan(ipAddress);
            }
        });

        int amountOfIpsToScan = 254;
        LOGGER.info("Scanning {} IPs...", amountOfIpsToScan);
        LOGGER.info("\n=============================================================\n");
        ipScanner.scan(new IpScannerInput("192.168.1.0", amountOfIpsToScan));
        LOGGER.info("\n=============================================================\n");
        LOGGER.info("SUMMARY (<IP>: <Ports>)");
        ipAddressesWithOpenPorts.forEach(
                (ipAddress, openPorts) -> LOGGER.info(
                        "{}: {}",
                        ipAddress,
                        openPorts.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                )
        );
    }

}
