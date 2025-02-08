package alpine.crixie.cli.utiities;

import org.junit.Test;

import java.io.FileNotFoundException;

public class MavenExecutorTest {

    @Test
    public void buildPackage() throws FileNotFoundException {
        new MavenExecutor(new PackageLuaModifier()).buildPackage();
    }
}