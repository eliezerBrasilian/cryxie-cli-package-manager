package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.LocalStorage;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageManager;
import alpine.crixie.cli.utiities.requests.Authenticator;
import picocli.CommandLine;

import java.io.FileNotFoundException;

import static alpine.crixie.cli.utiities.Utils.getPackageData;

@CommandLine.Command(
        name = "build-and-upload",
        description = "build and upload automatically package created from project"
)
public class BuildAndUploadJavaPackageCommand implements Runnable {

    @Override
    public void run() {
        try {
            var packageRequestDto = getPackageData();

            var manager = new UploadPackageManager(new UploadPackageImpl());

            String authorizationToken = new LocalStorage().getData().token();

            if (authorizationToken.isEmpty()) {
                new Authenticator()
                        .login();
            } else {
                int statusCode = manager.sendPackage(packageRequestDto);
                if (statusCode == 200) {
                    System.err.println("package registered successfully");
                }
                if (statusCode == 500) {
                    System.err.println("Already exists a package with this name: " + packageRequestDto.name());
                    System.err.println("If you want upload a new version use 'cryxie upload-new-version'");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
