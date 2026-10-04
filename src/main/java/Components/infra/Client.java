package Components.infra;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class Client {
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public int id;


    public Client(Socket socket,InputStream inputStream,OutputStream outputStream,int id) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.id = id;
    }
    
    
}