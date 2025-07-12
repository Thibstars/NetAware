package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.ScanCompletedEvent;
import com.github.thibstars.netaware.events.ScanJobCompletedEvent;
import com.github.thibstars.netaware.events.ScanJobStartedEvent;
import com.github.thibstars.netaware.events.ScanStartedEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.Validate;
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
    private final int amountOfThreadsToUse;

    private final EventManager eventManager;

    public IpScanner(EventManager eventManager) {
        this.eventManager = eventManager != null ? eventManager : new EventManager();
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        this.amountOfThreadsToUse = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        LOGGER.info("IpScanner is allocating {} threads.", amountOfThreadsToUse);
    }

    public IpScanner(EventManager eventManager, int amountOfThreads) {
        Validate.isTrue(amountOfThreads > 0, "The amount of threads must be greater than 0.");
        this.eventManager = eventManager != null ? eventManager : new EventManager();
        this.amountOfThreadsToUse = amountOfThreads;
        LOGGER.info("IpScanner is allocating {} threads.", amountOfThreadsToUse);
    }

    @Override
    public void scan(IpScannerInput ipScannerInput) {
        int actualThreadsToUse = Math.min(ipScannerInput.amountOfIpsToScan(), amountOfThreadsToUse);
        LOGGER.info("We need to scan {} ips, actually using {} threads.", ipScannerInput.amountOfIpsToScan(), actualThreadsToUse);

        eventManager.dispatch(new ScanStartedEvent<>(this));

        try (ExecutorService executorService = Executors.newFixedThreadPool(actualThreadsToUse)) {
            final String networkId = ipScannerInput.firstIpInTheNetwork()
                    .substring(0, ipScannerInput.firstIpInTheNetwork().length() - 1);

            AtomicInteger ips = new AtomicInteger(0);
            while (ips.get() <= ipScannerInput.amountOfIpsToScan()) {
                String ip = networkId + ips.getAndIncrement();
                executorService.submit(() -> {
                    eventManager.dispatch(new ScanJobStartedEvent<>(this));

                    try {
                        InetAddress ipAddress = InetAddress.getByName(ip);
                        if (ipAddress.isReachable(TIMEOUT)) {
                            IpAddressFoundEvent ipAddressFoundEvent = new IpAddressFoundEvent(this, ipScannerInput, ipAddress);
                            eventManager.dispatch(ipAddressFoundEvent);
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    eventManager.dispatch(new ScanJobCompletedEvent<>(this));
                });
            }
            executorService.submit(() -> eventManager.dispatch(new ScanCompletedEvent<>(this)));
            executorService.shutdown();
        }
    }

    @Override
    public void stop(IpScannerInput input) {
        LOGGER.info("Stopping scan for {}", input);
        ExecutorService executorService = EXECUTORS.get(input);
        if (executorService != null) {
            executorService.shutdownNow();
            eventManager.dispatch(new ScanCompletedEvent<>(this));
            EXECUTORS.remove(input);
        }
    }

}