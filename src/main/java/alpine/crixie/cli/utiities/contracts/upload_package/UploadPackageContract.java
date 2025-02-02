package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpResponse;

public interface UploadPackageContract {
    void generateJar() throws FileNotFoundException, IOException, InterruptedException;

    void obtainReadmePath();

    HttpResponse<String> sendPackage(PackageRequestDto packageRequestDto) throws IOException, InterruptedException;
}
