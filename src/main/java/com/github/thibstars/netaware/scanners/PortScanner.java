package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class PortScanner implements StopableScanner<InetAddress> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortScanner.class);

    private static final HashMap<InetAddress, ExecutorService> EXECUTORS = new HashMap<>();

    private static final int TIMEOUT = 200;
    private static final long SERVICE_TIME = 10L; // 10 ms should be enough just to connect to a port
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private static final int MAXIMUM_IPV4_TCP_IP_PORT_NUMBER = 65535;

    private final int optimalAmountOfThreads;

    private final EventManager eventManager;

    public PortScanner(EventManager eventManager) {
        this.eventManager = eventManager != null ? eventManager : new EventManager();
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        optimalAmountOfThreads = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        LOGGER.info("PortScanner is allocating {} threads.", optimalAmountOfThreads);
    }

    @Override
    public void scan(InetAddress ip) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(optimalAmountOfThreads)) {
            EXECUTORS.put(ip, executorService);
            AtomicInteger port = new AtomicInteger(0);
            while (port.get() < MAXIMUM_IPV4_TCP_IP_PORT_NUMBER) {
                final int currentPort = port.getAndIncrement();
                executorService.submit(() -> {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                        socket.close();
                        TcpIpPortFoundEvent tcpIpPortFoundEvent = new TcpIpPortFoundEvent(this, ip, currentPort);
                        eventManager.dispatch(tcpIpPortFoundEvent);
                    } catch (IOException e) {
                        // Do nothing, we can't connect, so we are not interested in the port
                    }
                });
            }
            executorService.shutdown();
        }
    }

    @Override
    public void stop(InetAddress inetAddress) {
        LOGGER.info("Stopping scan for {}", inetAddress.getHostAddress());
        ExecutorService executorService = EXECUTORS.get(inetAddress);
        if (executorService != null) {
            executorService.shutdownNow();
            EXECUTORS.remove(inetAddress);
        }
    }

}