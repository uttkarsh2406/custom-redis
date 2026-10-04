package Components.infra;

import java.util.Set;

import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ConnectionPool {
    private Set<Client> clients;

    private Set<Slave> slaves;


    public ConnectionPool() {
        this.clients = new HashSet<>();
        this.slaves = new HashSet<>();
    }

    public Set<Client> getClients() {
        return clients;
    }

    public Set<Slave> getSlaves() {
        return slaves;
    }


    public void addClient(Client client) {
        if(clients!=null){
            clients.add(client);
        }
    }

    public void addSlave(Slave slave) {
        if(slaves!=null){
            slaves.add(slave);
        }
    }

    public boolean removeClient(Client client) {
        return clients.remove(client);
    }

    public boolean removeSlave(Slave slave) {
        return slaves.remove(slave);
    }

    public boolean removeSlave(Client client) {
        Slave SlaveToRemove=null;
        for(Slave slave : slaves){
            if(slave.connection.equals(client)){
                SlaveToRemove=slave;
                break;
            }
        }
        return removeSlave(SlaveToRemove);
    }
    
    
}


