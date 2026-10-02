package Components;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class RespSerializer {
    public String serializeBulkString(String str) {
        return "+" + str + "\r\n";
    }

    public int getParts(char[] chars, int i, String[] result) {
        int j = 0;
        while (i < chars.length && j < result.length) {
            if (chars[i] == '$') {
                String len = "";
                i++;
                while (i < chars.length && chars[i] != '\r') {
                    len += chars[i];
                    i++;
                }
                i += 2;

                result[j] = new String(chars, i, Integer.parseInt(len));
                i += Integer.parseInt(len) + 2;
                j++;
            } else {
                i++;
            }
        }
        return i;
    }

    public List<String[]> deserializeBulkString(byte[] command) {
        String data = new String(command, StandardCharsets.UTF_8);

        char[] chars = data.toCharArray();

        List<String[]> result = new ArrayList<>();

        int i = 0;
        while (i < chars.length) {
            if (chars[i] == '*') {
                String arrayLength = "";
                i++;
                while (i < chars.length && chars[i] != '\r') {
                    arrayLength += chars[i];
                    i++;
                }
                i += 2;

                if (i < chars.length && chars[i] == '*') {
                    for (int t = 0; t < Integer.parseInt(arrayLength); t++) {
                        String nestedLen = "";

                        i++;
                        while (i < chars.length && Character.isDigit(chars[i])) {
                            nestedLen += chars[i];
                            i++;
                        }
                        i += 2;

                        String[] subArray = new String[Integer.parseInt(nestedLen)];
                        i = getParts(chars, i, subArray);
                        result.add(subArray);
                    }
                } else {
                    String[] subArray = new String[Integer.parseInt(arrayLength)];
                    i = getParts(chars, i, subArray);
                    result.add(subArray);
                }
            } else {
                i++;
            }
        }

        return result;
    }
}