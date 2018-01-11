package server.clients;

import server.clients.api.Client;
import server.clients.api.Sid;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

@Component
class RegisteredClients {
    private Map<Sid, Client> sidClientMap = HashMap.empty();

    Option<Client> getBySid(final Sid sid) {
        return sidClientMap.get(sid);
    }

    Try<Void> removeBySid(final Sid sid) {
        return Try.run(() -> sidClientMap = sidClientMap.remove(sid));
    }

    boolean isRegistered(final Sid sid) {
        return sidClientMap.containsKey(sid);
    }

    void register(final Sid sid) {
        sidClientMap = sidClientMap.put(sid, new Client(sid));
    }

    Seq<Client> getAllClients() {
        return sidClientMap.values();
    }
}
