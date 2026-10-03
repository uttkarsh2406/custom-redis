package Components.Server;

import org.springframework.stereotype.Component;

import Components.Service.CommandHandler;
import Components.Service.RespSerializer;
import infra.Client;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j 
@Component
public class TcpServer {

    @Autowired
    private RespSerializer respSerializer;

    @Autowired
    private CommandHandler commandHandler;


    public void handleClient(Client client) throws IOException {
        
        System.out.println("==============================================================================");


        while(client.socket.isConnected()) {
            byte[] buffer = new byte[client.socket.getReceiveBufferSize()];
            int bytesRead = client.inputStream.read(buffer);
            if(bytesRead > 0) {
                byte[] requestBytes = Arrays.copyOf(buffer, bytesRead);
                String request = new String(requestBytes);
                System.out.println("Received request: " + request.replace("\r", "\\r").replace("\n", "\\n"));

                System.out.println("Deserializing bulk string...");
                List<String[]> commands = respSerializer.deserializeBulkString(requestBytes);
                System.out.println("Deserialized bulk string...");

                for (String[] cmd : commands) {
                    System.out.println("Command: " + Arrays.toString(cmd));
                    handleCommand(cmd, client);
                }
            }


        }
        // Scanner sc = new Scanner(inputStream);
        // while (sc.hasNextLine()) {
        //     String nextLine = sc.nextLine();
        //     if (nextLine.contains("PING")) {
        //         outputStream.write("+PONG\r\n".getBytes());
        //     }

        //     if (nextLine.contains("ECHO")) {
        //         String respHeader = sc.nextLine();
        //         String respBody = sc.nextLine();
        //         outputStream.write(encodeRespString(respBody).getBytes());
        //     }
        // }
        System.out.println("==============================================================================");
    }

    public void handleCommand(String[] cmd, Client client) throws IOException {
        System.out.println("======================================Command========================================");
        String res = "";
        switch (cmd[0].toUpperCase()) {
            case "PING":
                res = commandHandler.ping(cmd);
                break;
            case "ECHO":
                res = commandHandler.echo(cmd);
                break;
            case "SET":
                res = commandHandler.set(cmd);
                break;
            case "GET":
                res = commandHandler.get(cmd);
        }
        System.out.println("Response: " + res.replace("\r", "\\r").replace("\n", "\\n"));

        if (res != null && !res.isEmpty()) {
            client.outputStream.write(res.getBytes());
            client.outputStream.flush();
        }
    }
    // public static String encodeRespString(String str) {
    //     String resp = "$";
    //     resp += str.length();
    //     resp += "\r\n";
    //     resp += str;
    //     resp += "\r\n";
    //     return resp;
    // }

    public void start(int port) {
        log.info("TcpServer started");
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        log.info("Port: " + port);
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            int id =0;
            while (true) {
                ++id;
                log.info("Waiting for client connection...");
                clientSocket = serverSocket.accept();
                Socket finalClientSocket = clientSocket;

                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();

                Client client = new Client(finalClientSocket, inputStream, outputStream, id);
                CompletableFuture.runAsync(() -> {
                    try {
                        handleClient(client);
                    } catch (IOException e) {
                        log.info("IOException: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            log.error("IOException " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error("IOException: " + e.getMessage());
            }
        }
    }
}
