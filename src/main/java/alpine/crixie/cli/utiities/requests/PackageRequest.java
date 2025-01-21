package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.RestUtils;
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
import java.util.UUID;

import static alpine.crixie.cli.utiities.CustomMultipart.Part;
import static alpine.crixie.cli.utiities.CustomMultipart.ofMimeMultipartData;

public class PackageRequest {
    final private String boundary = "----WebKitFormBoundary" + UUID.randomUUID();

    public HttpResponse<String> send(PackageRequestDto packageRequestDto,
                                     File readmeFile, File jarFile) throws IOException, InterruptedException {
        String packageJson = new JsonMapper<>(packageRequestDto).toJson();

        String bearerToken = new LocalStorage().getData().token();
        //String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoiYWxwaW5pc3RhbWVzdHJlQHlhaG9vLmNvbSIsImV4cCI6MTczNzQxNjA5OH0.Pe_SLTl2z9xVbUpDQQfZgaR7hWeoh-91sEkYW5i944g";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RestUtils.BASE_URL + "/package"))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(bodyWithMultipartData(readmeFile, jarFile, packageJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendNewVersion(NewVersionRequestDto newVersionRequestDto,
                                               File jarFile) throws IOException, InterruptedException {
        String url = RestUtils.BASE_URL + "/package/add-new-version";

        String json = new JsonMapper<>(newVersionRequestDto).toJson();

        String bearerToken = new LocalStorage().getData().token();

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
