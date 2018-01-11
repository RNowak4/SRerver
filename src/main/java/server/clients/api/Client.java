package server.clients.api;

import java.util.Objects;

public class Client {
    private Sid sid;
    private String login;

    public Client() {
    }

    public Client(Sid sid) {
        this.sid = sid;
    }

    public Client(Sid sid, String login) {
        this.sid = sid;
        this.login = login;
    }

    public Sid getSid() {
        return sid;
    }

    public void setSid(Sid sid) {
        this.sid = sid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(sid, client.sid) &&
                Objects.equals(login, client.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, login);
    }

    @Override
    public String toString() {
        return "Client{" +
                "sid=" + sid +
                ", login='" + login + '\'' +
                '}';
    }
}
