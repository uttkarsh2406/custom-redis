package Components.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Components.Repository.Store;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j 
@Component
public class CommandHandler {
    
    @Autowired 
    private RespSerializer respSerializer;


    @Autowired 
    private Store store;

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
}