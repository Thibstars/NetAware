package com.github.thibstars.netaware;

import com.github.thibstars.netaware.events.MacFoundEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.MacScanner;
import com.github.thibstars.netaware.scanners.PortScanner;
import java.net.InetAddress;
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
        MacScanner macScanner = new MacScanner(eventManager);

        HashMap<InetAddress, Set<Integer>> ipAddressesWithOpenPorts = new HashMap<>();
        eventManager.registerHandler(TcpIpPortFoundEvent.class, event -> {
            if (event instanceof TcpIpPortFoundEvent tcpIpPortFoundEvent) {
                InetAddress ipAddress = tcpIpPortFoundEvent.getIpAddress();
                Integer tcpIpPort = tcpIpPortFoundEvent.getTcpIpPort();
                LOGGER.info("Found open TCP/IP port '{}' on IP address '{}'.", tcpIpPort, ipAddress.getHostAddress());
                ipAddressesWithOpenPorts.get(ipAddress).add(tcpIpPort);
            }
        });
        HashMap<InetAddress, String> ipAddressesWithMacAddress = new HashMap<>();
        eventManager.registerHandler(MacFoundEvent.class, event -> {
            if (event instanceof MacFoundEvent macFoundEvent) {
                InetAddress ipAddress = macFoundEvent.getIpAddress();
                String macAddress = macFoundEvent.getMacAddress();
                LOGGER.info("Found MAC address '{}' on IP address '{}'.", macAddress, ipAddress.getHostAddress());
                ipAddressesWithMacAddress.replace(ipAddress, macAddress);
            }
        });
        eventManager.registerHandler(IpAddressFoundEvent.class, event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                LOGGER.info("Found IP address '{}', will scan for open TCP/IP ports.", ipAddress.getHostAddress());
                ipAddressesWithOpenPorts.put(ipAddress, new HashSet<>());
                ipAddressesWithMacAddress.put(ipAddress, null);
                portScanner.scan(ipAddress);
                macScanner.scan(ipAddress);
            }
        });

        int amountOfIpsToScan = 254;
        LOGGER.info("Scanning {} IPs...", amountOfIpsToScan);
        LOGGER.info("\n=============================================================\n");
        ipScanner.scan(new IpScannerInput("192.168.1.0", amountOfIpsToScan));
        LOGGER.info("\n=============================================================\n");
        LOGGER.info("SUMMARY");
        LOGGER.info("(<IP>: <Ports>)");
        ipAddressesWithOpenPorts.forEach(
                (ipAddress, openPorts) -> LOGGER.info(
                        "{}: {}",
                        ipAddress.getHostAddress(),
                        openPorts.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                )
        );
        LOGGER.info("(<IP>: <MAC>)");
        ipAddressesWithMacAddress.forEach(
                (ipAddress, macAddress) -> {
                    if (macAddress != null) {
                        LOGGER.info(
                                "{}: {}",
                                ipAddress.getHostAddress(),
                                macAddress
                        );
                    }
                }
        );
    }

}
