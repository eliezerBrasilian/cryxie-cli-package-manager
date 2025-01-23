package alpine.crixie.cli.utiities;

import java.util.Base64;

public class Base64Decoder {
    public String decode(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        return new String(decodedBytes);
    }
}
