package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.IOException;

public class UploadPackageManager {
    private final UploadPackageContract uploadPackageContract;

    public UploadPackageManager(UploadPackageContract uploadPackageContract) {
        this.uploadPackageContract = uploadPackageContract;
    }

    public int sendPackage(PackageRequestDto packageRequestDto) {
        try {
            uploadPackageContract.generateJar();

            System.out.println("jar was generated successfully");
            uploadPackageContract.obtainReadmePath();

            System.out.println("readme file was obtained");
            return uploadPackageContract.sendPackage(packageRequestDto);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
