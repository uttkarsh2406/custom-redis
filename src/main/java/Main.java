import Config.AppConfig;
import Components.TcpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {

    

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TcpServer tcpServer = context.getBean(TcpServer.class);
        tcpServer.start();

        
    }

}
