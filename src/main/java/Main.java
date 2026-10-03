import Config.AppConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import Components.Server.TcpServer;



public class Main {

    

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TcpServer tcpServer = context.getBean(TcpServer.class);
        int port=6379;
        for(int i=0; i<args.length; i++){
            if(args[i].equals("--port")){
                port = Integer.parseInt(args[i+1]);
                break;
            }
        }
        tcpServer.start(port);

        context.close();
    }

}
