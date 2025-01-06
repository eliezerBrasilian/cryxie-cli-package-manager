package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.FileDownloader.PasswordCallback;

public interface DependencyDownloader {
    void download(PasswordCallback callback);

    void addToPomXmlFile();

    void addToPackageLuaFile();
}

