package alpine.crixie.cli.utiities;

import org.junit.Test;

public class PomXmlModifierTest {


    @Test
    public void add() {

        var packageName = "guga-reader-5";
        var version = "1.0.1";

        var outputFilePath = "cryxie_libs/".concat(packageName).concat("@" + version).concat(".jar");

        new PomXmlModifier(packageName, version).add(outputFilePath);
    }

    @Test
    public void remove() {
        var packageName = "guga-reader-5";
        var version = "1.0.1";

        new PomXmlModifier(packageName, version).remove();
    }
}