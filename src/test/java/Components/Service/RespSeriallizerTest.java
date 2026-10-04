package Components.Service;

import org.junit.jupiter.api.Test;

import Components.Service.RespSerializer;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RespSeriallizerTest {
    private final RespSerializer respSerializer = new RespSerializer();

    @Test
    void testDeserializePing() {
        String command = "*1\r\n$4\r\nPING\r\n";
        List<String[]> result = respSerializer.deserializeBulkString(command.getBytes());

        for (String[] part : result) {
            for (String p : part) {
                System.out.println(p);
            }
        }

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().length);
        assertEquals("PING", result.getFirst()[0]);
    }


    @Test 
    void testMultipleCommands(){
        String multipleCommands = "*3\r\n$3\r\nSET\r\n$3\r\nfoo\r\n$3\r\nbar\r\n";
        List<String[]> result = respSerializer.deserializeBulkString(multipleCommands.getBytes());

        for(String[] part : result){
            for(String p : part){
                System.out.println(p);
            }
        }
        
        assertEquals(1, result.size());
        assertEquals(3, result.getFirst().length);
        assertEquals("SET", result.getFirst()[0]);
        assertEquals("foo", result.getFirst()[1]);
        assertEquals("bar", result.getFirst()[2]);
    }
}