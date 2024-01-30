package com.github.thibstars.netaware;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * @author Thibault Helsmoortel
 */
public class Demo {

    public static void main(String[] args) {
        IpScanner ipScanner = new IpScanner();
        PortScanner portScanner = new PortScanner();

        int amountOfIpsToScan = 254;
        ConcurrentSkipListSet<String> ipsInNetwork = ipScanner.scan(new IpScannerInput("192.168.1.0", amountOfIpsToScan));
        System.out.println("\n===================================================\n");
        System.out.println("Scanning " + amountOfIpsToScan + " IPs...");

        ConcurrentSkipListMap<String, HashSet<Integer>> ipsWithOpenPorts = new ConcurrentSkipListMap<>();

        ipsInNetwork.forEach(ip -> {
            System.out.println("Found IP: " + ip);
            ipsWithOpenPorts.put(ip, new HashSet<>());
        });

        ipsWithOpenPorts.forEach((ip, ports) -> {
            Set<Integer> openPorts = portScanner.scan(ip);
            ports.addAll(openPorts);
            System.out.println("Open ports for IP " + ip + ": " + openPorts.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        });

        System.out.println("Finished scanning!");
        System.out.println("\n===================================================\n");

        System.out.println("Found " + ipsWithOpenPorts.size() + " IPs:");
        ipsWithOpenPorts.forEach((ip, ports) -> System.out.println("IP: " + ip + " Open ports: " + ports.stream().map(String::valueOf).collect(Collectors.joining(", "))));
    }

}
