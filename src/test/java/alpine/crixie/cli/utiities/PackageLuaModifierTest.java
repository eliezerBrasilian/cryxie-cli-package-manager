package alpine.crixie.cli.utiities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageLuaModifierTest {
    PackageLuaModifier packageLuaModifier;

    @Before
    public void setUp() throws Exception {
        packageLuaModifier = new PackageLuaModifier();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void load() {
    }

    @Test
    public void addDependency() {
    }

    @Test
    public void removeDependency() {
    }

    @Test
    public void getLuaFileContent() {
        String luaFileContent = packageLuaModifier.getLuaFileContent();
        System.out.println(luaFileContent);
    }

    @Test
    public void getData() {

    }
}