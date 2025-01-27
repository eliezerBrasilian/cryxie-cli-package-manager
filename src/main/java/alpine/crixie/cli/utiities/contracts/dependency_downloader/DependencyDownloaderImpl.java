package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.CryxieLibsDirectory;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.PomXmlModifier;
import alpine.crixie.cli.utiities.PromptPackageDownloader;

import java.io.FileNotFoundException;

public class DependencyDownloaderImpl implements DependencyDownloader {
    String pesquisa;

    String packageName;
    String version = "latest";

    public DependencyDownloaderImpl(String pesquisa) {
        this.pesquisa = pesquisa;
        splitNameVersion();
    }

    private void splitNameVersion() {
        packageName = pesquisa.trim();

        if (pesquisa.contains("@")) {
            packageName = pesquisa.split("@")[0].trim();
            version = pesquisa.split("@")[1];
        }
    }

    public void download() {
        new PromptPackageDownloader(packageName, version)
                .download();
    }

    @Override
    public void addToPomXmlFile() {
        var outputFilePath = "cryxie_libs/".concat(packageName).concat("@" + version).concat(".jar");

        new PomXmlModifier(packageName, version).
                add(outputFilePath);
    }

    @Override
    public void addToPackageLuaFile() {
        try {
            PackageLuaModifier modifier = new PackageLuaModifier();
            modifier.addDependency(packageName, version);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("was not possible to add dependency into package.lua");
        }
    }

    @Override
    public void removeFromPomXmlFile() {
        new PomXmlModifier(pesquisa)
                .remove();
    }

    @Override
    public void removePackageFromLuaFile() throws FileNotFoundException {
        PackageLuaModifier modifier = new PackageLuaModifier();

        modifier.removeDependency(packageName);
    }

    @Override
    public void removePackageFromCryxieLibsDirectory() throws FileNotFoundException {
        new CryxieLibsDirectory().
                jarFileName(pesquisa)
                .remove();
    }
}
