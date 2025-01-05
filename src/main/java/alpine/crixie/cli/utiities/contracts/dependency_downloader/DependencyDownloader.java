package alpine.crixie.cli.utiities.contracts.dependency_downloader;

public interface DependencyDownloader {
    void download();
    void addToPomXmlFile();
    void addToPackageLuaFile();
}

