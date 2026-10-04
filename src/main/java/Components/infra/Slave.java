package Components.infra;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;


public class Slave {
    public Client connection;
    public List<String> capabilities;

    public Slave(Client connection) {
        this.connection = connection;
        this.capabilities = new ArrayList<>();
    }


    
    
    
}
