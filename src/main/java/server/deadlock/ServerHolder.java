package server.deadlock;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Component
class ServerHolder {
    private Set<Server> servers = new HashSet<>();

    @PostConstruct
    void init() throws IOException {
        final String filePath = ServerHolder.class.getResource("/servers/servers.txt").getFile();
        final File file = new File(filePath);

        Files.lines(file.toPath())
                .forEach(line -> {
                    final String[] splited = line.split(":");
                    servers.add(new Server(splited[0], splited[1]));
                });
    }

    Set<Server> getServers() {
        return servers;
    }
}
