package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

import static alpine.crixie.cli.utiities.RestUtils.BASE_URL;


public class InstallAllPackages {
    private final String name;
    private String version = "latest";
    private String outputFilePath;

    public InstallAllPackages(String packageName, String version) {
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

        String packageUrl = String.format("%s/package/download?name=%s&version=%s", BASE_URL, packageName, version);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(packageUrl))
                .POST(HttpRequest.BodyPublishers.ofString(new JsonMapper<>(new PasscodeRequest("empty")).toJson()))
                .build();

        var httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error on downloading package " + packageName + ": " + response.body());
        }

        record BytesAndDeps(@JsonProperty("jarBytes") String jarBytesEncoded, List<PackageRequestDto.Dependency> deps) {
        }

        ObjectMapper objectMapper = new ObjectMapper();
        var responseMapped = objectMapper.readValue(response.body(), BytesAndDeps.class);

        String base64Encoded = responseMapped.jarBytesEncoded();
        var deps = responseMapped.deps();

        //BAIXA O JAR

        var bytes = Base64.getDecoder().decode(base64Encoded);
        validateJar(bytes);

        outputFilePath = "cryxie_libs/".concat(packageName).concat("@" + version).concat(".jar");
        Files.createDirectories(Paths.get("cryxie_libs"));
        writeFileFromBytes(bytes);

        addToPomXmlFile(packageName, version);

        downloadedPackages.add(packageKey);

        // Baixar as dependências recursivamente
        if (!deps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : deps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }

    }


    private void validateJar(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
             ZipInputStream zis = new ZipInputStream(bais)) {
            if (zis.getNextEntry() == null) {
                throw new IOException("The downloaded file is not a valid JAR.");
            }
        }
    }

    void writeFileFromBytes(byte[] fileBytes) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(fileBytes);
        } catch (IOException e) {
            System.out.println("error on downloading: " + e);
        }
    }

    public void addToPomXmlFile(String packageName, String version) {
        new PomXmlModifier(packageName, version).
                add(outputFilePath);
    }

}
