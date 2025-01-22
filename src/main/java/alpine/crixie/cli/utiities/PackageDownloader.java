package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;

import static alpine.crixie.cli.utiities.RestUtils.BASE_URL;

public class PackageDownloader {
    private final String name;
    private String version = "latest";
    private String outputFilePath;

    public PackageDownloader(String packageName) {
        name = packageName;
        outputFilePath = "cryxie_libs/".concat(name).concat(".jar");
    }

    public PackageDownloader(String packageName, String version) {
        this.name = packageName;
        this.version = version;
        if (!version.equals("latest")) {
            outputFilePath = "cryxie_libs/".concat(name).concat("@" + version).concat(".jar");
        } else {
            outputFilePath = "cryxie_libs/".concat(name).concat(".jar");
        }
    }

    public record PasscodeRequest(String pass_code) {
    }

    public void downloadPackage() {
        Set<String> downloadedPackages = new HashSet<>();
        try {
            downloadPackageRecursively(name, version, downloadedPackages);
        } catch (Exception e) {
            throw new RuntimeException("Error on downloading package: " + e.getMessage());
        }
    }

    private void downloadPackageRecursively(String packageName, String version, Set<String> downloadedPackages)
            throws IOException, InterruptedException {
        // Evitar redundância
        String packageKey = packageName + "@" + version;
        if (downloadedPackages.contains(packageKey)) {
            System.out.println("Pacote já baixado: " + packageKey);
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
            throw new RuntimeException("Erro ao obter o pacote " + packageName + ": " + response.body());
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

    void writeFileFromBytes(byte[] fileBytes) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(fileBytes);
        } catch (IOException e) {
            System.out.println("erro ao baixar: " + e);
        }
    }


    public void retryDownload(String passcode) {
        WebClient client = getWebFluxClient();

        var body = new PasscodeRequest(passcode);

        // Garantir que seja executado em uma thread separada
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    client.post()
                            .uri("/cryxie/api/v1/package/download?name=" + name + "&version=" + version)
                            .header("Content-Type", "application/json")
                            .bodyValue(body)
                            .exchangeToMono(response -> handleResponse(response, null))
                            .block(); // Aqui é permitido porque estamos fora do contexto reactor

                    return null;
                } catch (Exception e) {
                    throw new RuntimeException("Error during download: " + e.getMessage(), e);
                }
            }).join(); // Bloqueia até a tarefa assíncrona terminar
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static WebClient getWebFluxClient() {
        // Aumentar o limite de buffer para 10 MB
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("http://localhost:4010")
                .build();
    }

    public void download1(PasswordCallback callback) {
        // Aumentar o limite de buffer para 10 MB
        WebClient client = getWebFluxClient();

        var body = new PasscodeRequest("latest");

        client.post()
                .uri("/cryxie/api/v1/package/download?name=".concat(name).concat("&version=").concat(version))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .exchangeToMono(response -> handleResponse(response, callback))
                .block();
    }

    @FunctionalInterface
    public interface PasswordCallback {
        void execute(String name, String version);
    }

    private Mono<Void> handleResponse(ClientResponse response, PasswordCallback callback) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class)
                    .map(body -> {
                        try {
                            // Extrair os bytes do JSON
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> map = mapper.readValue(body, new TypeReference<>() {
                            });
                            String base64Encoded = (String) map.get("file_in_bytes");

                            // Decodificar a string Base64 para byte[]
                            return Base64.getDecoder().decode(base64Encoded);
                        } catch (Exception e) {
                            throw new RuntimeException("Error processing response: " + e.getMessage(), e);
                        }
                    })
                    .doOnNext(bytes -> {
                        try {
                            validateJar(bytes);
                            Files.createDirectories(Paths.get("cryxie_libs"));
                            writeFileFromBytes(bytes);
                        } catch (Exception e) {
                            throw new RuntimeException("Error saving package/dependency: " + e.getMessage(), e);
                        }
                    })
                    .then();
        } else {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            if (callback != null) {
                                callback.execute(name, version);
                            }
                            throw new RuntimeException("the passcode is invalid");
                        }
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            throw new RuntimeException("package not found");
                        }
                        throw new RuntimeException("Error on fetching package: " + body);
                    });
        }
    }


}
