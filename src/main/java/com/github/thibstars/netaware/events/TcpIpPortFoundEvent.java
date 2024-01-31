package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.PortScanner;

/**
 * @author Thibault Helsmoortel
 */
public class TcpIpPortFoundEvent extends Event {

    private String ipAddress;
    private Integer tcpIpPort;

    public TcpIpPortFoundEvent(PortScanner scanner) {
        super(scanner);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getTcpIpPort() {
        return tcpIpPort;
    }

    public void setTcpIpPort(Integer tcpIpPort) {
        this.tcpIpPort = tcpIpPort;
    }
}
