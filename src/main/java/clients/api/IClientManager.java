package clients.api;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface IClientManager {

    Try<Void> registerClient(final Sid sid);

    Try<Void> setLoginForClient(final Sid sid, final String login);

    Try<Void> removeClient(final Sid sid);

    Try<Void> emit(final List<Client> clientList, final String eventName, final Option<String> eventBody);

    Option<Client> getClientByName(final String name);

    List<Client> getAll();
}