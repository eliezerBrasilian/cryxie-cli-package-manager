package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.FileDownloader;

/**
 * The DependencyManager class provides functionality for managing project dependencies.
 * It allows for downloading, installing, and uninstalling dependencies,
 * as well as updating the associated configuration files (`pom.xml` and `package.lua`).
 */
public class DependencyManager {
    // Instance of DependencyDownloader used to handle dependency operations.
    private final DependencyDownloader dependencyDownloader;

    /**
     * Constructs a DependencyManager with the specified search term for the dependency.
     *
     * @param pesquisa The search term used to identify the dependency to manage.
     */
    public DependencyManager(String pesquisa) {
        this.dependencyDownloader = new DependencyDownloaderImpl(pesquisa);
    }

    /**
     * Installs the specified dependency by performing the following steps:
     * - Downloads the dependency.
     * - Adds the dependency to `pom.xml`.
     * - Adds the dependency to `package.lua`.
     *
     * @param callback A callback interface used to handle password input for the download process, if required.
     */
    public void install(FileDownloader.PasswordCallback callback) {
        try {
            System.out.println("Starting dependency download...");
            dependencyDownloader.download(callback);

            System.out.println("Adding dependency to pom.xml...");
            dependencyDownloader.addToPomXmlFile();

            System.out.println("Adding the dependency to package.lua...");
            dependencyDownloader.addToPackageLuaFile();

            System.out.println("Process completed successfully!");
        } catch (Exception e) {
            System.err.println("Error while installing package: " + e.getMessage());
        }
    }

    /**
     * Uninstalls the specified dependency by performing the following steps:
     * - Removes the dependency from `pom.xml`.
     * - Removes the dependency from `package.lua`.
     * - Deletes the associated JAR file from the `cryxie_libs` directory.
     */
    public void uninstall() {
        try {
            System.out.println("Removing from dependency to pom.xml...");
            dependencyDownloader.removeFromPomXmlFile();

            System.out.println("Removing dependency from package.lua...");
            dependencyDownloader.removePackageFromLuaFile();

            System.out.println("Removing dependency from cryxie_libs directory");
            dependencyDownloader.removePackageFromCryxieLibsDirectory();

            System.out.println("Package has been uninstalled successfully!");
        } catch (Exception e) {
            System.err.println("Error while uninstalling package: " + e.getMessage());
        }
    }
}
