// package Components.Service;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.BeforeAll;
// import org.springframework.beans.factory.annotation.Autowired;

// import lombok.extern.slf4j.Slf4j;

// import org.junit.jupiter.api.Test;
// import Config.AppConfig;
// import Components.Server.RedisConfig;
// import Components.Server.MasterTcpServer;

// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// import java.util.concurrent.CompletableFuture;
// import java.lang.Thread;

// @Slf4j 
// @SpringBootTest(classes = AppConfig.class)
// class CommandHandlerTest {

//     @Autowired
//     private CommandHandler commandHandler;

//     @Autowired
//     private RespSerializer respSerializer;

//     @BeforeAll 
//     public static void setUp(@Autowired AnnotationConfigApplicationContext context) throws InterruptedException{
//         RedisConfig redisConfig = context.getBean(RedisConfig.class);
//         redisConfig.setRole("master");
//         redisConfig.setPort(6379);
//         MasterTcpServer tcpServer = context.getBean(MasterTcpServer.class);
//         CompletableFuture.runAsync(() -> {
//                 tcpServer.start(6379);
//         });
//         Thread.sleep(1000);
//     }


//     @Test
//     public void testInfo() {
//         String[] cmd = {"INFO", "replication"};
//         String result = commandHandler.info(cmd);
//         log.info("result: " + result);
//         assertEquals(respSerializer.serializeBulkString("role:master"), result);
//     }
// }