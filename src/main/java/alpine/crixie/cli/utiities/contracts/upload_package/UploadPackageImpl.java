package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.JarGeneratorNewVersion;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadPackageImpl implements UploadPackageContract {
    private File jarFile;
    private File readmeFile;

    public UploadPackageImpl() {
        obtainReadmePath();
    }

    @Override
    public void generateJar() throws IOException, InterruptedException {
        var generator = new JarGeneratorNewVersion(new PackageLuaModifier());
        generator.generateJar();
        jarFile = generator.getJarFile();
    }

    public void linkJarFileToUpload(File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public void obtainReadmePath() {
        readmeFile = new File(System.getProperty("user.dir").concat(File.separator) + "README.md");
    }

    @Override
    public void sendPackage(PackageRequestDto packageRequestDto) throws IOException, InterruptedException {
        if (!readmeFile.exists()) {
            throw new FileNotFoundException("The README.md file was not found in the root directory.");
        }
        if (!jarFile.exists()) {
            throw new FileNotFoundException("The file with .jar extension was not found in the build folder.");
        }

        new PackageRequest().send(packageRequestDto, readmeFile, jarFile);
    }
}
