package alpine.crixie.cli.utiities;

import org.junit.Test;

public class GitIgnoreGeneratorTest {
    @Test
    public void generateGitIgnore() {
        new GitIgnoreGenerator().generate();
    }
}