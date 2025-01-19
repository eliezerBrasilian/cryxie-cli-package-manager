package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;

import static alpine.crixie.cli.utiities.CustomMultipart.Part;
import static alpine.crixie.cli.utiities.CustomMultipart.ofMimeMultipartData;

public class PackageRequest {
    private final String baseUrl;
    final private String boundary;

    public PackageRequest(String baseUrl, String boundary) {
        this.baseUrl = baseUrl;
        this.boundary = boundary;
    }

    public HttpResponse<String> send(PackageRequestDto packageRequestDto,
                                     File readmeFile, File jarFile) throws IOException, InterruptedException {
        String url = baseUrl + "/package";

        String packageJson = new JsonMapper(packageRequestDto).toJson();

        String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidXN1YXJpb0B0ZXN0ZS5jb20iLCJleHAiOjE3MzU3NjQ3Mjd9._4DUkeelrlISjeapTsA5YBCU5RO-hpRVeEYwHLmoirQ";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(bodyWithMultipartData(readmeFile, jarFile, packageJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendNewVersion(NewVersionRequestDto newVersionRequestDto,
                                               File jarFile) throws IOException, InterruptedException {
        String url = baseUrl + "/package/add-new-version";

        String json = new JsonMapper(newVersionRequestDto).toJson();

        String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidXN1YXJpb0B0ZXN0ZS5jb20iLCJleHAiOjE3MzU3NjQ3Mjd9._4DUkeelrlISjeapTsA5YBCU5RO-hpRVeEYwHLmoirQ";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(bodyWithMultipartData(jarFile, json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private HttpRequest.BodyPublisher bodyWithMultipartData(File readmeFile, File jarFile, String packageJson) throws IOException {
        var parts = List.of(
                new Part("package", null, "application/json", packageJson.getBytes()),
                new Part("readme_file", readmeFile.getName(), "text/plain", Files.readAllBytes(readmeFile.toPath())),
                new Part("jar_file", jarFile.getName(), "application/java-archive", Files.readAllBytes(jarFile.toPath()))
        );

        return ofMimeMultipartData(parts, boundary);
    }

    private HttpRequest.BodyPublisher bodyWithMultipartData(File jarFile, String packageJson) throws IOException {
        var parts = List.of(
                new Part("version", null, "application/json", packageJson.getBytes()),
                new Part("jar_file", jarFile.getName(), "application/java-archive", Files.readAllBytes(jarFile.toPath()))
        );

        return ofMimeMultipartData(parts, boundary);
    }

}
