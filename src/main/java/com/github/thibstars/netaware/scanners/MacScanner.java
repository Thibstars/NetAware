package com.github.thibstars.netaware.scanners;

import com.github.thibstars.netaware.events.MacFoundEvent;
import com.github.thibstars.netaware.events.ScanCompletedEvent;
import com.github.thibstars.netaware.events.ScanJobCompletedEvent;
import com.github.thibstars.netaware.events.ScanJobStartedEvent;
import com.github.thibstars.netaware.events.ScanStartedEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scanner searching for the MAC Address linked to an IP Address.
 *
 * @author Thibault Helsmoortel
 */
public class MacScanner implements Scanner<InetAddress> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacScanner.class);

    private final EventManager eventManager;

    public MacScanner(EventManager eventManager) {
        this.eventManager = eventManager != null ? eventManager : new EventManager();
    }

    @Override
    public void scan(InetAddress ip) {
        try {
            eventManager.dispatch(new ScanStartedEvent<>(this));

            eventManager.dispatch(new ScanJobStartedEvent<>(this));
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);
            if (networkInterface != null) {
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress != null) {
                    String macAddress = getMacAddressFromBytes(hardwareAddress);

                    eventManager.dispatch(new MacFoundEvent(this, ip, macAddress));
                } else {
                    LOGGER.warn("No hardware address found for {}", ip.getHostAddress());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        eventManager.dispatch(new ScanJobCompletedEvent<>(this));

        eventManager.dispatch(new ScanCompletedEvent<>(this));
    }

    private String getMacAddressFromBytes(byte[] hardwareAddress) {
        String[] hexadecimal = new String[hardwareAddress.length];
        for (int i = 0; i < hardwareAddress.length; i++) {
            hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
        }

        return String.join("-", hexadecimal);
    }
}
