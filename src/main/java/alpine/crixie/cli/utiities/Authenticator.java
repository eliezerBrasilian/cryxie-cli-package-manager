package alpine.crixie.cli.utiities;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Authenticator {
    //logar usuario > obter o token e salvar

    public void login(String email, String pass) {
        try {
            String body = new JsonMapper<>()
                    .fromFields(Map.entry("email", email),
                            Map.entry("password", pass))
                    .fieldsToJson();

            var request = HttpRequest.newBuilder(new URI(RestUtils.BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            saveToken(response);
        } catch (ConnectException e) {
            throw new RuntimeException("Server is busy or is off, try again later");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveToken(HttpResponse<String> response) throws JsonProcessingException {
        switch (response.statusCode()) {
            case 200:
                System.out.println("logado");
                String body = response.body();
                ;

                record AuthResponseDto(String profile_picture, boolean is_membership, String token) {
                }

                var mapped = new JsonMapper<AuthResponseDto>()
                        .fromJsonToTarget(response.body(), AuthResponseDto.class);


                System.out.println(mapped.token);
                break;

            case 500:
                System.out.println("invalid credentials");
                break;
        }

    }

}
