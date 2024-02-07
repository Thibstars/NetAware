package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scanner searching for reachable IPs in the network.
 *
 * @author Thibault Helsmoortel
 */
public class IpScanner implements StopableScanner<IpScannerInput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpScanner.class);

    private static final HashMap<IpScannerInput, ExecutorService> EXECUTORS = new HashMap<>();

    private static final int TIMEOUT = 500;
    private static final long SERVICE_TIME = 1L; // 1 ms should be enough just to add an IP to a set
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private final int optimalAmountOfThreads;

    private final EventManager eventManager;

    public IpScanner(EventManager eventManager) {
        this.eventManager = eventManager != null ? eventManager : new EventManager();
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
                        InetAddress ipAddress = InetAddress.getByName(ip);
                        if (ipAddress.isReachable(TIMEOUT)) {
                            IpAddressFoundEvent ipAddressFoundEvent = new IpAddressFoundEvent(this, ipScannerInput, ipAddress);
                            eventManager.dispatch(ipAddressFoundEvent);
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                });
            }
            executorService.shutdown();
        }
    }

    @Override
    public void stop(IpScannerInput input) {
        LOGGER.info("Stopping scan for {}", input);
        ExecutorService executorService = EXECUTORS.get(input);
        if (executorService != null) {
            executorService.shutdownNow();
            EXECUTORS.remove(input);
        }
    }

}