package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.JarGenerator;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadPackageImpl implements UploadPackageContract {
    private File jarFile;
    private File readmeFile;

    @Override
    public void generateJar() throws IOException {
        JarGenerator generator = new JarGenerator();
        generator.generateJar();
        jarFile = generator.getJarFile();
    }

    @Override
    public void obtainReadmePath() {
        readmeFile = new File(System.getProperty("user.dir") + "/README.md");
    }

    @Override
    public int sendPackage(PackageRequestDto packageRequestDto) throws IOException, InterruptedException {
        if (!readmeFile.exists()) {
            throw new FileNotFoundException("The README.md file was not found in the root directory.");
        }
        if (!jarFile.exists()) {
            throw new FileNotFoundException("The file with .jar extension was not found in the builds folder.");
        }

        return new PackageRequest().send(packageRequestDto, readmeFile, jarFile).statusCode();
    }
}
