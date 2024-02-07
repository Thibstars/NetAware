package com.github.thibstars.netaware.cucumber;

import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.IpScannerEvent;
import com.github.thibstars.netaware.events.MacFoundEvent;
import com.github.thibstars.netaware.events.MacScannerEvent;
import com.github.thibstars.netaware.events.PortScannerEvent;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.events.core.EventHandler;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.MacScanner;
import com.github.thibstars.netaware.scanners.PortScanner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class ScanDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanDefinitions.class);

    private static final EventManager EVENT_MANAGER = new EventManager();

    private static final HashMap<InetAddress, Set<Integer>> IP_ADDRESSES_WITH_OPEN_PORTS = new HashMap<>();
    private static final HashMap<InetAddress, String> IP_ADDRESSES_WITH_MAC_ADDRESS = new HashMap<>();

    @Given("an event manager without listeners")
    public void anEventManagerWithoutListeners() {
        EVENT_MANAGER.removeAllHandlers(IpScannerEvent.class);
        EVENT_MANAGER.removeAllHandlers(PortScannerEvent.class);
        EVENT_MANAGER.removeAllHandlers(MacScannerEvent.class);
    }

    @Given("scans will stop after one result is found")
    public void stopScansAfterOneResultIsFound() {
        EventHandler<IpAddressFoundEvent> ipAddressFoundEventEventHandler = event -> ((IpScanner) event.getSource()).stop(event.getIpScannerInput());
        EVENT_MANAGER.registerHandler(IpAddressFoundEvent.class, ipAddressFoundEventEventHandler);
        EventHandler<TcpIpPortFoundEvent> tcpIpPortFoundEventEventHandler = event -> ((PortScanner) event.getSource()).stop(event.getIpAddress());
        EVENT_MANAGER.registerHandler(TcpIpPortFoundEvent.class, tcpIpPortFoundEventEventHandler);
    }

    @When("starting a full scan for ip {string}")
    public void startingAFullScanForIp(String ipToScan) {
        int amountOfIpsToScan = 254;

        IP_ADDRESSES_WITH_OPEN_PORTS.clear();
        IP_ADDRESSES_WITH_MAC_ADDRESS.clear();

        IpScanner ipScanner = new IpScanner(EVENT_MANAGER);
        PortScanner portScanner = new PortScanner(EVENT_MANAGER);
        MacScanner macScanner = new MacScanner(EVENT_MANAGER);

        EVENT_MANAGER.registerHandler(TcpIpPortFoundEvent.class, event -> {
            if (event instanceof TcpIpPortFoundEvent tcpIpPortFoundEvent) {
                InetAddress ipAddress = tcpIpPortFoundEvent.getIpAddress();
                Integer tcpIpPort = tcpIpPortFoundEvent.getTcpIpPort();
                LOGGER.info("Found open TCP/IP port '{}' on IP address '{}'.", tcpIpPort, ipAddress.getHostAddress());
                IP_ADDRESSES_WITH_OPEN_PORTS.get(ipAddress).add(tcpIpPort);
            }
        });

        EVENT_MANAGER.registerHandler(MacFoundEvent.class, event -> {
            if (event instanceof MacFoundEvent macFoundEvent) {
                InetAddress ipAddress = macFoundEvent.getIpAddress();
                String macAddress = macFoundEvent.getMacAddress();
                LOGGER.info("Found MAC address '{}' on IP address '{}'.", macAddress, ipAddress.getHostAddress());
                IP_ADDRESSES_WITH_MAC_ADDRESS.replace(ipAddress, macAddress);
            }
        });
        EVENT_MANAGER.registerHandler(IpAddressFoundEvent.class, event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                LOGGER.info("Found IP address '{}', will scan for open ports and MAC.", ipAddress.getHostAddress());
                IP_ADDRESSES_WITH_OPEN_PORTS.put(ipAddress, new HashSet<>());
                IP_ADDRESSES_WITH_MAC_ADDRESS.put(ipAddress, null);
                macScanner.scan(ipAddress);
                portScanner.scan(ipAddress);
            }
        });

        ipScanner.scan(new IpScannerInput(ipToScan, amountOfIpsToScan));
    }

    @Then("wait for a MAC address to be found")
    public void waitForAMACAddressToBeFound() throws InterruptedException {
        int checks = 0;
        while (!hasFoundAMacAddress() && checks < 20) {
            LOGGER.info("Attempt {} of checking MAC address.", checks + 1);
            Thread.sleep(500);
            checks++;
        }

        if (!hasFoundAMacAddress()) {
            Assertions.fail("No MAC address found.");
        }
    }

    private static boolean hasFoundAMacAddress() {
        return !IP_ADDRESSES_WITH_MAC_ADDRESS.values()
                .stream()
                .filter(Objects::nonNull)
                .toList()
                .isEmpty();
    }

    @Then("print scan results")
    public void printScanResults() {
        LOGGER.info("Results:");
        LOGGER.info("IP: Ports");
        IP_ADDRESSES_WITH_OPEN_PORTS.forEach(
                (ipAddress, openPorts) -> LOGGER.info(
                        "{}: {}",
                        ipAddress.getHostAddress(),
                        openPorts.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                )
        );
        LOGGER.info("IP: MAC");
        IP_ADDRESSES_WITH_MAC_ADDRESS.forEach(
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
