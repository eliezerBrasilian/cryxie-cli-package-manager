package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JsonMapper;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UploadJavaPackageCommandTest {
    @Test
    void uploadPackage() {

    }

    @Test
    void getJson() throws FileNotFoundException, JsonProcessingException {
        PackageLuaModifier packageLuaModifier = new PackageLuaModifier();

        var data = packageLuaModifier.getData();
        var pack = new PackageRequestDto(
                data.name(),
                data.directoryWhereMainFileIs(),
                data.description(),
                false,
                "12345",
                data.repositoryUrl(),
                List.of(),
                data.version(),
                new ArrayList<>(),
                PackageRequestDto.Type.JAVA
        );
        System.out.println("Pack: " + pack);
        System.out.println("Dependencies: " + pack.dependencies());
        System.out.println(new JsonMapper(pack).toJson());
    }
}
