package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

public class Authenticator {
    public void login() {
        try {
            Scanner input = new Scanner(System.in);

            System.out.print("Please enter your email: ");
            String email = input.nextLine().trim();

            System.out.print("Please enter your password: ");
            String pass = input.nextLine().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                System.out.println("You have to enter your email and password created at cryxie.com :(");
                return;
            }
            System.out.println("please wait...");

            String body = new JsonMapper<>()
                    .fromFields(Map.entry("email", email),
                            Map.entry("password", pass))
                    .fieldsToJson();

            var request = HttpRequest.newBuilder(new URI(Utils.BASE_URL + "/auth/login"))
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

    public void saveToken(HttpResponse<String> response
    ) throws JsonProcessingException {
        switch (response.statusCode()) {
            case 200:
                String body = response.body();

                record AuthResponseDto(
                        String profile_picture,
                        boolean is_membership, String token, String name, String user_id) {
                }

                var mapped = new JsonMapper<AuthResponseDto>()
                        .fromJsonToTarget(body, AuthResponseDto.class);

                new LocalStorage().updateData(
                        new LocalStorage.Data(mapped.token, mapped.profile_picture,
                                mapped.name, mapped.user_id()));
                System.out.println("Now you're logged in, try perform your operation again:)");
                break;
            case 500:
                System.out.println("invalid credentials");
                break;
        }

    }

}
