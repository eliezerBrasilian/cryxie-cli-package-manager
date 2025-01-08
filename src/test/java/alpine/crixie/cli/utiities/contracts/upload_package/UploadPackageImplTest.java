package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class UploadPackageImplTest {

    @Test
    void generateJar() {
    }

    @Test
    void obtainJarPath() {
    }

    @Test
    void obtainReadmePath() {
    }

    @Test
    void sendPackage() throws IOException, InterruptedException {

        var packageRequestDto = new PackageRequestDto(
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

        new UploadPackageImpl()
                .generateJar();

        //RestUtils.sendPackage(packageRequestDto,readmeFile,jarFile);
    }
}