package clients.api;

public class ClientDescriptor {
    private String ip;

    public ClientDescriptor() {
    }

    public ClientDescriptor(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDescriptor that = (ClientDescriptor) o;

        return ip != null ? ip.equals(that.ip) : that.ip == null;
    }

    @Override
    public int hashCode() {
        return ip != null ? ip.hashCode() : 0;
    }
}
