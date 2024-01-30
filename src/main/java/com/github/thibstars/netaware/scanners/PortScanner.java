package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thibault Helsmoortel
 */
public class PortScanner implements Scanner<String, Integer> {

    private static final int TIMEOUT = 200;
    private static final long SERVICE_TIME = 10L; // 10 ms should be enough just to connect to and add a port to a set
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private static final int MAXIMUM_IPV4_TCP_IP_PORT_NUMBER = 65535;

    private final int optimalAmountOfThreads;

    public PortScanner() {
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        optimalAmountOfThreads = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        System.out.println("PortScanner is allocating " + optimalAmountOfThreads + " threads.");
    }

    @Override
    public Set<Integer> scan(String ip) {
        ConcurrentLinkedQueue<Integer> openPorts = new ConcurrentLinkedQueue<>();
        try (ExecutorService executorService = Executors.newFixedThreadPool(optimalAmountOfThreads)) {
            AtomicInteger port = new AtomicInteger(0);
            while (port.get() < MAXIMUM_IPV4_TCP_IP_PORT_NUMBER) {
                final int currentPort = port.getAndIncrement();
                executorService.submit(() -> {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                        socket.close();
                        openPorts.add(currentPort);
                    } catch (IOException e) {
                        // Do nothing, we can't connect, so we are not interested in the port
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        Set<Integer> openPortList = new HashSet<>();
        while (!openPorts.isEmpty()) {
            openPortList.add(openPorts.poll());
        }

        return openPortList;
    }

}