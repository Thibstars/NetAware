package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.Event;
import com.github.thibstars.netaware.events.EventListener;
import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.IpScannerEvent;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetAddress;
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
public class IpScanner implements Scanner<IpScannerInput> {

    public static final Logger LOGGER = LoggerFactory.getLogger(IpScanner.class);

    private static final int TIMEOUT = 500;
    private static final long SERVICE_TIME = 1L; // 1 ms should be enough just to add an IP to a set
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private final int optimalAmountOfThreads;

    private final Set<EventListener<Event>> eventListeners = new HashSet<>();

    public IpScanner() {
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        optimalAmountOfThreads = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        LOGGER.info("IpScanner is allocating {} threads.", optimalAmountOfThreads);
    }

    @Override
    public void scan(IpScannerInput ipScannerInput) {
        int actualThreadsToUse = Math.min(ipScannerInput.amountOfIpsToScan(), optimalAmountOfThreads);
        LOGGER.info("We need to scan {} ips, actually using {} threads.", ipScannerInput.amountOfIpsToScan(), actualThreadsToUse);

        try (ExecutorService executorService = Executors.newFixedThreadPool(actualThreadsToUse)) {
            final String networkId = ipScannerInput.firstIpInTheNetwork()
                    .substring(0, ipScannerInput.firstIpInTheNetwork().length() - 1);

            AtomicInteger ips = new AtomicInteger(0);
            while (ips.get() <= ipScannerInput.amountOfIpsToScan()) {
                String ip = networkId + ips.getAndIncrement();
                executorService.submit(() -> {
                    try {
                        InetAddress inAddress = InetAddress.getByName(ip);
                        if (inAddress.isReachable(TIMEOUT)) {
                            createAndFireIpAddressFoundEvent(ip);
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
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

    public void createAndFireIpAddressFoundEvent(String ipAddress) {
        IpAddressFoundEvent ipAddressFoundEvent = new IpAddressFoundEvent(this, ipAddress);
        eventListeners.forEach(ipAddressFoundEvent::addListener);
        ipAddressFoundEvent.fire();
    }

}