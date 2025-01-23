package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageManager;
import alpine.crixie.cli.utiities.requests.Authenticator;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.util.ArrayList;

@CommandLine.Command(
        name = "upload",
        description = "Upload a specified package."
)
public class UploadJavaPackageCommand implements Runnable {

    @Override
    public void run() {
        try {
            var packageRequestDto = getPackageRequestDto();

            var manager = new UploadPackageManager(new UploadPackageImpl());

            String authorizationToken = new LocalStorage().getData().token();

            if (authorizationToken.isEmpty()) {
                new Authenticator()
                        .login();
            } else {
                int statusCode = manager.sendPackage(packageRequestDto);
                if (statusCode == 200) {
                    System.out.println("package registered successfully");
                }
                if (statusCode == 500) {
                    System.out.println("Already exists a package with this name: " + packageRequestDto.name());
                    System.out.println("If you want upload a new version use 'cryxie upload-new-version'");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static PackageRequestDto getPackageRequestDto() throws FileNotFoundException {
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

}
