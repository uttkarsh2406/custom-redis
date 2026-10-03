package Components;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import Components.RespSerializer;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import Components.CommandHandler;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import Components.Client;

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
        switch (cmd[0]) {
            case "PING":
                res = commandHandler.ping(cmd);
                break;
            case "ECHO":
                res = commandHandler.echo(cmd);
                break;
            case "SET":
                res = commandHandler.set(cmd);
                break;
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

    public void start() {
        System.out.println("TcpServer started");
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            int id =0;
            while (true) {
                ++id;
                System.out.println("Waiting for client connection...");
                clientSocket = serverSocket.accept();
                Socket finalClientSocket = clientSocket;

                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();

                Client client = new Client(finalClientSocket, inputStream, outputStream, id);
                CompletableFuture.runAsync(() -> {
                    try {
                        handleClient(client);
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
