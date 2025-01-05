package alpine.crixie.cli.utiities.contracts.dependency_downloader;

public class DependencyManager {
    private final DependencyDownloader dependencyDownloader;;

    public DependencyManager(String pesquisa){
        this.dependencyDownloader = new DependencyDownloaderImpl(pesquisa);
    }

    public void execute(){
        try{
            System.out.println("Starting dependency download...");
            dependencyDownloader.download();

            System.out.println("Adding dependency to pom.xml...");
            dependencyDownloader.addToPomXmlFile();

            System.out.println("Adding the dependency to package.lua...");
            dependencyDownloader.addToPackageLuaFile();

            System.out.println("Process completed successfully!");
        } catch (Exception e) {
            System.err.println("Error while installing package: " + e.getMessage());
        }

    }
}
