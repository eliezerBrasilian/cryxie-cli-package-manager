package alpine.crixie.cli.utiities.contracts;

import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.PromptPackageDownloader;
import alpine.crixie.cli.utiities.SearchArrumer;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyRemoverContract;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.JavaAllPackagesInstaller;
import alpine.crixie.cli.utiities.contracts.environment_loader.EnvironmentLoaderSetupImpl;
import alpine.crixie.cli.utiities.contracts.environment_loader.EnvironmentLoaderSetupManager;

import java.io.IOException;

public class DependencyManager {
    private final EnvironmentLoaderSetupManager environmentLoaderSetupManager = new EnvironmentLoaderSetupManager(new EnvironmentLoaderSetupImpl());
    private DependencyRemoverContract dependencyRemoverContract;
    private SearchArrumer searchArrumer;

    public DependencyManager() {
    }

    public DependencyManager(SearchArrumer arrumer) {
        this.searchArrumer = arrumer;
    }

    public DependencyManager(DependencyRemoverContract dependencyRemoverContract) {
        this.dependencyRemoverContract = dependencyRemoverContract;
    }

    public void uninstallDependency() throws IOException, InterruptedException {
        dependencyRemoverContract.removeFromPomXmlFile();
        dependencyRemoverContract.removePackageFromLuaFile();
        dependencyRemoverContract.removePackageFromCryxieLibsDirectory();
    }

    public void installDependencyFromPrompt() throws IOException {
        environmentLoaderSetupManager.load();

        new PromptPackageDownloader(
                searchArrumer.getPackageName(),
                searchArrumer.getVersion())
                .download();

    }

    public void installDependenciesFromLuaPackageFile() throws IOException {
        environmentLoaderSetupManager.load();
        var deps = new PackageLuaModifier().getData().deps();

        deps.forEach(dependency -> {
            new JavaAllPackagesInstaller(dependency.name(), dependency.version()).download();
        });
        System.out.println("deps installed successfully");
    }

}
