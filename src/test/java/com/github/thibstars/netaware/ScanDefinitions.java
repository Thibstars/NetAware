package com.github.thibstars.netaware;

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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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
                portScanner.scan(ipAddress);
                macScanner.scan(ipAddress);
            }
        });

        ipScanner.scan(new IpScannerInput(ipToScan, amountOfIpsToScan));
    }

    @Then("wait until a full result is found")
    public void waitUntilAFullResultIsFound() throws InterruptedException {
        AtomicBoolean openPortFound = new AtomicBoolean(false);
        AtomicBoolean macAddressFound = new AtomicBoolean(false);
        EventHandler<IpAddressFoundEvent> ipAddressFoundEventEventHandler = event -> ((IpScanner) event.getSource()).stopScan(event.getIpScannerInput());
        EVENT_MANAGER.registerHandler(IpAddressFoundEvent.class, ipAddressFoundEventEventHandler);
        EventHandler<TcpIpPortFoundEvent> tcpIpPortFoundEventEventHandler = event -> {
            openPortFound.set(true);
            ((PortScanner) event.getSource()).stopScan(event.getIpAddress());
        };
        EVENT_MANAGER.registerHandler(TcpIpPortFoundEvent.class, tcpIpPortFoundEventEventHandler);
        EventHandler<MacFoundEvent> macAddressFoundEventEventHandler = event -> macAddressFound.set(true);
        EVENT_MANAGER.registerHandler(MacFoundEvent.class, macAddressFoundEventEventHandler);

        int checks = 0;
        do {
            LOGGER.info("Attempt {} to check if we've got a full result.", checks);
            Thread.sleep(500);
            checks++;
        } while (!openPortFound.get() && !macAddressFound.get());

        LOGGER.info("Check successful.");
        IP_ADDRESSES_WITH_OPEN_PORTS.forEach(
                (ipAddress, openPorts) -> LOGGER.info(
                        "{}: {}",
                        ipAddress.getHostAddress(),
                        openPorts.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                )
        );
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

        EVENT_MANAGER.removeHandler(IpAddressFoundEvent.class, ipAddressFoundEventEventHandler);
        EVENT_MANAGER.removeHandler(TcpIpPortFoundEvent.class, tcpIpPortFoundEventEventHandler);
        EVENT_MANAGER.removeHandler(MacFoundEvent.class, macAddressFoundEventEventHandler);
    }
}
