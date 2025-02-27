package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JarSelector;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import static alpine.crixie.cli.utiities.Utils.getPackageData;

@CommandLine.Command(name = "deploy", description = "deploy java package to cryxie repositories")
public class UploadJavaPackageCommand implements Runnable {

    @Override
    public void run() {
        try {
            Optional<File> optionalJar = new JarSelector().select();
            if (optionalJar.isPresent()) {
                uploadFile(optionalJar.get());
            }
        } catch (IOException e) {
            System.err.println("Error reading the build directory: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void uploadFile(File jarFile) throws IOException, InterruptedException {
        System.out.printf("Uploading %s to Cryxie repositories...%n", jarFile.getName());

        var uploadPackageImpl = new UploadPackageImpl();
        uploadPackageImpl.linkJarFileToUpload(jarFile);

        var packageRequestDto = getPackageData();

        var s = new Scanner(System.in);
        var passcode = "";

        if (packageRequestDto.getVisibility().equals(PackageRequestDto.Visibility.PRIVATE)) {
            do {
                System.out.println("type a passcode to use in your package: ");
                passcode = s.nextLine();

            } while (passcode.isBlank());
        }

        s.close();

        packageRequestDto.setAccessToken(passcode);

        uploadPackageImpl.sendPackage(packageRequestDto);
    }
}
