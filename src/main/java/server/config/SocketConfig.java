package server.config;

import com.corundumstudio.socketio.SocketIOServer;
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
            client.joinRoom(data.getUserId());
            getLogger().info("Client: {} joined private room.", data.getUserId());
        });


        server.addEventListener("record_state_change", RecordChangeMessage.class, (client, data, ackSender) -> {
            if ("LOCK_RECORD".equals(data.getEventType())) {
                filesManager.lockRecord(data.getFile(), data.getRecord(), data.getUserId());
            } else {
                filesManager.unlockRecord(data.getFile(), data.getRecord(), data.getUserId());
            }
        });

        server.addEventListener("file_state_change", FileStateChangeMessage.class, (client, data, ackSender) -> {
            if ("OPEN_FILE".equals(data.getEventType())) {
                filesManager.addOpenedBy(data.getUserId(), data.getFile());
            } else {
                filesManager.removeOpenedBy(data.getUserId(), data.getFile());
            }
            ackSender.sendAckData();
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
