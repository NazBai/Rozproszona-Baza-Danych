package AgrumentsParser.models;

public class Connection {
    private static final String className = Connection.class.getName();

    private String host;
    private Integer port;

    public Connection(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String toString() {
        return String.format("%s<Host: %s, Port: %d>", className, host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
