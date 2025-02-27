package alpine.crixie.cli.utiities;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalStorageTest {
    @Test
    void testGetData() {
        var ls = new LocalStorage();
        assertEquals(ls.getData().name(), "teste");
    }

    @Test
    void testUpdateData() {
        var ls = new LocalStorage();

        String token = "abc", profilePicture = "", name = "teste", userId = "id_123";

        ls.updateData(new LocalStorage.Data(token, profilePicture, name, userId));

        assertEquals("abc", ls.getData().token());
    }
}
