package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.JarGenerator;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.IOException;
import java.net.ConnectException;

public class UploadPackageManager {
    private final UploadPackageContract uploadPackageContract;

    public UploadPackageManager(UploadPackageContract uploadPackageContract) {
        this.uploadPackageContract = uploadPackageContract;
    }

    public int sendPackage(PackageRequestDto packageRequestDto) {
        try {
            System.out.println("starting building project");
            uploadPackageContract.generateJar();

            System.out.println("jar was generated successfully");
            uploadPackageContract.obtainReadmePath();

            System.out.println("readme file was obtained");
            return uploadPackageContract.sendPackage(packageRequestDto).statusCode();
        } catch (ConnectException e) {
            throw new RuntimeException("can't send package looks live server is busy or off");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int sendNewVersion(
            NewVersionRequestDto newVersionRequestDto
    ) {
        try {
            System.out.println("starting building project");

            JarGenerator generator = new JarGenerator();
            generator.generateJar();
            System.out.println("jar was generated successfully");
            var jarFile = generator.getJarFile();

            return new PackageRequest().sendNewVersion(
                    newVersionRequestDto, jarFile).statusCode();
        } catch (ConnectException e) {
            throw new RuntimeException("can't send package looks live server is busy or off");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
