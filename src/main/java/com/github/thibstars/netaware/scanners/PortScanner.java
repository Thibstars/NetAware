package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.Event;
import com.github.thibstars.netaware.events.EventListener;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class PortScanner implements Scanner<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortScanner.class);

    private static final int TIMEOUT = 200;
    private static final long SERVICE_TIME = 10L; // 10 ms should be enough just to connect to a port
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private static final int MAXIMUM_IPV4_TCP_IP_PORT_NUMBER = 65535;

    private final int optimalAmountOfThreads;

    private final Set<EventListener<Event>> eventListeners = new HashSet<>();

    public PortScanner() {
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        optimalAmountOfThreads = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        LOGGER.info("PortScanner is allocating {} threads.", optimalAmountOfThreads);
    }

    @Override
    public void scan(String ip) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(optimalAmountOfThreads)) {
            AtomicInteger port = new AtomicInteger(0);
            while (port.get() < MAXIMUM_IPV4_TCP_IP_PORT_NUMBER) {
                final int currentPort = port.getAndIncrement();
                executorService.submit(() -> {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, currentPort), TIMEOUT);
                        socket.close();
                        createAndFireTcpIpPortFoundEvent(this, ip, currentPort);
                    } catch (IOException e) {
                        // Do nothing, we can't connect, so we are not interested in the port
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void addEventListener(EventListener<Event> eventListener) {
        this.eventListeners.add(eventListener);
    }

    @Override
    public void removeEventListener(EventListener<Event> eventListener) {
        this.eventListeners.remove(eventListener);
    }

    public void createAndFireTcpIpPortFoundEvent(PortScanner portScanner, String ip, Integer tcpIpPort) {
        TcpIpPortFoundEvent tcpIpPortFoundEvent = new TcpIpPortFoundEvent(portScanner, ip, tcpIpPort);
        eventListeners.forEach(tcpIpPortFoundEvent::addListener);
        tcpIpPortFoundEvent.fire();
    }

}