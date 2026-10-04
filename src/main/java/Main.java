import Config.AppConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import Components.Server.MasterTcpServer;
import Components.Server.SlaveTcpServer;
import Components.Server.RedisConfig;

public class Main {


    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MasterTcpServer tcpServer = context.getBean(MasterTcpServer.class);
        SlaveTcpServer slaveTcpServer = context.getBean(SlaveTcpServer.class);
        RedisConfig redisConfig = context.getBean(RedisConfig.class);
        int port=6379;
        String role="master";
        for(int i=0; i<args.length; i++){
            switch(args[i]){
                case "--port":
                    port = Integer.parseInt(args[i+1]);
                    i++;
                    break;

                case "--replicaof":
                    role="slave";
                    String masterHost = args[i+1].split(" ")[0];
                    int masterPort = Integer.parseInt(args[i+1].split(" ")[1]);
                    redisConfig.setMasterHost(masterHost);
                    redisConfig.setMasterPort(masterPort);
                    break;
            }
        }

        redisConfig.setPort(port);
        redisConfig.setRole(role);

        if(role.equals("slave")){
            slaveTcpServer.start();
        }else{
            tcpServer.start();
        }

        context.close();
    }

}
