package alpine.crixie.cli.utiities;

import org.junit.jupiter.api.Test;

class Base64DecoderTest {

    @Test
    void decode() {
        System.out.println(new Base64Decoder().decode(new LocalStorage().getData().name()));
    }
}