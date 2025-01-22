package alpine.crixie.cli.utiities;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class PackageLuaModifierTest {

    @Test
    void load() throws FileNotFoundException {
        var lua = new PackageLuaModifier();
        System.out.println(lua.getData());
    }

    @Test
    void addDependency() {
    }

    @Test
    void removeDependency() {
    }

    @Test
    void getData() {
    }
}