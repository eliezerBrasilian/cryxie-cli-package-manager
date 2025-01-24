package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.PomXmlModifier;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

public class JavaPackageInstallerBase {
    private final String name;
    private String version = "latest";
    private String outputFilePath;
    private PackageRequest packageRequest = new PackageRequest();

    public JavaPackageInstallerBase(String packageName) {
        name = packageName;
        outputFilePath = "cryxie_libs/".concat(name).concat(".jar");
    }

    public JavaPackageInstallerBase(String packageName, String version) {
        this.name = packageName;
        this.version = version;
        if (!version.equals("latest")) {
            outputFilePath = "cryxie_libs/".concat(name).concat("@" + version).concat(".jar");
        } else {
            outputFilePath = "cryxie_libs/".concat(name).concat(".jar");
        }
    }

    public void download() {
        Set<String> downloadedPackages = new HashSet<>();
        try {
            downloadPackageRecursively(name, version, downloadedPackages);
        } catch (Exception e) {
            throw new RuntimeException("Error on downloading package: " + e.getMessage());
        }
    }

    public record PasscodeRequest(String pass_code) {
    }

    private void downloadPackageRecursively(String packageName,
                                            String version, Set<String> downloadedPackages)
            throws IOException, InterruptedException {

        String packageKey = packageName + "@" + version;
        if (downloadedPackages.contains(packageKey)) {
            System.out.println("Package already downloaded: " + packageKey);
            return;
        }

        HttpResponse<String> response = packageRequest.download(packageName, version);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error on downloading package " + packageName + ": " + response.body());
        }

        record BytesAndDeps(@JsonProperty("jarBytes") String jarBytesEncoded, List<PackageRequestDto.Dependency> deps) {
        }

        ObjectMapper objectMapper = new ObjectMapper();
        var responseMapped = objectMapper.readValue(response.body(), BytesAndDeps.class);

        String base64Encoded = responseMapped.jarBytesEncoded();
        var deps = responseMapped.deps();


        var bytes = Base64.getDecoder().decode(base64Encoded);
        validateJar(bytes);

        outputFilePath = "cryxie_libs/".concat(packageName).concat("@" + version).concat(".jar");
        Files.createDirectories(Paths.get("cryxie_libs"));
        writeFileFromBytes(bytes);

        addToPomXmlFile(packageName, version);

        downloadedPackages.add(packageKey);
        
        if (!deps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : deps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }

    }

    protected void validateJar(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
             ZipInputStream zis = new ZipInputStream(bais)) {
            if (zis.getNextEntry() == null) {
                throw new IOException("The downloaded file is not a valid JAR.");
            }
        }
    }

    protected void writeFileFromBytes(byte[] fileBytes) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(fileBytes);
        } catch (IOException e) {
            System.out.println("error on downloading: " + e);
        }
    }

    protected void addToPomXmlFile(String packageName, String version) {
        new PomXmlModifier(packageName, version).
                add();
    }


}
