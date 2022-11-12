package ago.ago_be;

import java.util.Base64;
import java.util.UUID;

public class APIKeyTest {

    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString();
        System.out.println("uuid = " + uuid);
        byte[] bytes = uuid.getBytes();
        byte[] encode = Base64.getEncoder().encode(bytes);
        System.out.println("new String(encode) = " + new String(encode));
    }
}
