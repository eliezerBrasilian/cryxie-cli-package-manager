package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.FileDownloader.PasswordCallback;

import java.io.FileNotFoundException;

public interface DependencyDownloader {
    void download(PasswordCallback callback);

    void addToPomXmlFile();

    void addToPackageLuaFile();

    void removeFromPomXmlFile();

    void removePackageFromLuaFile() throws FileNotFoundException;

    void removePackageFromCryxieLibsDirectory() throws FileNotFoundException;
}

