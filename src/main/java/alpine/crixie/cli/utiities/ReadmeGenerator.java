package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReadmeGenerator {
    public void generate() {
        var gitIgnoreFile = new File(System.getProperty("user.dir"), "README.md");

        try {
            if (!gitIgnoreFile.exists()) {
                if (gitIgnoreFile.createNewFile()) {
                    try (FileWriter writer = new FileWriter(gitIgnoreFile)) {

                        String defaultContent = """
                                # Yeah, this project was created using Cryxie CLI
                                    Thanks for joining us 😁
                                """;

                        writer.write(defaultContent);

                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on creating README.MD file: " + e.getMessage());
        }

    }
}
