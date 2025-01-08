package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RestUtils {

    public final static String BASE_URL = "http://localhost:4010/cryxie/api/v1";

    public static int sendPackage(
            PackageRequestDto packageRequestDto,
            File readmeFile,
            File jarFile) throws IOException, InterruptedException {

        String boundary = "----WebKitFormBoundary" + UUID.randomUUID();

        var response = new PackageRequest(BASE_URL, boundary).
                send(packageRequestDto, readmeFile, jarFile);

        return response.statusCode();
    }

}
