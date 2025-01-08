package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.utiities.JarGenerator;
import alpine.crixie.cli.utiities.RestUtils;
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
            throw new FileNotFoundException("O arquivo README.md não foi encontrado no diretório raiz.");
        }
        if (!jarFile.exists()) {
            throw new FileNotFoundException("O arquivo mainclass-generator.jar não foi encontrado na pasta builds.");
        }

        return RestUtils.sendPackage(packageRequestDto, readmeFile, jarFile);
    }
}
