package alpine.crixie.cli.utiities.requests.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.List;

class PackageRequestDtoTest {

    @Test
    void getJson() throws JsonProcessingException {
        PackageRequestDto packageRequestDto = new PackageRequestDto(
                "guga-reader-6",
                "src/main",
                "Descrição do meu pacote",
                true,
                "12345",
                "https://github.com/meurepositorio",
                List.of("java", "spring", "api"),
                "1.0.0",
                List.of(new PackageRequestDto.Dependency("spring-boot-starter", "2.7.0")),
                PackageRequestDto.Type.JAVA
        );
        //System.out.println(packageRequestDto.getJson());
    }
}