package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.Utils;
import alpine.crixie.cli.utiities.contracts.upload_package.UploadPackageImpl;
import alpine.crixie.cli.utiities.requests.Authenticator;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static alpine.crixie.cli.utiities.Utils.getPackageData;

@CommandLine.Command(
        name = "upload",
        description = "upload java project to cryxie repositories"
)
public class UploadJavaPackageCommand implements Runnable {

    @Override
    public void run() {
        try {
            Path buildDir = Paths.get("build");
            List<Path> jarFiles = Files.walk(buildDir)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .toList();

            if (jarFiles.isEmpty()) {
                System.out.println("No .jar files found in the build directory.");
                return;
            }

            System.out.println("Select the .jar file to upload:");
            for (int i = 0; i < jarFiles.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, jarFiles.get(i).getFileName());
            }

            Scanner scanner = new Scanner(System.in);
            int choice = -1;

            while (choice < 1 || choice > jarFiles.size()) {
                System.out.print("Enter the number corresponding to your choice: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                } else {
                    scanner.next(); // Clear invalid input
                }
            }

            Path selectedJar = jarFiles.get(choice - 1);
            System.out.printf("You selected: %s%n", selectedJar.getFileName());

            uploadFile(selectedJar);

        } catch (IOException e) {
            System.err.println("Error reading the build directory: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void uploadFile(Path file) throws IOException, InterruptedException {
        System.out.printf("Uploading %s to Cryxie repositories...%n", file.getFileName());

        File jarFile = file.toFile();
        var uploadPackageImpl = new UploadPackageImpl();
        uploadPackageImpl.linkJarFileToUpload(jarFile);

        var packageRequestDto = getPackageData();
        var response = uploadPackageImpl.sendPackage(packageRequestDto);
        int statusCode = response.statusCode();

        if (Utils.StatusCode.fromCode(statusCode).equals(Utils.StatusCode.FORBIDDEN)) {
            new Authenticator().login();
        }

        /*
        else if (Utils.status200_OK(statusCode)) {
            System.out.println("upload completed successfully");
        } else if (statusCode == 404) {
            System.err.print(response.body());

        } else {
            System.err.println("Already exists a package with this name: " + packageRequestDto.name());
            System.err.println("If you want upload a new version use 'cryxie upload-new-version'");
            String body = response.body();

            ErrorResponseDto mapped = new JsonMapper<ErrorResponseDto>()
                    .fromJsonToTargetClass(body, ErrorResponseDto.class);


            throw new RuntimeException(mapped.message());
        }

         */

    }


}

