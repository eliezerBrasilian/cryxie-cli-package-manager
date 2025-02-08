package alpine.crixie.cli.utiities.contracts;

import alpine.crixie.cli.utiities.SearchArrumer;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyRemoverContractImpl;
import org.junit.Test;

import java.io.IOException;

public class DependencyManagerTest {

    @Test
    public void uninstallDependency() throws IOException {
        var manager = new DependencyManager(new DependencyRemoverContractImpl(
                new SearchArrumer("guga-reader-5@1.0.1")
        ));
        manager.uninstallDependency();
    }

    @Test
    public void installDependencyFromPrompt() {
    }

    @Test
    public void installDependenciesFromLuaPackageFile() {
    }
}