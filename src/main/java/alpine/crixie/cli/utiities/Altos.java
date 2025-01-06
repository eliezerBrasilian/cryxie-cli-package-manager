package alpine.crixie.cli.utiities;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

public class Altos {

    private static final WebClient CLIENT;

    static {
        // Configurações do WebClient
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(1000 * 1024 * 1024)) // Buffer de 10 MB
                .build();

        CLIENT = WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("http://localhost:4010") // Base URL fixa
                .build();
    }

    // Método para requisições GET
    public static WebClient.ResponseSpec get(String uri) {
        return CLIENT.get()
                .uri(uri)
                .retrieve(); // Lida automaticamente com o corpo da resposta
    }

    // Método para requisições POST com um corpo
    public static WebClient.ResponseSpec post(String uri, Object body) {
        return CLIENT.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve(); // Lida automaticamente com o corpo da resposta
    }

    // Método para requisições DELETE
    public static WebClient.ResponseSpec delete(String uri) {
        return CLIENT.delete()
                .uri(uri)
                .retrieve(); // Lida automaticamente com o corpo da resposta
    }

    // Método para requisições PUT com um corpo
    public static WebClient.ResponseSpec put(String uri, Object body) {
        return CLIENT.put()
                .uri(uri)
                .bodyValue(body)
                .retrieve(); // Lida automaticamente com o corpo da resposta
    }
}

