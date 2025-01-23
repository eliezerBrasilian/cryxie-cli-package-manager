package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.RestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

class AuthenticatorTest {

    @Test
    void login() throws IOException, URISyntaxException, InterruptedException {
        var email = "alpinistamestre@yahoo.com";
        var pass = "12345";

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

        switch (response.statusCode()) {
            case 200:
                String body_ = response.body();

                record AuthResponseDto(
                        String profile_picture,
                        boolean is_membership, String token, String name, String user_id) {
                }

                var mapped = new JsonMapper<AuthResponseDto>()
                        .fromJsonToTarget(body_, AuthResponseDto.class);

                new LocalStorage().updateData(
                        new LocalStorage.Data(mapped.token, mapped.profile_picture,
                                mapped.name, mapped.user_id));
                System.out.println("Now you're logged in, try perform your operation again:)");
                break;
            case 500:
                System.out.println("invalid credentials");
                break;
        }
    }

    @Test
    void saveToken() {
    }
}