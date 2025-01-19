package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.IOException;

public class RestUtils {

    public final static String BASE_URL = "http://localhost:4010/cryxie/api/v1";

    public static int sendPackage(
            PackageRequestDto packageRequestDto,
            File readmeFile,
            File jarFile) throws IOException, InterruptedException {

        var response = new PackageRequest().
                send(packageRequestDto, readmeFile, jarFile);

        return response.statusCode();
    }

    public static int sendNewVersion(
            NewVersionRequestDto newVersionRequestDto,
            File jarFile) throws IOException, InterruptedException {

        var response = new PackageRequest().
                sendNewVersion(newVersionRequestDto, jarFile);

        return response.statusCode();
    }

}
