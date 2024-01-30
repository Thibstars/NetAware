package com.github.thibstars.netaware;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thibault Helsmoortel
 */
public class IpScanner implements Scanner<IpScannerInput, String> {

    private static final int TIMEOUT = 500;
    private static final long SERVICE_TIME = 1L; // 1 ms should be enough just to add an IP to a set
    public static final double TARGET_CPU_UTILISATION = 0.9;
    private final int optimalAmountOfThreads;

    public IpScanner() {
        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        optimalAmountOfThreads = optimalThreadPoolSizeCalculator.get(TARGET_CPU_UTILISATION, TIMEOUT, SERVICE_TIME);
        System.out.println("IpScanner is allocating " + optimalAmountOfThreads + " threads.");
    }

    @Override
    public ConcurrentSkipListSet<String> scan(IpScannerInput ipScannerInput) {
        ConcurrentSkipListSet<String> ipsSet;

        int actualThreadsToUse = Math.min(ipScannerInput.amountOfIpsToScan(), optimalAmountOfThreads);
        System.out.println("We need to scan " + ipScannerInput.amountOfIpsToScan() + " ips, actually using " + actualThreadsToUse + " threads.");

        try (ExecutorService executorService = Executors.newFixedThreadPool(actualThreadsToUse)) {
            final String networkId = ipScannerInput.firstIpInTheNetwork().substring(0, ipScannerInput.firstIpInTheNetwork().length() - 1);
            ipsSet = new ConcurrentSkipListSet<>();

            AtomicInteger ips = new AtomicInteger(0);
            while (ips.get() <= ipScannerInput.amountOfIpsToScan()) {
                String ip = networkId + ips.getAndIncrement();
                executorService.submit(() -> {
                    try {
                        InetAddress inAddress = InetAddress.getByName(ip);
                        if (inAddress.isReachable(TIMEOUT)) {
                            ipsSet.add(ip);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        return ipsSet;
    }
}