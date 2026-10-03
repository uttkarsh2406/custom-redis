package Components;

import org.springframework.stereotype.Component;

@Component
public class CommandHandler {
    public String ping(String[] cmd) {
        return "+PONG\r\n";
    }

    public String echo(String[] cmd) {
        return "$" + cmd[1].length() + "\r\n" + cmd[1] + "\r\n";
    }

    public String set(String[] cmd) {
        return "+OK\r\n";
    }
}