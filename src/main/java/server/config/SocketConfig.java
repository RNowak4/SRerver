package server.config;

import com.corundumstudio.socketio.SocketIOServer;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import server.clients.api.messages.AuthorizeMessage;
import server.clients.api.messages.FileStateChangeMessage;
import server.clients.api.messages.RecordChangeMessage;
import server.files.api.IFilesManager;
import server.utils.HasLogger;

@Configuration
public class SocketConfig implements HasLogger {
    @Value("${socket.port}")
    private String socketPort;
    private final IFilesManager filesManager;
    private Map<String, String> clientsMap = HashMap.empty();

    public SocketConfig(IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @Bean(name = "webSocketServer")
    public SocketIOServer webSocketServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(Integer.valueOf(socketPort));

        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(client -> getLogger().info("Logging new session: {}.", client.getSessionId()));

        server.addEventListener("authorize", AuthorizeMessage.class, (client, data, ackSender) -> {
            getLogger().info("Authorizing client: {}.", data.getUserId());
            if (clientsMap.containsValue(data.getUserId())) {
                throw new RuntimeException("User already exists!");
            }
            client.joinRoom(data.getUserId());
            clientsMap = clientsMap.put(client.getSessionId().toString(), data.getUserId());
            getLogger().info("Client: {} joined private room.", data.getUserId());
        });


        server.addEventListener("record_state_change", RecordChangeMessage.class, (client, data, ackSender) -> {
            if ("LOCK_RECORD".equals(data.getEventType())) {
                getLogger().info("Received LOCK_RECORD message from client: {}", data.getUserId());
                filesManager.lockRecord(data.getFile(), data.getRecord(), data.getUserId());
            } else {
                getLogger().info("Received UNLOCK_RECORD message from client: {}", data.getUserId());
                filesManager.unlockRecord(data.getFile(), data.getRecord(), data.getUserId());
            }
        });

        server.addEventListener("file_state_change", FileStateChangeMessage.class, (client, data, ackSender) -> {
            if ("OPEN_FILE".equals(data.getEventType())) {
                getLogger().info("Received OPEN_FILE message from client: {}", data.getUserId());
                filesManager.addOpenedBy(data.getUserId(), data.getFile());
            } else {
                getLogger().info("Received CLOSE_FILE message from client: {}", data.getUserId());
                filesManager.removeOpenedBy(data.getUserId(), data.getFile());
            }
            ackSender.sendAckData();
        });

        server.addDisconnectListener(client -> {
            clientsMap.get(client.getSessionId().toString())
                    .onEmpty(() -> getLogger().warn("User: {} doesn't exist in system!", client.getSessionId().toString()))
                    .forEach(userName -> {
                        getLogger().info("Disconnected user: {}", userName);
                        filesManager.removeUserFromSystem(userName);
                        client.leaveRoom(userName);
                        clientsMap = clientsMap.remove(userName);
                    });
        });

        return server;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/files/**").allowedOrigins("http://localhost:3000");
            }
        };
    }
}
