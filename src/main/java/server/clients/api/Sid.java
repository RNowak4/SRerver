package server.clients.api;

import java.util.Objects;

public class Sid {
    private String value;

    public Sid() {
    }

    public Sid(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sid sid = (Sid) o;
        return Objects.equals(value, sid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Sid{" +
                "value='" + value + '\'' +
                '}';
    }
}
