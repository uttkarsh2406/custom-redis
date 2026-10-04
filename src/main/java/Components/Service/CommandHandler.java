package Components.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Components.Repository.Store;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import Components.Server.RedisConfig;
import Components.infra.ConnectionPool;
import Components.infra.Client;
import Components.infra.Slave;
@Slf4j 
@Component
public class CommandHandler {
    
    @Autowired 
    private RespSerializer respSerializer;


    @Autowired 
    private Store store;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private ConnectionPool connectionPool;

    public String ping(String[] cmd) {
        return "+PONG\r\n";
    }

    public String echo(String[] cmd) {
        return respSerializer.serializeBulkString(cmd[1]);
    }

    public String set(String[] cmd) {
        int pxFlag= Arrays.stream(cmd).toList().indexOf("PX");
        log.info("pxFlag: " + pxFlag);
        if(pxFlag != -1){
            String pxValue=cmd[pxFlag+1];
            int pxValueInt=Integer.parseInt(pxValue);
            log.info("pxValueInt: " + pxValueInt);
            log.info("now: " + LocalDateTime.now());
            log.info("exDateTime: " + LocalDateTime.now().plus(pxValueInt, ChronoUnit.MILLIS));
            return store.setWithExpiry(cmd[1], cmd[2], LocalDateTime.now().plus(pxValueInt, ChronoUnit.MILLIS));
        }
        return store.set(cmd[1], cmd[2]);
    }

    public String get(String[] cmd) {
        return store.get(cmd[1]);
    }

    public String info(String[] cmd) {
        int replicationFlag = Arrays.stream(cmd).toList().indexOf("replication");
        if(replicationFlag != -1){
            String role= "role:" + redisConfig.getRole();
            String masterReplId= "master_repl_id:" + redisConfig.getMasterReplId();
            String masterReplOffset= "master_repl_offset:" + redisConfig.getMasterReplOffset();

            String info = role + "\r\n" + masterReplId + "\r\n" + masterReplOffset + "\r\n";

            return respSerializer.serializeBulkString(info);
        }
        return "$-1\r\n";
    }

    public String replconf(String[] cmd, Client client) {
        switch(cmd[1].toUpperCase()){
            case "LISTENING-PORT":
                connectionPool.removeSlave(client);
                Slave slave=new Slave(client);
                connectionPool.addSlave(slave);
                return "+OK\r\n";
            case "CAPA":
                Slave sl=null;
                for(Slave ss : connectionPool.getSlaves()){
                    if(ss.connection.equals(client)){
                        sl=ss;
                        break;
                    }
                }
                for(int i=0;i<cmd.length;i++){
                    if(cmd[i].toUpperCase().equals("CAPA")){
                        sl.capabilities.add(cmd[i+1]);
                        break;
                    }
                }
                return "+OK\r\n";
        }
        return "";
    }
}