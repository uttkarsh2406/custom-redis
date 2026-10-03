package Components.Repository;


import Config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import Components.Service.RespSerializer;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.lang.Thread;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@SpringBootTest(classes =AppConfig.class)
class StoreTest {

    @Autowired 
    private Store store;

    @Autowired 
    private RespSerializer respSerializer;

    @BeforeEach
    private void setUp() {
        store.store.clear();
    }

    @Test
    void testSetAndGet() {
        String key = "test";
        String value = "test";
        String response = store.set(key, value);
        assertEquals("+OK\r\n", response);
        String result = store.get(key);
        assertEquals(respSerializer.serializeBulkString(value), result);

        response = store.get("key_not_found");
        assertEquals("$-1\r\n", response);
    }


    @Test 
    void testSetAndGetWithExpiry() {
        try{
            String key = "test";
            String value = "test";
            LocalDateTime expiry = LocalDateTime.now().plusSeconds(5);
            String response = store.setWithExpiry(key, value, expiry);
            assertEquals("+OK\r\n", response);
            String result = store.get(key);
            assertEquals(respSerializer.serializeBulkString(value), result);
            Thread.sleep(5000);
            response = store.get(key);
            assertEquals("$-1\r\n", response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSetAndGetWithExpiryReset() {
        try{
            String key = "test2";
            String value = "test2";
            LocalDateTime expiry = LocalDateTime.now().plusSeconds(3);
            String response = store.setWithExpiry(key, value, expiry);
            assertEquals("+OK\r\n", response);
            String result = store.get(key);
            assertEquals(respSerializer.serializeBulkString(value), result);
            expiry = LocalDateTime.now().plusSeconds(4);
            response = store.setWithExpiry(key, value, expiry);
            assertEquals("+OK\r\n", response);
            result = store.get(key);
            assertEquals(respSerializer.serializeBulkString(value), result);
            Thread.sleep(4000);
            response = store.get(key);
            assertEquals("$-1\r\n", response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test 
    void testConcurrentSetA() throws InterruptedException {

        List<CompletableFuture<Void>> l = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                for(int j = 0; j < 1000; j++) {
                    String key = "test" + j + "," + finalI;
                    String value = "test" + j + "," + finalI;
                    store.set(key, value);
                }
            });
            l.add(future);

        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(l.toArray(new CompletableFuture[l.size()]));
        allFutures.join();

        assertEquals(10000, store.getKeys().size());
    }

}