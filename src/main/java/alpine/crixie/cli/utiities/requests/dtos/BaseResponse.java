package alpine.crixie.cli.utiities.requests.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BaseResponse<T>(
        @JsonProperty("message") String message,
        @JsonProperty("data") T data
) {
}
