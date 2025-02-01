package alpine.crixie.cli.utiities.requests.dtos;

public record ErrorResponseDto(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}

