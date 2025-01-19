package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageManager;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.util.List;

@CommandLine.Command(
        name = "upload-new-version",
        description = "Upload a specified package."
)
public class UploadNewJavaVersionPackageCommand implements Runnable {

    @Override
    public void run() {
        try {
            var packageRequestDto = getNewVersionRequestDto();

            var manager = new UploadPackageManager(new UploadPackageImpl());

            int statusCode = manager.sendNewVersion(packageRequestDto);
            if (statusCode == 200) {
                System.out.println("new version was added");
            }
            if (statusCode == 500) {
                System.out.println("Already exists a version with this identifier: 0.0.1");
                System.out.println("Did you try change the version in package.lua file?");
            }
            System.out.println(statusCode);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static NewVersionRequestDto getNewVersionRequestDto() throws FileNotFoundException {
        PackageLuaModifier packageLuaModifier = PackageLuaModifier.getInstance();

        return new NewVersionRequestDto(
                packageLuaModifier.getName(),
                packageLuaModifier.getVersion(),
                List.of(),
                "12345",
                PackageRequestDto.Type.JAVA
        );
    }

}
