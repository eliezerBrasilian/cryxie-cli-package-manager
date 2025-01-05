package alpine.crixie.cli.utiities;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileDownloader {
    public void downloadByName(String name){
        try{
            var response = RestUtils.
                    get("http://localhost:4010/cryxie/api/v1/package/download?name=" + name);

            if(response.getResponseCode() == 200){
                try (InputStream in = response.getInputStream()) {
                    Files.createDirectories(Paths.get("libs"));
                    Files.copy(in, Paths.get("cryxie_libs/".concat(name).concat(".jar")), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Arquivo baixado com sucesso");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadByNameAndVersion(String jarFileName, String version){
        RestUtils.get("http://localhost:4010/cryxie/api/v1/package/download?name=" + jarFileName);
    }
}
