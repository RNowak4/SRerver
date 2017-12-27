package rest;

import clients.api.IClientManager;
import org.springframework.stereotype.Service;

@Service
class ClientService {
    private IClientManager clientManager;

    ClientService(final IClientManager clientManager) {
        this.clientManager = clientManager;
    }
}
