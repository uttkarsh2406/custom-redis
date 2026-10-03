package Components.Repository;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Components.Service.RespSerializer;

@Slf4j
@Component
public class Store {

    public ConcurrentHashMap<String, Value> store;

    @Autowired
    private RespSerializer respSerializer;

    public Store() {
        this.store = new ConcurrentHashMap<>();
    }

    public Set<String> getKeys() {
        return this.store.keySet();
    }

    public String set(String key, String val) {
        try {
            this.store.put(key, new Value(val, LocalDateTime.now(), LocalDateTime.MAX));
            return "+OK\r\n";
        } catch (Exception e) {
            log.error("Error setting value: {}", e.getMessage());
            return "$-1\r\n";
        }
    }

    public String setWithExpiry(String key, String val, LocalDateTime exDateTime) {
        try {
            this.store.put(key, new Value(val, LocalDateTime.now(), exDateTime));
            return "+OK\r\n";
        } catch (Exception e) {
            log.error("Error setting value: {}", e.getMessage());
            return "$-1\r\n";
        }
    }

    public String get(String key) {
        try {
            Value value = this.store.get(key);
            if (value == null) {
                return "$-1\r\n";
            }
            if (value.expiresAt.isBefore(LocalDateTime.now())) {
                this.store.remove(key);
                return "$-1\r\n";
            }
            return respSerializer.serializeBulkString(value.val);
        } catch (Exception e) {
            log.error("Error getting value: {}", e.getMessage());
            return "$-1\r\n";
        }
    }
}
