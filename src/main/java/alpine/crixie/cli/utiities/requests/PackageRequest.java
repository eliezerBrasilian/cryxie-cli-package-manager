package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.Utils;
import alpine.crixie.cli.utiities.requests.dtos.BaseResponse;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static alpine.crixie.cli.utiities.CustomMultipart.Part;
import static alpine.crixie.cli.utiities.CustomMultipart.ofMimeMultipartData;
import static alpine.crixie.cli.utiities.Utils.BASE_URL;

public class PackageRequest {
    final private String boundary = "----WebKitFormBoundary" + UUID.randomUUID();

    public void send(PackageRequestDto packageRequestDto,
            File readmeFile, File jarFile) throws IOException, InterruptedException {
        String packageJson = new JsonMapper<>(packageRequestDto).toJson();

        String bearerToken = new LocalStorage().getData().token();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/package"))
                .header("Authorization", "Bearer " + bearerToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(bodyWithMultipartData(readmeFile, jarFile, packageJson))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            processResponse(response);

        } catch (ConnectException e) {
            throw new RuntimeException("Error on uploading package, looks like the server is busy or off :(");
        }
    }

    public void sendNewVersion(NewVersionRequestDto newVersionRequestDto,
            File jarFile) throws IOException, InterruptedException {
        String url = BASE_URL + "/package/add-new-version";

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

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
    }

    private void processResponse(HttpResponse<String> response) throws JsonProcessingException {
        int statusCode = response.statusCode();

        var mappedResponse = new JsonMapper<BaseResponse<String>>()
                .fromJsonToTargetClass(response.body(), new TypeReference<>() {
                });

        if (Utils.StatusCode.fromCode(statusCode).equals(Utils.StatusCode.OK) ||
                Utils.StatusCode.fromCode(statusCode).equals(Utils.StatusCode.CREATED)) {
            System.out.println(mappedResponse.message());
        } else if (Utils.StatusCode.fromCode(statusCode).equals(Utils.StatusCode.FORBIDDEN)) {
            new Authenticator().login();
        } else {
            System.err.println(mappedResponse.data());
        }
    }

    public HttpResponse<String> download(String packageName, String version, String passCode) throws IOException {
        String packageUrl = String.format("%s/package/download?name=%s&version=%s", BASE_URL, packageName, version);

        var userId = new LocalStorage().getData().userId();

        var body = new JsonMapper<>().fromFields(
                Map.entry("pass_code", passCode),
                Map.entry("user_id", userId)).toJson();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(packageUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error on downloading package, looks like the server is busy or off :(");
        }

        return response;
    }

    private HttpRequest.BodyPublisher bodyWithMultipartData(File readmeFile, File jarFile, String packageJson)
            throws IOException {
        var parts = List.of(
                new Part("package", null, "application/json", packageJson.getBytes()),
                new Part("readme_file", readmeFile.getName(), "text/plain", Files.readAllBytes(readmeFile.toPath())),
                new Part("jar_file", jarFile.getName(), "application/java-archive",
                        Files.readAllBytes(jarFile.toPath())));

        return ofMimeMultipartData(parts, boundary);
    }

    private HttpRequest.BodyPublisher bodyWithMultipartData(File jarFile, String packageJson) throws IOException {
        var parts = List.of(
                new Part("version", null, "application/json", packageJson.getBytes()),
                new Part("jar_file", jarFile.getName(), "application/java-archive",
                        Files.readAllBytes(jarFile.toPath())));

        return ofMimeMultipartData(parts, boundary);
    }

}
