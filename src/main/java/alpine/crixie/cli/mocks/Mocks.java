package alpine.crixie.cli.mocks;

import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    final static String userId = new LocalStorage().getData().userId();

    public static PackageRequestDto package2() {
        return new PackageRequestDto(
                "pacote2",
                "src.main.java.br.com.cryxie.Main",
                "pacote 2 com pacote 1 como dependencia",
                false,
                userId,
                "",
                new ArrayList<>(),
                "1.0.0",
                List.of(new PackageRequestDto.Dependency("pacote1", "1.0.1")),
                PackageRequestDto.Type.JAVA
        );
    }

    public static PackageRequestDto package1() throws FileNotFoundException {
        return new PackageRequestDto(
                "pacote1",
                "src.main.java.br.com.cryxie.Main",
                "pacote 1 sem dependencias",
                false,
                userId,
                "",
                new ArrayList<>(),
                "1.0.1",
                List.of(),
                PackageRequestDto.Type.JAVA
        );
    }
}
