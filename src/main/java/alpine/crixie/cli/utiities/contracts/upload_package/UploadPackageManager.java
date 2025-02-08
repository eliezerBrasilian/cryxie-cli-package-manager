package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.JarSelector;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

public class UploadPackageManager {
    private final UploadPackageContract uploadPackageContract;


    public UploadPackageManager(UploadPackageContract uploadPackageContract) {
        this.uploadPackageContract = uploadPackageContract;
    }

    public void sendNewVersion(
            NewVersionRequestDto newVersionRequestDto
    ) {
        try {
            Optional<File> optionalJar = new JarSelector().select();

            optionalJar.ifPresent(file -> {
                try {
                    new PackageRequest().sendNewVersion(
                            newVersionRequestDto, file);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (ConnectException e) {
            throw new RuntimeException("can't send package looks like server is busy or off");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
