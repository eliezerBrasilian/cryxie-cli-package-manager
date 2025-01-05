package alpine.crixie.cli.utiities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FileUtils {

    public static byte[] getBytesFromJarFile() throws IOException {
        var inputStream = FileUtils.class.getResourceAsStream("/alpine_central_email_file_manager.jar");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (inputStream == null) {
            throw new IllegalArgumentException("Arquivo não encontrado");
        }

        byte[] temp = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

}
