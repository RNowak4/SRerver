package server.deadlock;

public class Server {
    private String host;
    private String port;

    Server(String host, String port) {
        this.host = host;
        this.port = port;
    }

    String getHost() {
        return host;
    }

    String getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (host != null ? !host.equals(server.host) : server.host != null) return false;
        return port != null ? port.equals(server.port) : server.port == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }
}
