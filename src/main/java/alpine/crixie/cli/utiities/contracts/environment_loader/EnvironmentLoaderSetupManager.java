package alpine.crixie.cli.utiities.contracts.environment_loader;

import java.io.IOException;

public class EnvironmentLoaderSetupManager {
    private final EnvironmentLoaderSetupContract environmentLoaderSetupContract;

    public EnvironmentLoaderSetupManager(EnvironmentLoaderSetupContract environmentLoaderSetupContract) {
        this.environmentLoaderSetupContract = environmentLoaderSetupContract;
    }

    public void load() throws IOException {
        environmentLoaderSetupContract.loadVsCodeFolder();
        environmentLoaderSetupContract.loadIntellijIdeaCryxieLibraryFolder();
        environmentLoaderSetupContract.loadGitIgnoreFile();
    }
}
