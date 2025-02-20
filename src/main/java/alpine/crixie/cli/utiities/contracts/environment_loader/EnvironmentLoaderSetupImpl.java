package alpine.crixie.cli.utiities.contracts.environment_loader;

import alpine.crixie.cli.utiities.GitIgnoreGenerator;
import alpine.crixie.cli.utiities.IntellijCryxieXmlManager;
import alpine.crixie.cli.utiities.ReadmeGenerator;
import alpine.crixie.cli.utiities.VSCodeSettingsManager;

import java.io.IOException;

public class EnvironmentLoaderSetupImpl implements EnvironmentLoaderSetupContract {
    @Override
    public void loadVsCodeFolder() {
        new VSCodeSettingsManager();
    }

    @Override
    public void loadGitIgnoreFile() throws IOException {
        new IntellijCryxieXmlManager();
    }

    @Override
    public void loadIntellijIdeaCryxieLibraryFolder() {
        new GitIgnoreGenerator().generate();
    }

    @Override
    public void loadReadmeFile() {
        new ReadmeGenerator().generate();
    }
}
