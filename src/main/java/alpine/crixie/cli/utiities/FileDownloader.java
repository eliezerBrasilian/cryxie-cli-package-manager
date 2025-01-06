package alpine.crixie.cli.utiities;

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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;

public class FileDownloader {
    private final String name;
    private String version = "null";

    public FileDownloader(String packageName) {
        name = packageName;
    }

    public FileDownloader(String packageName, String version) {
        this.name = packageName;
        this.version = version;
    }

    public record PasscodeRequest(String pass_code) {
    }

    public void retryDownload(String passcode) {

        var body = new PasscodeRequest(passcode);

        // Garantir que seja executado em uma thread separada
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    Altos.post("/cryxie/api/v1/package/download?name=" + name + "&version=" + version, body)
                            .bodyToMono(String.class) // Supondo que a resposta seja um JSON ou String
                            .doOnNext(response -> System.out.println("Download completed successfully: " + response))
                            .block(); // Bloqueia porque estamos fora do contexto reactor
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException("Error during download: " + e.getMessage(), e);
                }
            }).join(); // Bloqueia até a tarefa assíncrona terminar
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private Mono<Void> handleResponse(ClientResponse response) {
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
                            throw new RuntimeException("Erro ao processar a resposta: " + e.getMessage(), e);
                        }
                    })
                    .doOnNext(bytes -> {
                        try {
                            validateJar(bytes);
                            Files.createDirectories(Paths.get("cryxie_libs"));
                            writeFileFromBytes("cryxie_libs/" + name + ".jar", bytes);
                            System.out.println("Arquivo salvo com sucesso!");
                        } catch (Exception e) {
                            throw new RuntimeException("Erro ao salvar o arquivo: " + e.getMessage(), e);
                        }
                    })
                    .then();
        } else {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            System.out.println("Unauthorized. Please provide a valid passcode.");
                            return Mono.empty();
                        }
                        throw new RuntimeException("Error on fetching package: " + body);
                    });
        }
    }

    public void download1(PasswordCallback callback) {
        // Aumentar o limite de buffer para 10 MB
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();

        WebClient client = WebClient.builder()
                .exchangeStrategies(exchangeStrategies) // Aplicar a configuração do buffer
                .baseUrl("http://localhost:4010")
                .build();

        client.post()
                .uri("/cryxie/api/v1/package/download?name=".concat(name).concat("&version=").concat(version))
                .header("Content-Type", "application/json")
                .bodyValue("{\"pass_code\":\"null\"}")
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
                            throw new RuntimeException("Erro ao processar a resposta: " + e.getMessage(), e);
                        }
                    })
                    .doOnNext(bytes -> {
                        try {
                            validateJar(bytes);
                            Files.createDirectories(Paths.get("cryxie_libs"));
                            writeFileFromBytes("cryxie_libs/".concat(name).concat(".jar"), bytes);
                            System.out.println("Arquivo salvo com sucesso!");
                        } catch (Exception e) {
                            throw new RuntimeException("Erro ao salvar o arquivo: " + e.getMessage(), e);
                        }
                    })
                    .then();
        } else {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            callback.execute(name, version);
                            return Mono.empty();
                        }
                        throw new RuntimeException("Error on fetching package: " + body);
                    });
        }
    }


    private void validateJar(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
             ZipInputStream zis = new ZipInputStream(bais)) {
            if (zis.getNextEntry() == null) {
                throw new IOException("O arquivo baixado não é um JAR válido.");
            }
        }
    }


    void writeFileFromBytes(String outputFilePath, byte[] fileBytes) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(fileBytes);
        }
    }

    @Deprecated
    public void download() {
        try {
            var response = RestUtils.
                    get("http://localhost:4010/cryxie/api/v1/package/download?name=".concat(name).concat("&version=").concat(version));

            if (response.getResponseCode() == 200) {
                try (InputStream in = response.getInputStream()) {
                    Files.createDirectories(Paths.get("libs"));
                    Files.copy(in, Paths.get("cryxie_libs/".concat(name).concat(".jar")), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
