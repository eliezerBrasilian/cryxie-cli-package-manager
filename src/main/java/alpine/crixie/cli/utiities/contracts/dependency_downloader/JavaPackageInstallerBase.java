package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.PomXmlModifier;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.BaseResponse;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipInputStream;

public abstract class JavaPackageInstallerBase {
    protected final String name;
    protected String version = "latest";
    protected String outputFilePath;
    protected final PackageRequest packageRequest = new PackageRequest();

    public JavaPackageInstallerBase(String packageName) {
        name = packageName;
        outputFilePath = "cryxie_libs/".concat(name).concat(".jar");
    }

    public JavaPackageInstallerBase(String packageName, String version) {
        this.name = packageName;
        this.version = version;
        defineOutputFilePathForPackage(name, version);
    }

    public void download() {
        Set<String> downloadedPackages = new HashSet<>();
        try {
            downloadPackageRecursively(name, version, downloadedPackages);
            System.out.printf("Package %s was installed :)", name);
        } catch (IOException | InterruptedException | RuntimeException e) {
            System.err.print(e.getMessage());
        }
    }

    protected abstract void downloadPackageRecursively(
            String packageName, String version, Set<String> downloadedPackages)
            throws IOException, InterruptedException;

    protected Data performCommonOperationsAndGetDependencies(
            String packageName, String version, Set<String> downloadedPackages) throws IOException, InterruptedException {
        String packageKey = packageName + "@" + version;

        if (packageIsAlreadyInstalled(packageKey, downloadedPackages)) {
            System.out.println("Package already downloaded: " + packageKey);
            return null;
        }

        HttpResponse<String> response;
        int statusCode;

        var packageWasPrivate = false;

        var scanner = new Scanner(System.in);
        do {
            String passcode = "empty";
            if (packageWasPrivate) {
                System.out.print("Type the pass code: ");
                passcode = scanner.nextLine();
            }
            response = packageRequest.download(packageName, version, passcode);
            statusCode = response.statusCode();

            if (statusCode == 401) {
                packageWasPrivate = true;
            }

        } while (statusCode == 401);

        if (statusCode == 400) {
            throw new RuntimeException("Package doesn't exist in cryxie repositories");
        }

        if (statusCode != 200) {
            throw new RuntimeException("Error on downloading package " + packageName + ": " + response.body());
        }

        record BytesAndDeps(@JsonProperty("jarBytes") String jarBytesEncoded, List<PackageRequestDto.Dependency> deps,
                            String version) {
        }

        var responseMapped = new JsonMapper<BaseResponse<BytesAndDeps>>().fromJsonToTargetClass(response.body(), new TypeReference<>() {
        });
        var data = responseMapped.data();

        String base64Encoded = data.jarBytesEncoded();
        List<PackageRequestDto.Dependency> deps = responseMapped.data().deps();
        var correctVersion = responseMapped.data().version();

        var bytes = Base64.getDecoder().decode(base64Encoded);
        validateJar(bytes);

        defineOutputFilePathForPackage(packageName, correctVersion);

        Files.createDirectories(Paths.get("cryxie_libs"));
        writeFileFromBytes(bytes);

        addToPomXmlFile(packageName, correctVersion);

        downloadedPackages.add(packageKey);
        return new Data(correctVersion, deps);
    }

    public record Data(String correctVersion, List<PackageRequestDto.Dependency> childDeps) {
    }

    private boolean packageIsAlreadyInstalled(String packageKey, Set<String> downloadedPackages) {
        var exists = new File(outputFilePath).exists();

        return downloadedPackages.contains(packageKey) && exists;
    }

    private void defineOutputFilePathForPackage(String packageName, String version) {
        if (!version.equals("latest")) {
            outputFilePath = "cryxie_libs/".concat(packageName).concat("@" + version).concat(".jar");
        } else {
            outputFilePath = "cryxie_libs/".concat(packageName).concat(".jar");
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

    private void addToPomXmlFile(String packageName, String version) {
        new PomXmlModifier(packageName, version).
                add(outputFilePath);
    }

}
