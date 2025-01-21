package alpine.crixie.cli.mocks;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.util.ArrayList;
import java.util.List;

public class Mocks {
    
    public static PackageRequestDto package2() {
        return new PackageRequestDto(
                "pacote2",
                "src.main.java.br.com.cryxie.Main",
                "pacote 2 com pacote 1 como dependencia",
                false,
                "12345",
                "",
                new ArrayList<>(),
                "1.0.0",
                List.of(new PackageRequestDto.Dependency("pacote1", "1.0.1")),
                PackageRequestDto.Type.JAVA
        );
    }

    public static PackageRequestDto package1() {
        return new PackageRequestDto(
                "pacote1",
                "src.main.java.br.com.cryxie.Main",
                "pacote 1 sem dependencias",
                false,
                "12345",
                "",
                new ArrayList<>(),
                "1.0.1",
                new ArrayList<>(),
                PackageRequestDto.Type.JAVA
        );
    }
}
