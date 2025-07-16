# NetAware [![](https://jitpack.io/v/Thibstars/netaware.svg)](https://jitpack.io/#Thibstars/netaware)
Java library able to scan networks.

Using an event-based approach, you can discover elements on your local network including:
- IP addresses
- MAC addresses
- open TCP/IP ports

<img alt="The NetAware logo." height="125" src="NetAware.png" title="NetAware logo" width="125"/>

## Installation
### Maven

Include the dependency in the `dependencies` tag in your pom file (create a property with the desired version `netaware.version` in the `properties` tag).

````xml
<dependency>
    <groupId>com.github.thibstars</groupId>
    <artifactId>netaware</artifactId>
    <version>${netaware.version}</version>
</dependency>
````

Make sure to add the repository to the `repositories` tag.
````xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
````

## Usage
### Using an `EventManager`
The library will dispatch different events throughout its lifecycle.
The `EventManager` class can be used to handle these events.

First instantiate a new instance:

`EventManager eventManager = new EventManager();`

#### Register event handlers
Now, we only need to register handlers to perform tasks when events occur:

````java
eventManager.registerHandler(
        IpAddressFoundEvent.class,
        event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                String hostAddress = ipAddress.getHostAddress();
                System.out.println("Found IP address: " + hostAddress);
            }
        });
````

The library will dispatch multiple different event types.
These include main events:
- `IpAddressFoundEvent`
- `TcpIpPortFoundEvent`
- `MacFoundEvent`

There are a few more events you can handle.
Find them all in the `com.github.thibstars.netaware.events` package.

### Scan
Now it is time to discover devices on the local network!
Simply instantiate a scanner and provide it with the required input, the library will do the rest.

````java
IpScanner ipScanner = new IpScanner(eventManager);
String firstIpInTheNetwork = "192.168.1.0";
int amountOfIpsToScan = 255;
ipScanner.scan(new IpScannerInput(firstIpInTheNetwork, amountOfIpsToScan));
````

The library comes with different scanners.
These include main scanners:
- `IpScanner`
- `PortScanner`
- `MacScanner`

Find them all in the `com.github.thibstars.netaware.scanners` package.

## Demo
Looking for a quick demo? Play around with this sample code to discover IP addresses along with open TCP/IP ports and MAC addresses on your local network.

If you prefer to look at a GUI demo; head over to [NetAwareDesktop](https://github.com/Thibstars/NetAwareDesktop).

````java
public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        IpScanner ipScanner = new IpScanner(eventManager);
        PortScanner portScanner = new PortScanner(eventManager);
        MacScanner macScanner = new MacScanner(eventManager);

        HashMap<InetAddress, Set<Integer>> ipAddressesWithOpenPorts = new HashMap<>();
        eventManager.registerHandler(TcpIpPortFoundEvent.class, event -> {
            if (event instanceof TcpIpPortFoundEvent tcpIpPortFoundEvent) {
                InetAddress ipAddress = tcpIpPortFoundEvent.getIpAddress();
                Integer tcpIpPort = tcpIpPortFoundEvent.getTcpIpPort();
                LOGGER.info("Found open TCP/IP port '{}' on IP address '{}'.", tcpIpPort, ipAddress.getHostAddress());
                ipAddressesWithOpenPorts.get(ipAddress).add(tcpIpPort);
            }
        });
        HashMap<InetAddress, String> ipAddressesWithMacAddress = new HashMap<>();
        eventManager.registerHandler(MacFoundEvent.class, event -> {
            if (event instanceof MacFoundEvent macFoundEvent) {
                InetAddress ipAddress = macFoundEvent.getIpAddress();
                String macAddress = macFoundEvent.getMacAddress();
                LOGGER.info("Found MAC address '{}' on IP address '{}'.", macAddress, ipAddress.getHostAddress());
                ipAddressesWithMacAddress.replace(ipAddress, macAddress);
            }
        });
        eventManager.registerHandler(IpAddressFoundEvent.class, event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                LOGGER.info("Found IP address '{}', will scan for open TCP/IP ports.", ipAddress.getHostAddress());
                ipAddressesWithOpenPorts.put(ipAddress, new HashSet<>());
                ipAddressesWithMacAddress.put(ipAddress, null);
                portScanner.scan(ipAddress);
                macScanner.scan(ipAddress);
            }
        });

        String firstIpInTheNetwork = "192.168.1.0";
        int amountOfIpsToScan = 255;
        LOGGER.info("Scanning {} IPs...", amountOfIpsToScan);
        LOGGER.info("\n=============================================================\n");
        ipScanner.scan(new IpScannerInput(firstIpInTheNetwork, amountOfIpsToScan));
        LOGGER.info("\n=============================================================\n");
        LOGGER.info("SUMMARY");
        LOGGER.info("(<IP>: <Ports>)");
        ipAddressesWithOpenPorts.forEach(
                (ipAddress, openPorts) -> LOGGER.info(
                        "{}: {}",
                        ipAddress.getHostAddress(),
                        openPorts.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "))
                )
        );
        LOGGER.info("(<IP>: <MAC>)");
        ipAddressesWithMacAddress.forEach(
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
````

---
Apache 2.0 License