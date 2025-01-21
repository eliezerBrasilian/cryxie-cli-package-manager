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
        PackageLuaModifier packageLuaModifier = PackageLuaModifier.getInstance();

        var pack = new PackageRequestDto(
                packageLuaModifier.getName(),
                packageLuaModifier.getDirectoryWhereMainFileIs(),
                packageLuaModifier.getDescription(),
                true,
                "12345",
                packageLuaModifier.getRepositoryUrl(),
                List.of(),
                packageLuaModifier.getVersion(),
                new ArrayList<>(),
                PackageRequestDto.Type.JAVA
        );
        System.out.println("Pack: " + pack);
        System.out.println("Dependencies: " + pack.dependencies());
        System.out.println(new JsonMapper(pack).toJson());
    }
}
