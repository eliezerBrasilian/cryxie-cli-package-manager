package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import java.io.FileNotFoundException;

public interface DependencyDownloader {
    void download();

    void addToPomXmlFile();

    void addToPackageLuaFile();

    void removeFromPomXmlFile();

    void removePackageFromLuaFile() throws FileNotFoundException;

    void removePackageFromCryxieLibsDirectory() throws FileNotFoundException;
}

