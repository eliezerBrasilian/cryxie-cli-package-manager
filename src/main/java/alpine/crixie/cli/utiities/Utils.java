package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Utils {
    public final static String BASE_URL = "http://localhost:4010/cryxie/api/v1";
    public static final String LIB_PATH = "cryxie_libs";
    public static final String SRC_PATH = "src/main/java";

    public static PackageRequestDto getPackageData() throws FileNotFoundException {
        var data = new PackageLuaModifier().getData();
        String userId = new LocalStorage().getData().userId();

        return new PackageRequestDto(
                data.name(),
                data.directoryWhereMainFileIs(),
                data.description(),
                true,
                userId,
                data.repositoryUrl(),
                new ArrayList<>(),
                data.version(),
                data.deps(),
                PackageRequestDto.Type.JAVA
        );
    }

    public static boolean isUnauthorized(int statusCode) {
        return statusCode == 403;
    }

    public static boolean status200_OK(int statusCode) {
        return statusCode == 200;
    }

}
