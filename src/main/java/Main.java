import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;


public class Main {

    public static void handleClient(Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();


        Scanner sc = new Scanner(inputStream);
        System.out.println("==============================================================================");
        while (sc.hasNextLine()){ 
            String nextLine = sc.nextLine();
            if (nextLine.contains("PING")){
                outputStream.write("+PONG\r\n".getBytes());
            }
        }
        System.out.println("==============================================================================");
    }
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true){
                System.out.println("Waiting for client connection...");
                clientSocket = serverSocket.accept();
                Socket finalClientSocket = clientSocket;
                CompletableFuture.runAsync(() -> {
                    try{
                        handleClient(finalClientSocket);
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


    public static String encodeRespString(String str) {
        String resp="$";
        resp += str.length();
        rest+="\r\n";
        resp += str;
        resp += "\r\n";
        return resp;
    }
}