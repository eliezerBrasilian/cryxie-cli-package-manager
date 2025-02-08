package alpine.crixie.cli.utiities;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CryxieLibsDirectoryTest {

    @Test
    public void jarFileName() {
    }

    @Test
    public void findJarWithName() throws IOException {

        Path buildDir = Paths.get("cryxie_libs");
        List<Path> jarFiles = Files.walk(buildDir)
                .filter(path -> path.toString().endsWith(".jar"))
                .toList();

        var foundedPathList = jarFiles.stream().filter(jar -> jar.toString().contains("gson")).toList();
        //System.out.println(foundedPathList);

        var foundedList = new ArrayList<String>();

        for (Path path : foundedPathList) {
            var pathString = path.toString();

            var novoNome = pathString.replace(File.separator, "/");

            var splited = novoNome.split("/");

            if (splited[1].startsWith("gson"))
                foundedList.add(splited[1]);
        }

        System.out.println(foundedList);
    }

    @Test
    public void remove() throws IOException {
        new CryxieLibsDirectory()
                .jarFileName("gson")
                .remove();
    }
}