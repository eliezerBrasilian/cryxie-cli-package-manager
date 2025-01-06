package alpine.crixie.cli.utiities;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class FileDownloaderTest {

    @Test
    void retryDownload() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();

        WebClient client = WebClient.builder()
                .exchangeStrategies(exchangeStrategies) // Aplicar a configuração do buffer
                .baseUrl("http://localhost:4010")
                .build();

        var passcode = "12345";
        var body = new FileDownloader.PasscodeRequest(passcode);

        var name = "pacote";
        var version = "1.1.1";

        client.post()
                .uri("/cryxie/api/v1/package/download?name=" + name + "&version=" + version)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .exchangeToMono((r) -> {
                    System.out.println(r.statusCode());
                    return Mono.empty();
                })
                .doOnError(error -> System.err.println("Error during download: " + error.getMessage()))
                .block(); // Executa a operação de forma assíncrona
    }
}