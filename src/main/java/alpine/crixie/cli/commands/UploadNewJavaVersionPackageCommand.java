package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageManager;
import alpine.crixie.cli.utiities.requests.Authenticator;
import alpine.crixie.cli.utiities.requests.dtos.NewVersionRequestDto;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import picocli.CommandLine;

import java.io.FileNotFoundException;

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

            String authorizationToken = new LocalStorage().getData().token();

            if (authorizationToken.isEmpty()) {
                new Authenticator()
                        .login();
            } else {
                int statusCode = manager.sendNewVersion(packageRequestDto);
                if (statusCode == 200) {
                    System.out.println("new version was added");
                }
                if (statusCode == 500) {
                    System.out.println("Already exists a version with this identifier: 0.0.1");
                    System.out.println("Did you try change the version in package.lua file?");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static NewVersionRequestDto getNewVersionRequestDto() throws FileNotFoundException {
        PackageLuaModifier packageLuaModifier = new PackageLuaModifier();

        String userId = new LocalStorage().getData().userId();

        var data = packageLuaModifier.getData();
        return new NewVersionRequestDto(
                data.name(),
                data.version(),
                data.deps(),
                userId,
                PackageRequestDto.Type.JAVA
        );
    }

}
