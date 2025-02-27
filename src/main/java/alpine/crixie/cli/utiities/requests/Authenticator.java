package alpine.crixie.cli.utiities.requests;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.Utils;
import alpine.crixie.cli.utiities.requests.dtos.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

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
                input.close();
                return;
            }

            System.out.println("please wait...");
            input.close();

            String body = new JsonMapper<>()
                    .fromFields(Map.entry("email", "alpinistamestre@yahoo.com"),
                            Map.entry("password", "12345678"))
                    .toJson();

            var request = HttpRequest.newBuilder(new URI(Utils.BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            processResponse(response);
        } catch (ConnectException e) {
            throw new RuntimeException("Server is busy or is off, try again later");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processResponse(HttpResponse<String> response) throws JsonProcessingException {
        String responseJson = response.body();
        int status = response.statusCode();

        if (Utils.StatusCode.fromCode(status).isOK()) {
            saveToken(response.body());
            return;
        } else if (Utils.StatusCode.fromCode(status).isExpectationFailed()) {
            System.out.println("account not found or password is invalid");
            return;
        } else {
            var responseMapped = new JsonMapper<BaseResponse<Object>>().fromJsonToTargetClass(responseJson,
                    new TypeReference<>() {
                    });
            throw new RuntimeException(responseMapped.message());
        }
    }

    private void saveToken(String body) throws JsonProcessingException {

        record AuthResponseDto(
                @JsonProperty("profile_picture") String profilePicture,
                @JsonProperty("token") String token,
                @JsonProperty("name") String name,
                @JsonProperty("user_id") String userId) {
        }

        var mapped = new JsonMapper<BaseResponse<AuthResponseDto>>()
                .fromJsonToTargetClass(body, new TypeReference<>() {
                });

        new LocalStorage().updateData(
                new LocalStorage.Data(mapped.data().token(), mapped.data().profilePicture(),
                        mapped.data().name(), mapped.data().userId()));

        System.out.println("Now you're logged in, try perform your operation again:)");

    }

}
