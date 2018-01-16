package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
// TODO zapis do pliku po unlock
// TODO zwolnienie pamieci jak plik nie czytany przez nikogo
public class Server {

    public static void main(final String[] args){
        SpringApplication.run(Server.class, args);
    }
}
