package AgrumentsParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AgrumentsParser.models.Connection;

public class ArgumentsObject {
    private Integer tcpPort;
    private Integer key = null;
    private Integer value = null;
    private List<Connection> connections = new ArrayList<Connection>();

    public ArgumentsObject() {}

    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setRecord(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public Integer getTcpPort() {
        return tcpPort;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public String toString() {
        return "ArgumentsObject [tcpPort=" + tcpPort + ", record=[" + key + ":" + value + "], connections=" + connections.toString() + "]";
    }

}
