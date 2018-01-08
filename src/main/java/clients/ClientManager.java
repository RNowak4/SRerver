package clients;

import clients.api.Client;
import clients.api.IClientManager;
import clients.api.Sid;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
class ClientManager implements IClientManager {
    private RegisteredClients registeredClients;

    ClientManager(final RegisteredClients registeredClients) {
        this.registeredClients = registeredClients;
    }

    @Override
    public Try<Void> registerClient(final Sid sid) {
        if(registeredClients.isRegistered(sid)) {
            return Try.failure(new RuntimeException("Already registered client!"));
        } else {
            return Try.run(() -> registeredClients.register(sid));
        }
    }

    @Override
    public Try<Void> setLoginForClient(final Sid sid, final String login) {
        return registeredClients.getBySid(sid)
                .peek(client -> client.setLogin(login))
                .toTry()
                .map(v -> null);
    }

    @Override
    public Try<Void> removeClient(Sid sid) {
        return registeredClients.removeBySid(sid);
    }

    @Override
    public Try<Void> emit(List<Client> clientList, String eventName, Option<String> eventBody) {
        // TODO
        return null;
    }

    @Override
    public Option<Client> getClientByName(String name) {
        return registeredClients.getAllClients().toStream()
                .filter(client -> client.getLogin().equals(name))
                .headOption();
    }

    @Override
    public List<Client> getAll() {
        return registeredClients.getAllClients().toList();
    }
}