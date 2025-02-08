package alpine.crixie.cli.utiities.contracts.environment_loader;

import java.io.IOException;

public interface EnvironmentLoaderSetupContract {
    void loadVsCodeFolder();

    void loadGitIgnoreFile() throws IOException;

    void loadIntellijIdeaCryxieLibraryFolder();
}
